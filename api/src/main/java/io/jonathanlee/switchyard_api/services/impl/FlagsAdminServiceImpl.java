package io.jonathanlee.switchyard_api.services.impl;

import static io.jonathanlee.switchyard_api.config.CacheConfig.FLAGS;
import static io.jonathanlee.switchyard_api.config.CacheConfig.ID_KEY;
import static io.jonathanlee.switchyard_api.config.CacheConfig.KEY_KEY;
import static io.jonathanlee.switchyard_api.config.CacheConfig.UNLESS_EMPTY;

import io.jonathanlee.switchyard_api.domain.converters.FlagConverter;
import io.jonathanlee.switchyard_api.domain.converters.UuidParser;
import io.jonathanlee.switchyard_api.domain.dtos.FlagDto;
import io.jonathanlee.switchyard_api.domain.dtos.FlagStateUpdateDto;
import io.jonathanlee.switchyard_api.domain.dtos.request.CreateFlagRequestDto;
import io.jonathanlee.switchyard_api.domain.models.Flag;
import io.jonathanlee.switchyard_api.mappers.FlagsMapper;
import io.jonathanlee.switchyard_api.services.FlagsAdminService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * Reads are cached per id and per key. Every write evicts the whole {@code flags} cache: an update
 * may rename a flag, so the stale entry can sit under the previous key as well as the id and the
 * list, and clearing everything is simpler and safer than tracking each of those.
 */
@Service
@RequiredArgsConstructor
public class FlagsAdminServiceImpl implements FlagsAdminService {

  private final FlagsMapper flagsMapper;
  private final FlagStateUpdatesSink flagStateUpdatesSink;

  @Override
  @Cacheable(cacheNames = FLAGS, key = ID_KEY, unless = UNLESS_EMPTY)
  public Optional<FlagDto> getFlagById(String id) {
    return UuidParser.parse(id).flatMap(flagsMapper::findById).map(FlagConverter::toDto);
  }

  @Override
  @Cacheable(cacheNames = FLAGS, key = KEY_KEY, unless = UNLESS_EMPTY)
  public Optional<FlagDto> getFlagByKey(String key) {
    return flagsMapper.findByKey(key).map(FlagConverter::toDto);
  }

  @Override
  @CacheEvict(cacheNames = FLAGS, allEntries = true)
  public Optional<FlagDto> createFlag(CreateFlagRequestDto dto) {
    try {
      return flagsMapper.insert(FlagConverter.fromRequest(dto)).map(this::publishAndConvert);
    } catch (DuplicateKeyException e) {
      return Optional.empty();
    }
  }

  @Override
  @CacheEvict(cacheNames = FLAGS, allEntries = true)
  public Optional<FlagDto> updateFlagById(String id, CreateFlagRequestDto dto) {
    try {
      return UuidParser.parse(id)
          .flatMap(uuid -> flagsMapper.updateById(uuid, FlagConverter.fromRequest(dto)))
          .map(this::publishAndConvert);
    } catch (DuplicateKeyException e) {
      return Optional.empty();
    }
  }

  @Override
  @CacheEvict(cacheNames = FLAGS, allEntries = true)
  public Optional<FlagDto> updateFlagByKey(String key, CreateFlagRequestDto dto) {
    try {
      return flagsMapper
          .updateByKey(key, FlagConverter.fromRequest(dto))
          .map(this::publishAndConvert);
    } catch (DuplicateKeyException e) {
      return Optional.empty();
    }
  }

  @Override
  @CacheEvict(cacheNames = FLAGS, allEntries = true)
  public Optional<FlagDto> deleteFlagById(String id) {
    return UuidParser.parse(id).flatMap(flagsMapper::deleteById).map(this::publishDisabledAndConvert);
  }

  @Override
  @CacheEvict(cacheNames = FLAGS, allEntries = true)
  public Optional<FlagDto> deleteFlagByKey(String key) {
    return flagsMapper.deleteByKey(key).map(this::publishDisabledAndConvert);
  }

  private FlagDto publishAndConvert(Flag flag) {
    flagStateUpdatesSink.publish(FlagConverter.toStateUpdate(flag));
    return FlagConverter.toDto(flag);
  }

  /** A deleted flag no longer exists, so subscribers are told it is now disabled. */
  private FlagDto publishDisabledAndConvert(Flag flag) {
    flagStateUpdatesSink.publish(
        FlagStateUpdateDto.builder().key(flag.getKey()).enabled(false).build());
    return FlagConverter.toDto(flag);
  }
}
