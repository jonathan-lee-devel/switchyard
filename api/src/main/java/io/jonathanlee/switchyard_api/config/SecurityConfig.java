package io.jonathanlee.switchyard_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Stateless JWT bearer-token security.
 *
 * <ul>
 *   <li>{@code GET /v1/flags/**} is public so SDKs can read flags and subscribe to the SSE stream
 *       without credentials.
 *   <li>{@code /v1/admin/**} requires a valid JWT carrying {@code ADMIN} in its {@value
 *       #ROLES_CLAIM} claim.
 *   <li>Everything else requires a valid JWT.
 * </ul>
 *
 * <p>The JWT issuer is configured via {@code spring.security.oauth2.resourceserver.jwt.issuer-uri}.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  public static final String ROLES_CLAIM = "roles";
  public static final String ADMIN_ROLE = "ADMIN";

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
    return http.csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.GET, "/v1/flags/**")
                    .permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                    .permitAll()
                    .requestMatchers("/v1/admin/**")
                    .hasRole(ADMIN_ROLE)
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
        .build();
  }

  /** Maps each entry of the {@value #ROLES_CLAIM} claim to a {@code ROLE_}-prefixed authority. */
  @Bean
  JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter() {
    JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
    authoritiesConverter.setAuthoritiesClaimName(ROLES_CLAIM);
    authoritiesConverter.setAuthorityPrefix("ROLE_");
    return authoritiesConverter;
  }

  @Bean
  JwtAuthenticationConverter jwtAuthenticationConverter(
      JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter) {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
    return converter;
  }
}
