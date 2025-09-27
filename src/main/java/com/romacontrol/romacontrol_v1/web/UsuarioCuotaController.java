


package com.romacontrol.romacontrol_v1.web;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.UsuarioCuotaItemResponse;
import com.romacontrol.romacontrol_v1.service.UsuarioCuotaQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Validated
public class UsuarioCuotaController {

  private final UsuarioCuotaQueryService usuarioCuotaQueryService;

  /**
   * Ejemplos:
   * GET /api/usuarios/33548166/cuotas          -> PENDIENTE, VENCIDA, PAGADA
   * GET /api/usuarios/33548166/cuotas?estado=PENDIENTE,VENCIDA
   */
  @GetMapping("/{dni}/cuotas")
  public ResponseEntity<List<UsuarioCuotaItemResponse>> listarCuotasPorDni(
      @PathVariable String dni,
      @RequestParam(required = false) String estado) {

    List<String> estados = (estado == null || estado.isBlank())
        ? List.of() // el service toma todos por defecto
        : Arrays.stream(estado.split(","))
                .map(String::trim)
                .toList();

    var resp = usuarioCuotaQueryService.listarPorDniYEstados(dni, estados);
    return ResponseEntity.ok(resp);
  }
}
