package com.romacontrol.romacontrol_v1.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.ContactoUsuarioResponse;
import com.romacontrol.romacontrol_v1.service.UsuarioContactoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contactos-urgencia")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true")
public class UsuarioContactoController {

  private static final Logger log = LoggerFactory.getLogger(UsuarioContactoController.class);
  private final UsuarioContactoService usuarioContactoService;

  /**
   * 🔍 Buscar usuarios por DNI, Nombre o Apellido
   * Devuelve también los datos del contacto de urgencia asociado.
   *
   * Ejemplo:
   *   GET /api/contactos-urgencia/buscar?filtro=Chamorro
   *   GET /api/contactos-urgencia/buscar?filtro=Diego
   *   GET /api/contactos-urgencia/buscar?filtro=33548166
   */
  @GetMapping("/buscar")
  public ResponseEntity<List<ContactoUsuarioResponse>> buscar(@RequestParam String filtro) {
    log.debug("➡️ Buscando usuarios por filtro: {}", filtro);

    if (filtro == null || filtro.trim().isEmpty()) {
      log.warn("⚠️ Filtro vacío en búsqueda de contactos de urgencia");
      return ResponseEntity.ok(List.of());
    }

    List<ContactoUsuarioResponse> resultados = usuarioContactoService.buscarPorDniOApellido(filtro.trim());
    log.info("✅ Se encontraron {} resultado(s) para '{}'", resultados.size(), filtro);

    return ResponseEntity.ok(resultados);
  }
}
