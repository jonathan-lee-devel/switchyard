package io.jonathanlee.switchyard_api.services.impl;

import io.jonathanlee.switchyard_api.domain.dtos.FlagStateUpdateDto;
import io.jonathanlee.switchyard_api.domain.dtos.FlagStateUpdatesContainerDto;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * In-process bridge between flag writes and the live update stream. Admin writes publish here and
 * {@link FlagsServiceImpl} exposes the resulting flux.
 */
@Component
public class FlagStateUpdatesSink {

  private final Sinks.Many<FlagStateUpdatesContainerDto> sink =
      Sinks.many().multicast().onBackpressureBuffer();

  public void publish(FlagStateUpdateDto... updates) {
    sink.emitNext(
        FlagStateUpdatesContainerDto.builder().updates(List.of(updates)).build(),
        Sinks.EmitFailureHandler.FAIL_FAST);
  }

  public Flux<FlagStateUpdatesContainerDto> asFlux() {
    return sink.asFlux();
  }
}
