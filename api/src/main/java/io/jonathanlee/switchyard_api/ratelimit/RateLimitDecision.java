package io.jonathanlee.switchyard_api.ratelimit;

/**
 * Outcome of a single rate-limit check.
 *
 * @param allowed whether the request may proceed
 * @param limit the configured maximum for the window
 * @param remaining requests left in the current window, never negative
 * @param retryAfterSeconds seconds until the window resets, rounded up; at least 1
 */
public record RateLimitDecision(boolean allowed, int limit, long remaining, long retryAfterSeconds) {

  static RateLimitDecision unlimited(int limit) {
    return new RateLimitDecision(true, limit, limit, 0);
  }
}
