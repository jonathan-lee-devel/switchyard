package io.jonathanlee.switchyard_api.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import io.jonathanlee.switchyard_api.TestcontainersConfiguration;
import io.jonathanlee.switchyard_api.domain.models.Flag;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class FlagsMapperTests {

  @Autowired private FlagsMapper flagsMapper;

  @Test
  void roundTripsAFlagThroughEveryMapperMethod() {
    String key = "flag-" + UUID.randomUUID().toString().substring(0, 8);

    Optional<Flag> inserted = flagsMapper.insert(Flag.builder().key(key).enabled(true).build());
    assertThat(inserted).isPresent();
    Flag flag = inserted.get();
    assertThat(flag.getId()).isNotNull();
    assertThat(flag.getKey()).isEqualTo(key);
    assertThat(flag.isEnabled()).isTrue();
    assertThat(flag.getCreatedAt()).isNotNull();
    assertThat(flag.getUpdatedAt()).isNotNull();

    assertThat(flagsMapper.findById(flag.getId())).contains(flag);
    assertThat(flagsMapper.findByKey(key)).contains(flag);
    assertThat(flagsMapper.findAll()).contains(flag);

    Optional<Flag> updatedById =
        flagsMapper.updateById(flag.getId(), Flag.builder().key(key).enabled(false).build());
    assertThat(updatedById).isPresent();
    assertThat(updatedById.get().isEnabled()).isFalse();
    assertThat(updatedById.get().getUpdatedAt()).isAfterOrEqualTo(flag.getUpdatedAt());

    String renamed = key + "-renamed";
    Optional<Flag> updatedByKey =
        flagsMapper.updateByKey(key, Flag.builder().key(renamed).enabled(true).build());
    assertThat(updatedByKey).isPresent();
    assertThat(updatedByKey.get().getKey()).isEqualTo(renamed);
    assertThat(flagsMapper.findByKey(key)).isEmpty();

    Optional<Flag> deleted = flagsMapper.deleteByKey(renamed);
    assertThat(deleted).isPresent();
    assertThat(deleted.get().getId()).isEqualTo(flag.getId());
    assertThat(flagsMapper.findById(flag.getId())).isEmpty();
    assertThat(flagsMapper.deleteById(flag.getId())).isEmpty();
  }
}
