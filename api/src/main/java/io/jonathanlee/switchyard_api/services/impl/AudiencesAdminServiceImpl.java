package io.jonathanlee.switchyard_api.services.impl;

import io.jonathanlee.switchyard_api.domain.dtos.AudienceDto;
import io.jonathanlee.switchyard_api.domain.dtos.request.CreateAudienceRequestDto;
import io.jonathanlee.switchyard_api.services.AudiencesAdminService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AudiencesAdminServiceImpl implements AudiencesAdminService {

  @Override
  public List<AudienceDto> getAudiences() {
    return List.of();
  }

  @Override
  public Optional<AudienceDto> getAudienceById(String id) {
    return Optional.empty();
  }

  @Override
  public Optional<AudienceDto> getAudienceByKey(String key) {
    return Optional.empty();
  }

  @Override
  public Optional<AudienceDto> createAudience(CreateAudienceRequestDto dto) {
    return Optional.empty();
  }

  @Override
  public Optional<AudienceDto> updateAudienceById(String id, CreateAudienceRequestDto dto) {
    return Optional.empty();
  }

  @Override
  public Optional<AudienceDto> updateAudienceByKey(String key, CreateAudienceRequestDto dto) {
    return Optional.empty();
  }

  @Override
  public Optional<AudienceDto> deleteAudienceById(String id) {
    return Optional.empty();
  }

  @Override
  public Optional<AudienceDto> deleteAudienceByKey(String key) {
    return Optional.empty();
  }
}
