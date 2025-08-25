package com.romacontrol.romacontrol_v1.web;

import java.io.IOException;
import java.net.URI;
import java.util.List; // 👈 agregado

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // 👈 agregado
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;        // 👈 agregado
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;   // 👈 agregado
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;      // 👈 agregado
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
  // NUEVO: POST multipart/form-data (payload JSON + fotoPerfil)
  // Mantiene el endpoint JSON anterior, y agrega este para soportar imagen
  // ===========================
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UsuarioResponse> crearConFoto(
      @RequestPart("payload") @Valid UsuarioCreateRequest request,
      @RequestPart(value = "fotoPerfil", required = false) MultipartFile fotoPerfil,
      Authentication auth) throws IOException {

    if (auth == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
    }
    String dniActual = auth.getName() == null ? "" : auth.getName().trim();

    Long creadorId = usuarioRepository.findIdByDni(dniActual)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "Usuario autenticado no encontrado: " + dniActual));

    // 🔒 AGREGADO: Validación de imagen (formato + tamaño) ANTES de leer bytes
    if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
      String ct = fotoPerfil.getContentType();
      long size = fotoPerfil.getSize();

      // formatos permitidos
      if (ct == null || !(ct.equals("image/jpeg") || ct.equals("image/png") || ct.equals("image/webp"))) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato no permitido. Use JPG, PNG o WebP.");
      }
      // tamaño máximo: 2 MB
      if (size > 2 * 1024 * 1024) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La imagen no puede superar 2 MB.");
      }
    }

    byte[] fotoBytes = (fotoPerfil != null && !fotoPerfil.isEmpty()) ? fotoPerfil.getBytes() : null;

    // Llama a un método específico que incluye el manejo de foto en Persona (bytea)
    UsuarioResponse resp = usuarioService.crearConFoto(request, creadorId, fotoBytes);

    return ResponseEntity
        .created(URI.create("/api/usuarios/" + resp.id()))
        .body(resp);
  }

  // ===========================
  // GET /api/usuarios/{id}/foto  -> devuelve la foto (byte[])
  // ===========================
  @GetMapping(value = "/{id}/foto", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<byte[]> verFoto(@PathVariable Long id) {
    byte[] bytes = usuarioService.obtenerFotoPerfil(id);
    if (bytes == null || bytes.length == 0) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(bytes);
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
