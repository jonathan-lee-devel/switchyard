package io.jonathanlee.switchyard_api.domain.models;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseModel implements Serializable {
  private UUID id;
  private Instant createdAt;
  private Instant updatedAt;
}
