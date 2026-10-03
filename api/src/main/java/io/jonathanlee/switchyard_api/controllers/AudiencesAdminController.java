package io.jonathanlee.switchyard_api.controllers;

import io.jonathanlee.switchyard_api.domain.dtos.AudienceDto;
import io.jonathanlee.switchyard_api.domain.dtos.request.CreateAudienceRequestDto;
import io.jonathanlee.switchyard_api.services.AudiencesAdminService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/v1/admin/audiences")
public class AudiencesAdminController {

  private final AudiencesAdminService audiencesAdminService;

  @GetMapping
  public List<AudienceDto> getAudiences() {
    return audiencesAdminService.getAudiences();
  }

  @GetMapping("/{id}")
  public ResponseEntity<AudienceDto> getAudienceById(@PathVariable String id) {
    return audiencesAdminService
        .getAudienceById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/key/{key}")
  public ResponseEntity<AudienceDto> getAudienceByKey(@PathVariable String key) {
    return audiencesAdminService
        .getAudienceByKey(key)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<AudienceDto> createAudience(
      @Valid @RequestBody CreateAudienceRequestDto dto) {
    return audiencesAdminService
        .createAudience(dto)
        .map(audience -> ResponseEntity.status(HttpStatus.CREATED).body(audience))
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<AudienceDto> updateAudienceById(
      @PathVariable String id, @Valid @RequestBody CreateAudienceRequestDto dto) {
    return audiencesAdminService
        .updateAudienceById(id, dto)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/key/{key}")
  public ResponseEntity<AudienceDto> updateAudienceByKey(
      @PathVariable String key, @Valid @RequestBody CreateAudienceRequestDto dto) {
    return audiencesAdminService
        .updateAudienceByKey(key, dto)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAudienceById(@PathVariable String id) {
    return audiencesAdminService
        .deleteAudienceById(id)
        .map(deleted -> ResponseEntity.noContent().<Void>build())
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/key/{key}")
  public ResponseEntity<Void> deleteAudienceByKey(@PathVariable String key) {
    return audiencesAdminService
        .deleteAudienceByKey(key)
        .map(deleted -> ResponseEntity.noContent().<Void>build())
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
