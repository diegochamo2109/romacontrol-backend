package com.romacontrol.romacontrol_v1.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.RegistroTerminalResponse;
import com.romacontrol.romacontrol_v1.service.TerminalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/terminal")
@RequiredArgsConstructor
public class TerminalController {

    private final TerminalService terminalService;

    /**
     * 📜 Devuelve el historial completo de aperturas y cierres de terminales.
     */
    @GetMapping("/historial")
    public ResponseEntity<List<RegistroTerminalResponse>> listarHistorial() {
        return ResponseEntity.ok(terminalService.listarHistorial());
    }

    /**
     * 🟢 Abre la terminal usando el usuario autenticado (sin volver a loguearse).
     */
    @PostMapping("/abrir")
    public ResponseEntity<Map<String, Object>> abrirTerminal() {
        return ResponseEntity.ok(terminalService.abrirTerminal());
    }

    /**
     * 🔒 Cierra la terminal.
     */
    @PostMapping("/cerrar")
    public ResponseEntity<Map<String, Object>> cerrarTerminal(@RequestBody(required = false) Map<String, String> datos) {
        String dni = (datos != null) ? datos.get("dni") : null;
        String pin = (datos != null) ? datos.get("pin") : null;
        return ResponseEntity.ok(terminalService.cerrarTerminal(dni, pin));
    }

    /**
     * 🟡 NUEVO: Devuelve información completa del estado actual de la terminal.
     * Incluye si está abierta, quién la abrió y su DNI.
     */
    @GetMapping("/estado")
    public ResponseEntity<Map<String, Object>> estadoTerminal() {
        return ResponseEntity.ok(terminalService.obtenerEstadoTerminal());
    }
}
