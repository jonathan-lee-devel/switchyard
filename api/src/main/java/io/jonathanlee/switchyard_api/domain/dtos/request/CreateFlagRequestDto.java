package io.jonathanlee.switchyard_api.domain.dtos.request;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class CreateFlagRequestDto implements Serializable {

  @NotNull
  @Min(1)
  @Max(50)
  private String key;

  @JsonAlias("isEnabled")
  private Boolean enabled;
}
