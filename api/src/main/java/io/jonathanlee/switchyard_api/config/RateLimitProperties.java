package io.jonathanlee.switchyard_api.config;

import jakarta.validation.constraints.Positive;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/**
 * Limits for the public, unauthenticated flag endpoints.
 *
 * @param limit maximum requests a single client IP may make per {@code window}
 * @param window length of the fixed window after which the client's count resets
 */
@Validated
@ConfigurationProperties("switchyard.rate-limit.public-flags")
public record RateLimitProperties(
    @Positive @DefaultValue("120") int limit, @DefaultValue("1m") Duration window) {}
