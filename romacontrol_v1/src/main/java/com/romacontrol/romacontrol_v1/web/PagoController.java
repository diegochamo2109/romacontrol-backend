package com.romacontrol.romacontrol_v1.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.PagoCreateRequest;
import com.romacontrol.romacontrol_v1.dto.PagoDetailResponse;
import com.romacontrol.romacontrol_v1.service.PagoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Validated
public class PagoController {

  private final PagoService pagoService;

  /**
   * Registra un pago para una cuota ya asignada al usuario.
   * Valida que no esté pagada y marca fueraDeTermino según fechaLimite.
   */
  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','PROFESOR')")
  public ResponseEntity<PagoDetailResponse> registrar(@Valid @RequestBody PagoCreateRequest req,
                                                      Authentication auth) {
    String username = auth.getName();
    var resp = pagoService.registrarPago(req, username);
    return ResponseEntity.ok(resp);
  }
}
