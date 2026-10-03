package io.jonathanlee.switchyard_api.services.impl;

import io.jonathanlee.switchyard_api.domain.converters.AudienceConverter;
import io.jonathanlee.switchyard_api.domain.converters.UuidParser;
import io.jonathanlee.switchyard_api.domain.dtos.AudienceDto;
import io.jonathanlee.switchyard_api.domain.dtos.request.CreateAudienceRequestDto;
import io.jonathanlee.switchyard_api.mappers.AudiencesMapper;
import io.jonathanlee.switchyard_api.services.AudiencesAdminService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AudiencesAdminServiceImpl implements AudiencesAdminService {

  private final AudiencesMapper audiencesMapper;

  @Override
  public List<AudienceDto> getAudiences() {
    return audiencesMapper.findAll().stream().map(AudienceConverter::toDto).toList();
  }

  @Override
  public Optional<AudienceDto> getAudienceById(String id) {
    return UuidParser.parse(id).flatMap(audiencesMapper::findById).map(AudienceConverter::toDto);
  }

  @Override
  public Optional<AudienceDto> getAudienceByKey(String key) {
    return audiencesMapper.findByKey(key).map(AudienceConverter::toDto);
  }

  @Override
  public Optional<AudienceDto> createAudience(CreateAudienceRequestDto dto) {
    try {
      return audiencesMapper.insert(AudienceConverter.fromRequest(dto)).map(AudienceConverter::toDto);
    } catch (DuplicateKeyException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<AudienceDto> updateAudienceById(String id, CreateAudienceRequestDto dto) {
    try {
      return UuidParser.parse(id)
          .flatMap(uuid -> audiencesMapper.updateById(uuid, AudienceConverter.fromRequest(dto)))
          .map(AudienceConverter::toDto);
    } catch (DuplicateKeyException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<AudienceDto> updateAudienceByKey(String key, CreateAudienceRequestDto dto) {
    try {
      return audiencesMapper
          .updateByKey(key, AudienceConverter.fromRequest(dto))
          .map(AudienceConverter::toDto);
    } catch (DuplicateKeyException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<AudienceDto> deleteAudienceById(String id) {
    return UuidParser.parse(id).flatMap(audiencesMapper::deleteById).map(AudienceConverter::toDto);
  }

  @Override
  public Optional<AudienceDto> deleteAudienceByKey(String key) {
    return audiencesMapper.deleteByKey(key).map(AudienceConverter::toDto);
  }
}
