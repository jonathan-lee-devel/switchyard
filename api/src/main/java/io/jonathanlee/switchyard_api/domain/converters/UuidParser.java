package io.jonathanlee.switchyard_api.domain.converters;

import java.util.Optional;
import java.util.UUID;

/** Parses externally supplied identifiers into {@link UUID}s without throwing. */
public final class UuidParser {

  private UuidParser() {}

  /**
   * @param id the raw identifier, typically from a path variable
   * @return the parsed UUID, or empty if {@code id} is null or not a valid UUID
   */
  public static Optional<UUID> parse(String id) {
    if (id == null) {
      return Optional.empty();
    }
    try {
      return Optional.of(UUID.fromString(id));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }
}
