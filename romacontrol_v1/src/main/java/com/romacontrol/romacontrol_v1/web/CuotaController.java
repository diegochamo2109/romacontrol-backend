package com.romacontrol.romacontrol_v1.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.CuotaCreateRequest;
import com.romacontrol.romacontrol_v1.dto.CuotaDetailResponse;
import com.romacontrol.romacontrol_v1.service.CuotaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cuotas")
@RequiredArgsConstructor
@Validated
public class CuotaController {

  private final CuotaService cuotaService;

@GetMapping
@PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
public ResponseEntity<List<CuotaDetailResponse>> listar() {
    var cuotas = cuotaService.listarTodas();
    return ResponseEntity.ok(cuotas);
}


  /**
   * Crea una cuota; si `asignar=true`, se asigna a todos los usuarios activos.
   */
  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
  public ResponseEntity<CuotaDetailResponse> crear(@Valid @RequestBody CuotaCreateRequest req,
                                                   @RequestParam(defaultValue = "false") boolean asignar,
                                                   Authentication auth) {
    String username = auth.getName(); // en tu sistema suele ser el DNI
    var resp = cuotaService.crearYAsignar(req, username, asignar);
    return ResponseEntity.ok(resp);
  }
}
