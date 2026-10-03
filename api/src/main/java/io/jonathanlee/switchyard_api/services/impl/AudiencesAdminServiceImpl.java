package io.jonathanlee.switchyard_api.services.impl;

import static io.jonathanlee.switchyard_api.config.CacheConfig.ALL_KEY;
import static io.jonathanlee.switchyard_api.config.CacheConfig.AUDIENCES;
import static io.jonathanlee.switchyard_api.config.CacheConfig.ID_KEY;
import static io.jonathanlee.switchyard_api.config.CacheConfig.KEY_KEY;
import static io.jonathanlee.switchyard_api.config.CacheConfig.UNLESS_EMPTY;

import io.jonathanlee.switchyard_api.domain.converters.AudienceConverter;
import io.jonathanlee.switchyard_api.domain.converters.UuidParser;
import io.jonathanlee.switchyard_api.domain.dtos.AudienceDto;
import io.jonathanlee.switchyard_api.domain.dtos.request.CreateAudienceRequestDto;
import io.jonathanlee.switchyard_api.mappers.AudiencesMapper;
import io.jonathanlee.switchyard_api.services.AudiencesAdminService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * Reads are cached as a list, per id and per key. Every write evicts the whole {@code audiences}
 * cache, since a rename leaves a stale entry under the previous key as well as the id and the list.
 */
@Service
@RequiredArgsConstructor
public class AudiencesAdminServiceImpl implements AudiencesAdminService {

  private final AudiencesMapper audiencesMapper;

  @Override
  @Cacheable(cacheNames = AUDIENCES, key = ALL_KEY)
  public List<AudienceDto> getAudiences() {
    return audiencesMapper.findAll().stream().map(AudienceConverter::toDto).toList();
  }

  @Override
  @Cacheable(cacheNames = AUDIENCES, key = ID_KEY, unless = UNLESS_EMPTY)
  public Optional<AudienceDto> getAudienceById(String id) {
    return UuidParser.parse(id).flatMap(audiencesMapper::findById).map(AudienceConverter::toDto);
  }

  @Override
  @Cacheable(cacheNames = AUDIENCES, key = KEY_KEY, unless = UNLESS_EMPTY)
  public Optional<AudienceDto> getAudienceByKey(String key) {
    return audiencesMapper.findByKey(key).map(AudienceConverter::toDto);
  }

  @Override
  @CacheEvict(cacheNames = AUDIENCES, allEntries = true)
  public Optional<AudienceDto> createAudience(CreateAudienceRequestDto dto) {
    try {
      return audiencesMapper.insert(AudienceConverter.fromRequest(dto)).map(AudienceConverter::toDto);
    } catch (DuplicateKeyException e) {
      return Optional.empty();
    }
  }

  @Override
  @CacheEvict(cacheNames = AUDIENCES, allEntries = true)
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
  @CacheEvict(cacheNames = AUDIENCES, allEntries = true)
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
  @CacheEvict(cacheNames = AUDIENCES, allEntries = true)
  public Optional<AudienceDto> deleteAudienceById(String id) {
    return UuidParser.parse(id).flatMap(audiencesMapper::deleteById).map(AudienceConverter::toDto);
  }

  @Override
  @CacheEvict(cacheNames = AUDIENCES, allEntries = true)
  public Optional<AudienceDto> deleteAudienceByKey(String key) {
    return audiencesMapper.deleteByKey(key).map(AudienceConverter::toDto);
  }
}
