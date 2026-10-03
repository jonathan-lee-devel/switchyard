package io.jonathanlee.switchyard_api.services.impl;

import io.jonathanlee.switchyard_api.domain.dtos.FlagDto;
import io.jonathanlee.switchyard_api.domain.dtos.request.CreateFlagRequestDto;
import io.jonathanlee.switchyard_api.services.FlagsAdminService;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class FlagsAdminServiceImpl implements FlagsAdminService {

  @Override
  public Optional<FlagDto> getFlagById(String id) {
    return Optional.empty();
  }

  @Override
  public Optional<FlagDto> getFlagByKey(String key) {
    return Optional.empty();
  }

  @Override
  public Optional<FlagDto> createFlag(CreateFlagRequestDto dto) {
    return Optional.empty();
  }

  @Override
  public Optional<FlagDto> updateFlagById(String id, CreateFlagRequestDto dto) {
    return Optional.empty();
  }

  @Override
  public Optional<FlagDto> updateFlagByKey(String key, CreateFlagRequestDto dto) {
    return Optional.empty();
  }

  @Override
  public Optional<FlagDto> deleteFlagById(String id) {
    return Optional.empty();
  }

  @Override
  public Optional<FlagDto> deleteFlagByKey(String key) {
    return Optional.empty();
  }
}
