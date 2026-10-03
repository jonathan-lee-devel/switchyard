package io.jonathanlee.switchyard_api.domain.converters;

import io.jonathanlee.switchyard_api.domain.dtos.AudienceDto;
import io.jonathanlee.switchyard_api.domain.dtos.request.CreateAudienceRequestDto;
import io.jonathanlee.switchyard_api.domain.models.Audience;

public final class AudienceConverter {

  private AudienceConverter() {}

  public static AudienceDto toDto(Audience audience) {
    return AudienceDto.builder()
        .id(audience.getId() == null ? null : audience.getId().toString())
        .key(audience.getKey())
        .query(audience.getQuery())
        .createdAt(audience.getCreatedAt())
        .updatedAt(audience.getUpdatedAt())
        .build();
  }

  public static Audience fromRequest(CreateAudienceRequestDto dto) {
    return Audience.builder().key(dto.getKey()).query(dto.getQuery()).build();
  }
}
