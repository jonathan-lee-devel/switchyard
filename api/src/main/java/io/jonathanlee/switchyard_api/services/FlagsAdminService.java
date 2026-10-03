package io.jonathanlee.switchyard_api.services;

import io.jonathanlee.switchyard_api.domain.dtos.FlagDto;
import java.util.Optional;

public interface FlagsAdminService {

  Optional<FlagDto> getFlagById(String id);

  Optional<FlagDto> getFlagByKey(String key);

  Optional<FlagDto> createFlag();

  Optional<FlagDto> updateFlagById(String id);

  Optional<FlagDto> updateFlagByKey(String key);

  Optional<FlagDto> deleteFlagById(String id);

  Optional<FlagDto> deleteFlagByKey(String key);
}
