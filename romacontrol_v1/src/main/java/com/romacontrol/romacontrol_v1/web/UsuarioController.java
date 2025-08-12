package com.romacontrol.romacontrol_v1.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.UsuarioCreateRequest;
import com.romacontrol.romacontrol_v1.dto.UsuarioResponse;
import com.romacontrol.romacontrol_v1.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

  private final UsuarioService usuarioService;

  @PostMapping
  public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioCreateRequest request) {
    var resp = usuarioService.crear(request, null); // TODO: creadoPorId desde auth
    return ResponseEntity.ok(resp);
  }
}
