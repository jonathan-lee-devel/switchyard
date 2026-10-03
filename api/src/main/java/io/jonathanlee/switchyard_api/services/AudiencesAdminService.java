package io.jonathanlee.switchyard_api.services;

import io.jonathanlee.switchyard_api.domain.dtos.AudienceDto;
import java.util.List;
import java.util.Optional;

public interface AudiencesAdminService {

  List<AudienceDto> getAudiences();

  Optional<AudienceDto> getAudienceById(String id);

  Optional<AudienceDto> getAudienceByKey(String key);

  Optional<AudienceDto> createAudience();

  Optional<AudienceDto> updateAudience();

  Optional<AudienceDto> deleteAudience();
}
