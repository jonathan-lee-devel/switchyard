package io.jonathanlee.switchyard_api.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Applies {@link RedisRateLimiter} to the public, unauthenticated flag endpoints: {@code GET
 * /v1/flags} and anything beneath it, including the SSE stream.
 *
 * <p>Clients are identified by remote address. With {@code server.forward-headers-strategy=native}
 * Tomcat resolves that from {@code X-Forwarded-For} when the request arrived via a trusted proxy,
 * so the application never has to trust that header directly.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@RequiredArgsConstructor
public class PublicFlagsRateLimitFilter extends OncePerRequestFilter {

  static final String PUBLIC_FLAGS_PATH = "/v1/flags";
  static final String LIMIT_HEADER = "X-RateLimit-Limit";
  static final String REMAINING_HEADER = "X-RateLimit-Remaining";
  static final String RETRY_AFTER_HEADER = "Retry-After";

  private final RedisRateLimiter rateLimiter;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    if (!HttpMethod.GET.matches(request.getMethod())) {
      return true;
    }
    String path = request.getRequestURI().substring(request.getContextPath().length());
    return !(path.equals(PUBLIC_FLAGS_PATH) || path.startsWith(PUBLIC_FLAGS_PATH + "/"));
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    RateLimitDecision decision = rateLimiter.check(request.getRemoteAddr());

    response.setHeader(LIMIT_HEADER, Integer.toString(decision.limit()));
    response.setHeader(REMAINING_HEADER, Long.toString(decision.remaining()));

    if (decision.allowed()) {
      filterChain.doFilter(request, response);
      return;
    }

    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    response.setHeader(RETRY_AFTER_HEADER, Long.toString(decision.retryAfterSeconds()));
    response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response
        .getWriter()
        .write(
            """
            {"type":"about:blank","title":"Too Many Requests","status":429,\
            "detail":"Rate limit of %d requests per window exceeded. Retry after %d seconds.",\
            "instance":"%s"}"""
                .formatted(decision.limit(), decision.retryAfterSeconds(), request.getRequestURI()));
  }
}
