package io.jonathanlee.switchyard_api.services;

import io.jonathanlee.switchyard_api.domain.dtos.AudienceDto;
import io.jonathanlee.switchyard_api.domain.dtos.request.CreateAudienceRequestDto;
import java.util.List;
import java.util.Optional;

public interface AudiencesAdminService {

  List<AudienceDto> getAudiences();

  Optional<AudienceDto> getAudienceById(String id);

  Optional<AudienceDto> getAudienceByKey(String key);

  Optional<AudienceDto> createAudience(CreateAudienceRequestDto dto);

  Optional<AudienceDto> updateAudienceById(String id, CreateAudienceRequestDto dto);

  Optional<AudienceDto> updateAudienceByKey(String key, CreateAudienceRequestDto dto);

  Optional<AudienceDto> deleteAudienceById(String id);

  Optional<AudienceDto> deleteAudienceByKey(String key);
}
