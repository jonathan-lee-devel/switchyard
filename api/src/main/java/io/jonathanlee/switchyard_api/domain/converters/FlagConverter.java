package io.jonathanlee.switchyard_api.domain.converters;

import io.jonathanlee.switchyard_api.domain.dtos.FlagDto;
import io.jonathanlee.switchyard_api.domain.dtos.FlagStateUpdateDto;
import io.jonathanlee.switchyard_api.domain.dtos.request.CreateFlagRequestDto;
import io.jonathanlee.switchyard_api.domain.models.Flag;

public final class FlagConverter {

  private FlagConverter() {}

  public static FlagDto toDto(Flag flag) {
    return FlagDto.builder()
        .id(flag.getId() == null ? null : flag.getId().toString())
        .key(flag.getKey())
        .enabled(flag.isEnabled())
        .createdAt(flag.getCreatedAt())
        .updatedAt(flag.getUpdatedAt())
        .build();
  }

  public static Flag fromRequest(CreateFlagRequestDto dto) {
    return Flag.builder().key(dto.getKey()).enabled(Boolean.TRUE.equals(dto.getEnabled())).build();
  }

  public static FlagStateUpdateDto toStateUpdate(Flag flag) {
    return FlagStateUpdateDto.builder().key(flag.getKey()).enabled(flag.isEnabled()).build();
  }
}
