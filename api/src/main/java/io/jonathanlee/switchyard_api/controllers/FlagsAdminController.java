package io.jonathanlee.switchyard_api.controllers;

import io.jonathanlee.switchyard_api.domain.dtos.FlagDto;
import io.jonathanlee.switchyard_api.domain.dtos.request.CreateFlagRequestDto;
import io.jonathanlee.switchyard_api.services.FlagsAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/flags")
public class FlagsAdminController {

  private final FlagsAdminService flagsAdminService;

  @GetMapping("/{id}")
  public ResponseEntity<FlagDto> getFlagById(@PathVariable String id) {
    return flagsAdminService.getFlagById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/key/{key}")
  public ResponseEntity<FlagDto> getFlagByKey(@PathVariable String key) {
    return flagsAdminService.getFlagByKey(key)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<FlagDto> createFlag(@Valid @RequestBody CreateFlagRequestDto dto) {
    return flagsAdminService
        .createFlag(dto)
        .map(flag -> ResponseEntity.status(HttpStatus.CREATED).body(flag))
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<FlagDto> updateFlagById(
      @PathVariable String id, @Valid @RequestBody CreateFlagRequestDto dto) {
    return flagsAdminService.updateFlagById(id, dto)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/key/{key}")
  public ResponseEntity<FlagDto> updateFlagByKey(
      @PathVariable String key, @Valid @RequestBody CreateFlagRequestDto dto) {
    return flagsAdminService.updateFlagByKey(key, dto)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFlagById(@PathVariable String id) {
    return flagsAdminService.deleteFlagById(id)
        .map(deleted -> ResponseEntity.noContent().<Void>build())
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/key/{key}")
  public ResponseEntity<Void> deleteFlagByKey(@PathVariable String key) {
    return flagsAdminService.deleteFlagByKey(key)
        .map(deleted -> ResponseEntity.noContent().<Void>build())
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
