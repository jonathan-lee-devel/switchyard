package io.jonathanlee.switchyard_api.services.impl;

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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlagsAdminServiceImpl implements FlagsAdminService {

  private final FlagsMapper flagsMapper;
  private final FlagStateUpdatesSink flagStateUpdatesSink;

  @Override
  public Optional<FlagDto> getFlagById(String id) {
    return UuidParser.parse(id).flatMap(flagsMapper::findById).map(FlagConverter::toDto);
  }

  @Override
  public Optional<FlagDto> getFlagByKey(String key) {
    return flagsMapper.findByKey(key).map(FlagConverter::toDto);
  }

  @Override
  public Optional<FlagDto> createFlag(CreateFlagRequestDto dto) {
    try {
      return flagsMapper.insert(FlagConverter.fromRequest(dto)).map(this::publishAndConvert);
    } catch (DuplicateKeyException e) {
      return Optional.empty();
    }
  }

  @Override
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
  public Optional<FlagDto> deleteFlagById(String id) {
    return UuidParser.parse(id).flatMap(flagsMapper::deleteById).map(this::publishDisabledAndConvert);
  }

  @Override
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
