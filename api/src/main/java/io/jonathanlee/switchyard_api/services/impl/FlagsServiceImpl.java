package io.jonathanlee.switchyard_api.services.impl;

import io.jonathanlee.switchyard_api.domain.dtos.FlagDto;
import io.jonathanlee.switchyard_api.domain.dtos.FlagStateUpdatesContainerDto;
import io.jonathanlee.switchyard_api.services.FlagsService;
import java.util.List;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class FlagsServiceImpl implements FlagsService {

  private final Sinks.Many<FlagStateUpdatesContainerDto> flagUpdatesSink =
      Sinks.many().multicast().onBackpressureBuffer();

  @Override
  public List<FlagDto> getFlags() {
    return List.of();
  }

  @Override
  public Flux<FlagStateUpdatesContainerDto> streamFlagUpdates() {
    return flagUpdatesSink.asFlux();
  }
}
