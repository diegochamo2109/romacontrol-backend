package com.romacontrol.romacontrol_v1.service.impl;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;
import com.romacontrol.romacontrol_v1.service.UsuarioFotoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioFotoServiceImpl implements UsuarioFotoService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void actualizarFotoPerfil(MultipartFile fotoPerfil, Authentication auth) throws Exception {
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        String dni = auth.getName().trim();
        Usuario usuario = usuarioRepository.findByDni(dni)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + dni));

        if (fotoPerfil == null || fotoPerfil.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar una imagen válida.");
        }

        String tipo = Objects.requireNonNull(fotoPerfil.getContentType(), "Tipo de archivo no detectado");
        if (!(tipo.equals("image/jpeg") || tipo.equals("image/png") || tipo.equals("image/webp"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato no permitido (solo JPG, PNG o WEBP).");
        }

        if (fotoPerfil.getSize() > 2 * 1024 * 1024) { // 2 MB
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La imagen no puede superar los 2 MB.");
        }

        // ✅ Guardar la foto
        usuario.getPersona().setFotoPerfil(fotoPerfil.getBytes());
        usuarioRepository.save(usuario);
    }
}
