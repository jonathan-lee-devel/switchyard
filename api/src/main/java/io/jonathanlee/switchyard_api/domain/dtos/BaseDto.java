package io.jonathanlee.switchyard_api.domain.dtos;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseDto implements Serializable {
  private String id;
  private Instant createdAt;
  private Instant updatedAt;
}
