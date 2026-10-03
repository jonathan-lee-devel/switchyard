package io.jonathanlee.switchyard_api.controllers;

import io.jonathanlee.switchyard_api.domain.dtos.FlagDto;
import io.jonathanlee.switchyard_api.domain.dtos.FlagStateUpdatesContainerDto;
import io.jonathanlee.switchyard_api.services.FlagsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/flags")
public class FlagsController {

  private final FlagsService flagsService;

  @GetMapping
  public List<FlagDto> getFlags() {
    return flagsService.getFlags();
  }

  @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<FlagStateUpdatesContainerDto> streamFlagUpdates() {
    return flagsService.streamFlagUpdates();
  }
}
