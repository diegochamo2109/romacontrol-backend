package com.romacontrol.romacontrol_v1.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.romacontrol.romacontrol_v1.dto.UsuarioCreateRequest;
import com.romacontrol.romacontrol_v1.dto.UsuarioResponse;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;
import com.romacontrol.romacontrol_v1.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

  private final UsuarioService usuarioService;
  private final UsuarioRepository usuarioRepository;

  @PostMapping
  public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioCreateRequest request,
                                               Authentication auth) {
    if (auth == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
    }

    // En tu login usás UsernamePasswordAuthenticationToken(dni, pin),
    // por lo que auth.getName() devuelve el DNI.
    String dniActual = auth.getName();

    Long creadorId = usuarioRepository.findIdByDni(dniActual)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "Usuario autenticado no encontrado"));

    var resp = usuarioService.crear(request, creadorId);
    return ResponseEntity.ok(resp);
  }
}
