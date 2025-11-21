package com.romacontrol.romacontrol_v1.web;

import java.io.IOException;
import java.net.URI;
import java.util.List; // 👈 agregado

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // 👈 agregado
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;   // 👈 agregado (CORS rápido local)
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping; // 👈 agregado
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
import com.romacontrol.romacontrol_v1.service.UsuarioFotoService;
import com.romacontrol.romacontrol_v1.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true") // 👈 agregado: CORS local (puedes moverlo a config global)
public class UsuarioController {
  private final UsuarioFotoService usuarioFotoService;

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



  // ===========================
  // POST multipart/form-data (payload JSON + fotoPerfil)
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

    // 🔒 Validación de imagen (formato + tamaño) ANTES de leer bytes
    if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
      String ct = fotoPerfil.getContentType();
      long size = fotoPerfil.getSize();

      if (ct == null || !(ct.equals("image/jpeg") || ct.equals("image/png") || ct.equals("image/webp"))) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato no permitido. Use JPG, PNG o WebP.");
      }
      if (size > 2 * 1024 * 1024) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La imagen no puede superar 2 MB.");
      }
    }

    byte[] fotoBytes = (fotoPerfil != null && !fotoPerfil.isEmpty()) ? fotoPerfil.getBytes() : null;

    UsuarioResponse resp = usuarioService.crearConFoto(request, creadorId, fotoBytes);

    return ResponseEntity
        .created(URI.create("/api/usuarios/" + resp.id()))
        .body(resp);
  }

  // ===========================
  // GET /api/usuarios/{id}/foto  -> devuelve la foto (byte[])
  // ===========================
@GetMapping("/{id}/foto")
public ResponseEntity<byte[]> verFoto(@PathVariable Long id) {
    try {
        byte[] bytes = usuarioService.obtenerFotoPerfil(id);

        // Si no hay imagen guardada, usamos la por defecto
        if (bytes == null || bytes.length == 0) {
            ClassPathResource defaultImage = new ClassPathResource("static/dist/css/imagenes/default-user.png");
            bytes = defaultImage.getInputStream().readAllBytes();

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(bytes);
        }

        // Intentar detectar tipo MIME (image/jpeg, image/png, etc.)
        String mimeType = java.net.URLConnection.guessContentTypeFromStream(
                new java.io.ByteArrayInputStream(bytes)
        );

        MediaType mediaType = (mimeType != null)
                ? MediaType.parseMediaType(mimeType)
                : MediaType.IMAGE_JPEG; // fallback

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(bytes);

    } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}



  // ===========================
  // PATCH /api/usuarios/{id}/activo?activo=true|false
  // Cambia el estado lógico del usuario
  // ===========================
  @PatchMapping(value = "/{id}/activo", produces = "application/json") // 👈 agregado
  public ResponseEntity<UsuarioDetailResponse> cambiarActivo(@PathVariable Long id,
                                                             @RequestParam("activo") boolean activo,
                                                             Authentication auth) {
    if (auth == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
    }

    final String dniActual = auth.getName() == null ? "" : auth.getName().trim();
    Long editorId = usuarioRepository.findIdByDni(dniActual)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "Usuario autenticado no encontrado: " + dniActual));

    UsuarioDetailResponse actualizado = usuarioService.cambiarActivo(id, activo, editorId); // 👈 NUEVO
    return ResponseEntity.ok(actualizado);
  }

  // ===========================
  // PUT /api/usuarios/{id}  (edición completa)
  // ===========================
  @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json") // 👈 agregado
  public ResponseEntity<UsuarioDetailResponse> editar(@PathVariable Long id,                         // 👈 agregado
                                                      @Valid @RequestBody UsuarioUpdateRequest req,   // 👈 agregado
                                                      Authentication auth) {                          // 👈 agregado
    if (auth == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
    }

    final String dniActual = auth.getName() == null ? "" : auth.getName().trim();

    Long editorId = usuarioRepository.findIdByDni(dniActual)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "Usuario autenticado no encontrado: " + dniActual));

    UsuarioDetailResponse actualizado = usuarioService.editar(id, req, editorId); // 👈 agregado

    return ResponseEntity.ok(actualizado); // 👈 agregado
  }
      // ===========================
    // GET /api/usuarios/dni/{dni} -> buscar usuario por DNI
    // ===========================
    @GetMapping(value = "/dni/{dni}", produces = "application/json")
    public ResponseEntity<UsuarioDetailResponse> buscarPorDni(@PathVariable String dni) {
        return usuarioRepository.findByDni(dni)
            .map(usuario -> ResponseEntity.ok(usuarioService.detalle(usuario.getId())))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/buscar", produces = "application/json")
public ResponseEntity<List<UsuarioListItem>> buscarUsuarios(@RequestParam("query") String query) {
  if (query == null || query.isBlank()) {
    return ResponseEntity.badRequest().build();
  }

  List<UsuarioListItem> resultados = usuarioService.buscarPorDniONombreOApellido(query.trim());
  if (resultados.isEmpty()) {
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  return ResponseEntity.ok(resultados);
}
// ===========================
// ===========================
// PUT /api/usuarios/foto  -> actualiza foto y devuelve imagen actualizada
// ===========================
@PutMapping(value = "/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<byte[]> actualizarFotoPerfil(
        @RequestPart("fotoPerfil") MultipartFile fotoPerfil,
        Authentication auth) {

    try {
        // 🔹 Actualiza la foto en la base de datos
        usuarioFotoService.actualizarFotoPerfil(fotoPerfil, auth);

        // 🔹 Obtiene el usuario autenticado actualizado
        String dni = auth.getName().trim();
        var usuario = usuarioRepository.findByDni(dni)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        byte[] bytes = usuario.getPersona().getFotoPerfil();
        if (bytes == null || bytes.length == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se pudo recuperar la nueva foto");
        }

        // 🔹 Detecta tipo MIME para responder correctamente
        String mimeType = java.net.URLConnection.guessContentTypeFromStream(
                new java.io.ByteArrayInputStream(bytes)
        );
        MediaType mediaType = (mimeType != null)
                ? MediaType.parseMediaType(mimeType)
                : MediaType.IMAGE_JPEG;

        // 🔹 Devuelve la imagen actualizada directamente
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(bytes);

    } catch (ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(null);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}


}
