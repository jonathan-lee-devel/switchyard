package io.jonathanlee.switchyard_api.domain.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
  @Min(1)
  @Max(50)
  private String key;

  @NotNull
  @Min(1)
  @Max(100)
  private String query;
}
