package io.jonathanlee.switchyard_api.services.impl;

import io.jonathanlee.switchyard_api.domain.converters.FlagConverter;
import io.jonathanlee.switchyard_api.domain.dtos.FlagDto;
import io.jonathanlee.switchyard_api.domain.dtos.FlagStateUpdatesContainerDto;
import io.jonathanlee.switchyard_api.mappers.FlagsMapper;
import io.jonathanlee.switchyard_api.services.FlagsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class FlagsServiceImpl implements FlagsService {

  private final FlagsMapper flagsMapper;
  private final FlagStateUpdatesSink flagStateUpdatesSink;

  @Override
  public List<FlagDto> getFlags() {
    return flagsMapper.findAll().stream().map(FlagConverter::toDto).toList();
  }

  @Override
  public Flux<FlagStateUpdatesContainerDto> streamFlagUpdates() {
    return flagStateUpdatesSink.asFlux();
  }
}
