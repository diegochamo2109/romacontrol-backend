    package com.romacontrol.romacontrol_v1.service;

    import com.romacontrol.romacontrol_v1.dto.UsuarioDetailResponse;
    import com.romacontrol.romacontrol_v1.dto.UsuarioUpdateRequest;

    public interface UsuarioEdicionService {
        UsuarioDetailResponse editar(Long id, UsuarioUpdateRequest request, Long editorId);
    }
