package io.jonathanlee.switchyard_api.services.impl;

import static org.assertj.core.api.Assertions.assertThat;

import io.jonathanlee.switchyard_api.TestcontainersConfiguration;
import io.jonathanlee.switchyard_api.config.CacheConfig;
import io.jonathanlee.switchyard_api.domain.dtos.AudienceDto;
import io.jonathanlee.switchyard_api.domain.dtos.FlagDto;
import io.jonathanlee.switchyard_api.domain.dtos.request.CreateAudienceRequestDto;
import io.jonathanlee.switchyard_api.domain.dtos.request.CreateFlagRequestDto;
import io.jonathanlee.switchyard_api.services.AudiencesAdminService;
import io.jonathanlee.switchyard_api.services.FlagsAdminService;
import io.jonathanlee.switchyard_api.services.FlagsService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.cache.RedisCacheManager;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ServiceCachingTests {

  @Autowired private CacheManager cacheManager;
  @Autowired private FlagsService flagsService;
  @Autowired private FlagsAdminService flagsAdminService;
  @Autowired private AudiencesAdminService audiencesAdminService;

  private static String shortId() {
    return UUID.randomUUID().toString().substring(0, 8);
  }

  @Test
  void cacheManagerIsBackedByRedis() {
    assertThat(cacheManager).isInstanceOf(RedisCacheManager.class);
  }

  @Test
  void flagReadsAreCachedUnderVersionedKeysAndEvictedOnWrite() {
    Cache flags = cacheManager.getCache(CacheConfig.FLAGS);
    String key = "flag-" + shortId();
    FlagDto created =
        flagsAdminService
            .createFlag(CreateFlagRequestDto.builder().key(key).enabled(true).build())
            .orElseThrow();

    flagsService.getFlags();
    flagsAdminService.getFlagById(created.getId());
    flagsAdminService.getFlagByKey(key);

    assertThat(flags.get(CacheConfig.VERSION + ":all")).isNotNull();
    assertThat(flags.get(CacheConfig.VERSION + ":id:" + created.getId(), FlagDto.class))
        .isEqualTo(created);
    assertThat(flags.get(CacheConfig.VERSION + ":key:" + key, FlagDto.class)).isEqualTo(created);

    flagsAdminService.updateFlagByKey(
        key, CreateFlagRequestDto.builder().key(key).enabled(false).build());

    assertThat(flags.get(CacheConfig.VERSION + ":all")).isNull();
    assertThat(flags.get(CacheConfig.VERSION + ":id:" + created.getId())).isNull();
    assertThat(flags.get(CacheConfig.VERSION + ":key:" + key)).isNull();

    assertThat(flagsAdminService.getFlagByKey(key).orElseThrow().isEnabled()).isFalse();
    flagsAdminService.deleteFlagById(created.getId());
    assertThat(flags.get(CacheConfig.VERSION + ":key:" + key)).isNull();
  }

  @Test
  void missesAreNotCached() {
    Cache flags = cacheManager.getCache(CacheConfig.FLAGS);
    String key = "missing-" + shortId();

    assertThat(flagsAdminService.getFlagByKey(key)).isEmpty();
    assertThat(flags.get(CacheConfig.VERSION + ":key:" + key)).isNull();

    flagsAdminService.createFlag(CreateFlagRequestDto.builder().key(key).enabled(true).build());
    assertThat(flagsAdminService.getFlagByKey(key)).isPresent();
    flagsAdminService.deleteFlagByKey(key);
  }

  @Test
  void audienceReadsAreCachedUnderVersionedKeysAndEvictedOnWrite() {
    Cache audiences = cacheManager.getCache(CacheConfig.AUDIENCES);
    String key = "aud-" + shortId();
    AudienceDto created =
        audiencesAdminService
            .createAudience(CreateAudienceRequestDto.builder().key(key).query("plan = 'pro'").build())
            .orElseThrow();

    audiencesAdminService.getAudiences();
    audiencesAdminService.getAudienceById(created.getId());
    audiencesAdminService.getAudienceByKey(key);

    assertThat(audiences.get(CacheConfig.VERSION + ":all")).isNotNull();
    assertThat(audiences.get(CacheConfig.VERSION + ":id:" + created.getId(), AudienceDto.class))
        .isEqualTo(created);
    assertThat(audiences.get(CacheConfig.VERSION + ":key:" + key, AudienceDto.class))
        .isEqualTo(created);

    audiencesAdminService.deleteAudienceById(created.getId());

    assertThat(audiences.get(CacheConfig.VERSION + ":all")).isNull();
    assertThat(audiences.get(CacheConfig.VERSION + ":id:" + created.getId())).isNull();
    assertThat(audiences.get(CacheConfig.VERSION + ":key:" + key)).isNull();
  }
}
