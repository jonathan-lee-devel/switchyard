package io.jonathanlee.switchyard_api.domain.dtos;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlagStateUpdatesContainerDto implements Serializable {
  private List<FlagStateUpdateDto> updates;
}
