package com.romacontrol.romacontrol_v1.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.romacontrol.romacontrol_v1.dto.ContactoUsuarioResponse;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;
import com.romacontrol.romacontrol_v1.service.UsuarioContactoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioContactoServiceImpl implements UsuarioContactoService {

  private final UsuarioRepository usuarioRepository;

  @Override
  public List<ContactoUsuarioResponse> buscarPorDniOApellido(String filtro) {
    if (filtro == null || filtro.trim().isEmpty()) {
      return List.of(); // 🔹 evita errores por string vacío
    }

    // 🔍 Buscar por DNI, nombre o apellido (sin duplicados)
    List<Usuario> usuarios = usuarioRepository.buscarPorDniONombreOApellido(filtro.trim());

    // 🔹 Mapear resultados al DTO ContactoUsuarioResponse
    return usuarios.stream()
        .map(u -> {
          var persona = u.getPersona();
          var contacto = (persona != null) ? persona.getContactoUrgencia() : null;

          return ContactoUsuarioResponse.builder()
              .idUsuario(u.getId())
              .dni(u.getDni())
              .nombreCompleto(persona != null
                  ? persona.getApellido() + ", " + persona.getNombre()
                  : null)
              .activo(u.isActivo())
              .nombreContacto(contacto != null ? contacto.getNombre() : null)
              .apellidoContacto(contacto != null ? contacto.getApellido() : null)
              .relacion(contacto != null ? contacto.getRelacion() : null)
              .telefonoArea(contacto != null ? contacto.getTelefonoArea() : null)
              .telefonoNumero(contacto != null ? contacto.getTelefonoNumero() : null)
              .build();
        })
        .collect(Collectors.toList());
  }
}
