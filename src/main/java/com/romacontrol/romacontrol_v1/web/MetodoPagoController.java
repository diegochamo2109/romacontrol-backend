package com.romacontrol.romacontrol_v1.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.model.MetodoPago;
import com.romacontrol.romacontrol_v1.repository.MetodoPagoRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/metodos-pago")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true") // igual que en UsuarioController
public class MetodoPagoController {

    private final MetodoPagoRepository metodoPagoRepository;

    // 🔹 GET /api/metodos-pago
    @GetMapping
    public ResponseEntity<List<MetodoPago>> listarMetodos() {
        List<MetodoPago> metodos = metodoPagoRepository.findAll();
        return ResponseEntity.ok(metodos);
    }
}
