package io.jonathanlee.switchyard_api.domain.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAudienceRequestDto implements Serializable {

  @NotNull
  @Size(min = 1, max = 50)
  private String key;

  @NotNull
  @Size(min = 1, max = 100)
  private String query;
}
