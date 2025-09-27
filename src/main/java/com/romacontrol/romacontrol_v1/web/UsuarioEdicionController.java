package com.romacontrol.romacontrol_v1.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.UsuarioDetailResponse;
import com.romacontrol.romacontrol_v1.dto.UsuarioUpdateRequest;
import com.romacontrol.romacontrol_v1.service.UsuarioEdicionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Validated
public class UsuarioEdicionController {

    private final UsuarioEdicionService usuarioEdicionService;

    /**
     * Editar usuario existente
     * Endpoint: PUT /api/usuarios/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDetailResponse> editarUsuario(
        @PathVariable Long id,
        @Valid @RequestBody UsuarioUpdateRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long editorId = Long.parseLong(userDetails.getUsername()); // Si estás usando el DNI, adaptar esto
        UsuarioDetailResponse actualizado = usuarioEdicionService.editar(id, request, editorId);
        return ResponseEntity.ok(actualizado);
    }
}
