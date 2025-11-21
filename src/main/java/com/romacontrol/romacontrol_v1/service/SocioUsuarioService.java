package com.romacontrol.romacontrol_v1.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.romacontrol.romacontrol_v1.dto.SocioUsuarioResponse;
import com.romacontrol.romacontrol_v1.dto.UsuarioUpdateRequest;
import com.romacontrol.romacontrol_v1.model.Usuario;

public interface SocioUsuarioService {
    SocioUsuarioResponse obtenerMisDatos(String dni);
    void editarMisDatos(String dni, UsuarioUpdateRequest request);
    Usuario actualizarFoto(String dni, MultipartFile file) throws IOException;
}
