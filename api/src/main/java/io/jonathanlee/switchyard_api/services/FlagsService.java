package io.jonathanlee.switchyard_api.services;

import io.jonathanlee.switchyard_api.domain.dtos.FlagDto;
import io.jonathanlee.switchyard_api.domain.dtos.FlagStateUpdatesContainerDto;
import java.util.List;
import reactor.core.publisher.Flux;

/** Read access to feature flags and a live stream of their state changes. */
public interface FlagsService {

  /**
   * Returns a snapshot of every flag, including its key, enabled state and audit timestamps.
   *
   * @return the current flags; never {@code null}, empty if no flags exist
   */
  List<FlagDto> getFlags();

  /**
   * Returns a stream of flag state changes. Each emitted {@link FlagStateUpdatesContainerDto}
   * batches one or more {@link io.jonathanlee.switchyard_api.domain.dtos.FlagStateUpdateDto}
   * entries, each carrying a flag key and its new enabled state, so a single event can describe
   * several flags that changed together.
   *
   * <p>A controller can return this directly from an endpoint producing {@code
   * text/event-stream} to expose it as Server-Sent Events, with each container serialized as one
   * event. The stream is unbounded and ends only when the subscriber cancels, typically on client
   * disconnect; implementations should release any underlying resources at that point.
   *
   * @return a hot, never-completing stream of batched flag state updates
   */
  Flux<FlagStateUpdatesContainerDto> streamFlagUpdates();
}
