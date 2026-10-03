package io.jonathanlee.switchyard_api.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import io.jonathanlee.switchyard_api.TestcontainersConfiguration;
import io.jonathanlee.switchyard_api.domain.models.Audience;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class AudiencesMapperTests {

  @Autowired private AudiencesMapper audiencesMapper;

  @Test
  void roundTripsAnAudienceThroughEveryMapperMethod() {
    String key = "audience-" + UUID.randomUUID().toString().substring(0, 8);

    Optional<Audience> inserted =
        audiencesMapper.insert(Audience.builder().key(key).query("country = 'IE'").build());
    assertThat(inserted).isPresent();
    Audience audience = inserted.get();
    assertThat(audience.getId()).isNotNull();
    assertThat(audience.getKey()).isEqualTo(key);
    assertThat(audience.getQuery()).isEqualTo("country = 'IE'");
    assertThat(audience.getCreatedAt()).isNotNull();
    assertThat(audience.getUpdatedAt()).isNotNull();

    assertThat(audiencesMapper.findById(audience.getId())).contains(audience);
    assertThat(audiencesMapper.findByKey(key)).contains(audience);
    assertThat(audiencesMapper.findAll()).contains(audience);

    Optional<Audience> updatedById =
        audiencesMapper.updateById(
            audience.getId(), Audience.builder().key(key).query("country = 'GB'").build());
    assertThat(updatedById).isPresent();
    assertThat(updatedById.get().getQuery()).isEqualTo("country = 'GB'");
    assertThat(updatedById.get().getUpdatedAt()).isAfterOrEqualTo(audience.getUpdatedAt());

    String renamed = key + "-renamed";
    Optional<Audience> updatedByKey =
        audiencesMapper.updateByKey(
            key, Audience.builder().key(renamed).query("country = 'US'").build());
    assertThat(updatedByKey).isPresent();
    assertThat(updatedByKey.get().getKey()).isEqualTo(renamed);
    assertThat(audiencesMapper.findByKey(key)).isEmpty();

    Optional<Audience> deleted = audiencesMapper.deleteByKey(renamed);
    assertThat(deleted).isPresent();
    assertThat(deleted.get().getId()).isEqualTo(audience.getId());
    assertThat(audiencesMapper.findById(audience.getId())).isEmpty();
    assertThat(audiencesMapper.deleteById(audience.getId())).isEmpty();
  }
}
