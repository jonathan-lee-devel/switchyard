package io.jonathanlee.switchyard_api.domain.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlagStateUpdateDto implements Serializable {

  private String key;

  @JsonProperty("isEnabled")
  private boolean enabled;
}
