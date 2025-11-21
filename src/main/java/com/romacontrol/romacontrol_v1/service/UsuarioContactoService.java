package com.romacontrol.romacontrol_v1.service;

import java.util.List;

import com.romacontrol.romacontrol_v1.dto.ContactoUsuarioResponse;

public interface UsuarioContactoService {
  List<ContactoUsuarioResponse> buscarPorDniOApellido(String filtro);
}
