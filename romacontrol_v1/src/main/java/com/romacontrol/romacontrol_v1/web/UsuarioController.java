package com.romacontrol.romacontrol_v1.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;        // 👈 agregado
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.romacontrol.romacontrol_v1.dto.UsuarioCreateRequest;
import com.romacontrol.romacontrol_v1.dto.UsuarioDetailResponse;
import com.romacontrol.romacontrol_v1.dto.UsuarioListItem;
import com.romacontrol.romacontrol_v1.dto.UsuarioResponse;
import com.romacontrol.romacontrol_v1.dto.UsuarioUpdateRequest; // 👈 agregado
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

  @GetMapping(produces = "application/json")
  public List<UsuarioListItem> listar(@RequestParam(value = "activo", required = false) Boolean activo) {
    return usuarioService.listar(activo);
  }

  @GetMapping(value = "/{id}", produces = "application/json")
  public UsuarioDetailResponse detalle(@PathVariable Long id) {
    return usuarioService.detalle(id);
  }

  @PostMapping(consumes = "application/json", produces = "application/json")
  public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioCreateRequest request,
                                               Authentication auth) {
    if (auth == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
    }

    String dniActual = auth.getName() == null ? "" : auth.getName().trim();

    Long creadorId = usuarioRepository.findIdByDni(dniActual)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "Usuario autenticado no encontrado: " + dniActual));

    UsuarioResponse resp = usuarioService.crear(request, creadorId);

    return ResponseEntity
        .created(URI.create("/api/usuarios/" + resp.id()))
        .body(resp);
  }

  // ===========================
  // PUT /api/usuarios/{id}
  // Edición completa de usuario
  // ===========================
  @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json") // 👈 agregado
  public ResponseEntity<UsuarioDetailResponse> editar(@PathVariable Long id,                         // 👈 agregado
                                                      @Valid @RequestBody UsuarioUpdateRequest req,   // 👈 agregado
                                                      Authentication auth) {                          // 👈 agregado
    // Reuso el mismo esquema de autenticación que en POST/crear
    if (auth == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
    }

    final String dniActual = auth.getName() == null ? "" : auth.getName().trim();

    // Obtengo el id del usuario autenticado para auditoría/modificación
    Long editorId = usuarioRepository.findIdByDni(dniActual)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "Usuario autenticado no encontrado: " + dniActual));

    // Delego la lógica de negocio al service (actualiza persona, contacto, roles,
    // tipo de cuota y PIN opcional; NO toca 'dni' ni 'activo')
    UsuarioDetailResponse actualizado = usuarioService.editar(id, req, editorId); // 👈 agregado

    return ResponseEntity.ok(actualizado); // 👈 agregado
  }
}
