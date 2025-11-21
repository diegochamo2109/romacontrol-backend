package com.romacontrol.romacontrol_v1.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.CuotaDetailResponse;
import com.romacontrol.romacontrol_v1.dto.CuotaEditRequest;
import com.romacontrol.romacontrol_v1.service.CuotaEstadoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cuotas/estado")
@RequiredArgsConstructor
public class CuotaEstadoController {

    private final CuotaEstadoService cuotaEstadoService;

    @PutMapping("/{id}")
    public ResponseEntity<CuotaDetailResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestBody CuotaEditRequest request) {

        CuotaDetailResponse resp = cuotaEstadoService.cambiarEstado(id, request);
        return ResponseEntity.ok(resp);
    }
}
