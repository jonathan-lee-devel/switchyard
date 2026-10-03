package io.jonathanlee.switchyard_api.services;

import io.jonathanlee.switchyard_api.domain.dtos.FlagDto;
import io.jonathanlee.switchyard_api.domain.dtos.request.CreateFlagRequestDto;
import java.util.Optional;

public interface FlagsAdminService {

  Optional<FlagDto> getFlagById(String id);

  Optional<FlagDto> getFlagByKey(String key);

  Optional<FlagDto> createFlag(CreateFlagRequestDto dto);

  Optional<FlagDto> updateFlagById(String id, CreateFlagRequestDto dto);

  Optional<FlagDto> updateFlagByKey(String key, CreateFlagRequestDto dto);

  Optional<FlagDto> deleteFlagById(String id);

  Optional<FlagDto> deleteFlagByKey(String key);
}
