package com.romacontrol.romacontrol_v1.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.romacontrol.romacontrol_v1.dto.RegistroTerminalResponse;
import com.romacontrol.romacontrol_v1.model.RegistroTerminal;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.repository.RegistroTerminalRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TerminalService {

    private final RegistroTerminalRepository registroTerminalRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 🔹 Abre la terminal usando el usuario autenticado.
     * Guarda el usuario que la abrió y devuelve su nombre y rol al frontend.
     */
    public Map<String, Object> abrirTerminal() {
        Map<String, Object> respuesta = new HashMap<>();

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String dniUsuario = auth.getName();

            Usuario admin = usuarioRepository.findByDni(dniUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado."));

            // 🔍 Verificar si ya hay terminal abierta
            if (registroTerminalRepository.findFirstByTerminalAbiertaTrueOrderByIdDesc().isPresent()) {
                respuesta.put("mensaje", "Ya existe una terminal abierta actualmente.");
                return respuesta;
            }

            // 🟢 Crear nueva terminal
            RegistroTerminal terminal = new RegistroTerminal();
            terminal.setAdministrador(admin);
            terminal.setFechaHoraApertura(LocalDateTime.now());
            terminal.setTerminalAbierta(true);
            terminal.setObservacion("Terminal abierta automáticamente por sesión de " + admin.getPersona().getNombre());
            registroTerminalRepository.save(terminal);

            // 🟢 Devolver información adicional al frontend
            String rolDescripcion = admin.getRoles().stream()
                    .map(r -> (r.getDescripcion() != null && !r.getDescripcion().isBlank()) ? r.getDescripcion() : "Sin descripción")
                    .findFirst()
                    .orElse("Sin rol");

            respuesta.put("mensaje", "Terminal conectada correctamente.");
            respuesta.put("admin", admin.getPersona().getNombre() + " " + admin.getPersona().getApellido());
            respuesta.put("rol", rolDescripcion);

        } catch (Exception e) {
            respuesta.put("mensaje", "Ocurrió un error al abrir la terminal.");
        }

        return respuesta;
    }

    /**
     * 🔒 Cierra la terminal.
     * Permite cerrarla al mismo usuario que la abrió o a cualquier usuario con rol ADMIN o PROFESOR.
     * Se valida DNI + PIN del usuario que intenta cerrarla.
     */
    public Map<String, Object> cerrarTerminal(String dniIngresado, String pinIngresado) {
        Map<String, Object> respuesta = new HashMap<>();

        try {
            // ✅ Validaciones iniciales
            if (dniIngresado == null || dniIngresado.isBlank()) {
                respuesta.put("mensaje", "Debe ingresar su DNI para cerrar la terminal.");
                return respuesta;
            }
            if (pinIngresado == null || pinIngresado.isBlank()) {
                respuesta.put("mensaje", "Debe ingresar su PIN para cerrar la terminal.");
                return respuesta;
            }

            // 🔍 Buscar el usuario que intenta cerrar
            Usuario usuarioCierre = usuarioRepository.findByDni(dniIngresado)
                    .orElseThrow(() -> new RuntimeException("No se encontró un usuario con el DNI ingresado."));

            // 🔐 Validar PIN
            if (!passwordEncoder.matches(pinIngresado, usuarioCierre.getPin())) {
                respuesta.put("mensaje", "PIN incorrecto. No se puede cerrar la terminal.");
                return respuesta;
            }

            // 🔍 Buscar la terminal abierta
            RegistroTerminal terminal = registroTerminalRepository.findFirstByTerminalAbiertaTrueOrderByIdDesc()
                    .orElseThrow(() -> new RuntimeException("No hay terminal abierta actualmente."));

            // 🔹 Verificar si es el mismo usuario o tiene rol ADMIN/PROFESOR
            boolean mismoUsuario = terminal.getAdministrador().getId().equals(usuarioCierre.getId());

            boolean tieneRolPermitido = usuarioCierre.getRoles().stream().anyMatch(r -> {
                String rolDesc = (r.getDescripcion() != null) ? r.getDescripcion().trim().toUpperCase() : "";
                String rolNombre = (r.getNombre() != null) ? r.getNombre().trim().toUpperCase() : "";
                return rolDesc.contains("ADMIN") || rolDesc.contains("PROFESOR")
                        || rolNombre.contains("ADMIN") || rolNombre.contains("PROFESOR");
            });

            if (!mismoUsuario && !tieneRolPermitido) {
                respuesta.put("mensaje",
                        "Solo el usuario que abrió la terminal o un usuario con rol ADMIN/PROFESOR puede cerrarla.");
                return respuesta;
            }

            // ✅ Cerrar la terminal correctamente
            terminal.setFechaHoraCierre(LocalDateTime.now());
            terminal.setTerminalAbierta(false);
            terminal.setCerradaPor(usuarioCierre);
            terminal.setObservacion("Cerrada correctamente por " + usuarioCierre.getPersona().getNombre());
            registroTerminalRepository.save(terminal);

            respuesta.put("mensaje", "Terminal cerrada correctamente por " + usuarioCierre.getPersona().getNombre());

        } catch (Exception e) {
            respuesta.put("mensaje",
                    (e.getMessage() != null) ? e.getMessage() : "Ocurrió un error al intentar cerrar la terminal.");
        }

        return respuesta;
    }

    /** 
     * 📋 Devuelve un historial limpio de todas las aperturas/cierres de terminales.
     * Incluye el usuario que la abrió y el que la cerró.
     */
    public List<RegistroTerminalResponse> listarHistorial() {
        return registroTerminalRepository.findAll().stream().map(r -> new RegistroTerminalResponse(
                r.getId(),
                r.getAdministrador() != null
                        ? (r.getAdministrador().getPersona() != null
                                ? r.getAdministrador().getPersona().getNombre() + " "
                                        + r.getAdministrador().getPersona().getApellido()
                                : r.getAdministrador().getUsername())
                        : "—",
                r.getCerradaPor() != null
                        ? (r.getCerradaPor().getPersona() != null
                                ? r.getCerradaPor().getPersona().getNombre() + " "
                                        + r.getCerradaPor().getPersona().getApellido()
                                : r.getCerradaPor().getUsername())
                        : "—",
                r.getFechaHoraApertura(),
                r.getFechaHoraCierre(),
                r.isTerminalAbierta(),
                r.getObservacion()))
                .collect(Collectors.toList());
    }

    /** 
     * 🟢 Verifica si hay una terminal abierta actualmente.
     */
    public boolean hayTerminalAbierta() {
        return registroTerminalRepository.findFirstByTerminalAbiertaTrueOrderByIdDesc().isPresent();
    }

    /**
     * 🆕 NUEVO: Devuelve información completa del estado actual de la terminal.
     * Incluye si está abierta, quién la abrió, su DNI y la fecha de apertura.
     */
    public Map<String, Object> obtenerEstadoTerminal() {
        Map<String, Object> datos = new HashMap<>();

        registroTerminalRepository.findFirstByTerminalAbiertaTrueOrderByIdDesc().ifPresentOrElse(terminal -> {
            datos.put("abierta", true);

            // 🔹 Nombre completo del usuario que abrió la terminal
            if (terminal.getAdministrador() != null && terminal.getAdministrador().getPersona() != null) {
                datos.put("usuario",
                        terminal.getAdministrador().getPersona().getNombre() + " " +
                                terminal.getAdministrador().getPersona().getApellido());
            }

            // 🔹 DNI pertenece a Usuario, no a Persona
            if (terminal.getAdministrador() != null) {
                datos.put("dni", terminal.getAdministrador().getDni());
            }

            datos.put("fecha_apertura", terminal.getFechaHoraApertura());
        }, () -> {
            datos.put("abierta", false);
        });

        return datos;
    }
}
