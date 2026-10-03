package io.jonathanlee.switchyard_api.domain.dtos.request;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class CreateFlagRequestDto implements Serializable {

  @NotNull
  @Size(min = 1, max = 50)
  private String key;

  @JsonAlias("isEnabled")
  private Boolean enabled;
}
