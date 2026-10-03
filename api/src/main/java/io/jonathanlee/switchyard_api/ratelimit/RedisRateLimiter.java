package io.jonathanlee.switchyard_api.ratelimit;

import io.jonathanlee.switchyard_api.config.RateLimitProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

/**
 * Fixed-window counter stored in Redis so limits are shared across API instances.
 *
 * <p>Each check atomically increments the client's counter and, on the first hit of a window,
 * starts the expiry. If Redis is unreachable the limiter fails open: a public read endpoint should
 * stay available even when the limiter's backing store is not.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisRateLimiter {

  static final String KEY_PREFIX = "ratelimit:public-flags:";

  @SuppressWarnings("rawtypes")
  private static final RedisScript<List> INCREMENT_AND_EXPIRE =
      new DefaultRedisScript<>(
          """
          local count = redis.call('INCR', KEYS[1])
          if count == 1 then
            redis.call('PEXPIRE', KEYS[1], ARGV[1])
          end
          local ttl = redis.call('PTTL', KEYS[1])
          return {count, ttl}
          """,
          List.class);

  private final StringRedisTemplate redisTemplate;
  private final RateLimitProperties properties;

  public RateLimitDecision check(String clientId) {
    int limit = properties.limit();
    long windowMillis = properties.window().toMillis();

    List<?> result;
    try {
      result =
          redisTemplate.execute(
              INCREMENT_AND_EXPIRE, List.of(KEY_PREFIX + clientId), Long.toString(windowMillis));
    } catch (DataAccessException e) {
      log.warn("Rate limiter unavailable, allowing request for {}: {}", clientId, e.getMessage());
      return RateLimitDecision.unlimited(limit);
    }

    if (result == null || result.size() < 2) {
      log.warn("Rate limiter returned unexpected result {}, allowing request", result);
      return RateLimitDecision.unlimited(limit);
    }

    long count = ((Number) result.get(0)).longValue();
    long ttlMillis = ((Number) result.get(1)).longValue();
    long retryAfterSeconds = Math.max(1, (ttlMillis + 999) / 1000);
    return new RateLimitDecision(
        count <= limit, limit, Math.max(0, limit - count), retryAfterSeconds);
  }
}
