package io.jonathanlee.switchyard_api.config;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Enables Spring's cache abstraction and centralises cache names and the key version.
 *
 * <p>Every cache key is prefixed with {@link #VERSION}. Bump it whenever the shape of a cached DTO
 * changes so that entries written by an older deployment are ignored rather than deserialised into
 * an incompatible class.
 */
@Configuration
@EnableCaching
public class CacheConfig {

  public static final String VERSION = "v1";

  public static final String FLAGS = "flags";
  public static final String AUDIENCES = "audiences";

  public static final String ALL_KEY = "'" + VERSION + ":all'";
  public static final String ID_KEY = "'" + VERSION + ":id:' + #id";
  public static final String KEY_KEY = "'" + VERSION + ":key:' + #key";

  /** Skips caching when the lookup found nothing, so a later create is visible immediately. */
  public static final String UNLESS_EMPTY = "#result == null";

  /**
   * Spring Data Redis 4 performs cache writes and clears asynchronously by default when the
   * connection factory is reactive, which Lettuce is. That lets a read issued right after a {@code
   * @CacheEvict(allEntries = true)} still observe the stale entry. Immediate writes make eviction
   * complete before the service method returns. SCAN replaces KEYS so clearing never blocks Redis.
   */
  @Bean
  RedisCacheManagerBuilderCustomizer immediateWritesCacheWriter(
      RedisConnectionFactory connectionFactory) {
    return builder ->
        builder.cacheWriter(
            RedisCacheWriter.create(
                connectionFactory,
                config -> config.immediateWrites(true).batchStrategy(BatchStrategies.scan(100))));
  }
}
