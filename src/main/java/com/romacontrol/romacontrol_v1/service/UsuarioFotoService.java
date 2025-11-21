package com.romacontrol.romacontrol_v1.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

public interface UsuarioFotoService {

    /**
     * Actualiza la foto de perfil del usuario autenticado.
     * 
     * @param fotoPerfil archivo de imagen (JPG, PNG o WEBP)
     * @param auth objeto Authentication con el usuario logueado
     * @throws Exception si hay errores en el formato o al guardar
     */
    void actualizarFotoPerfil(MultipartFile fotoPerfil, Authentication auth) throws Exception;
}
