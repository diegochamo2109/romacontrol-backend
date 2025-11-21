package com.romacontrol.romacontrol_v1.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.PagoResponse;
import com.romacontrol.romacontrol_v1.dto.RegistroPagoSolicitud;
import com.romacontrol.romacontrol_v1.model.CuotaMensual;
import com.romacontrol.romacontrol_v1.model.EstadoPago;
import com.romacontrol.romacontrol_v1.model.MetodoPago;
import com.romacontrol.romacontrol_v1.model.Pago;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.model.UsuarioCuota;
import com.romacontrol.romacontrol_v1.model.UsuarioCuotaEstado;
import com.romacontrol.romacontrol_v1.repository.CuotaMensualRepository;
import com.romacontrol.romacontrol_v1.repository.EstadoPagoRepository;
import com.romacontrol.romacontrol_v1.repository.MetodoPagoRepository;
import com.romacontrol.romacontrol_v1.repository.PagoRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioCuotaRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;
import com.romacontrol.romacontrol_v1.service.EmailService;
import com.romacontrol.romacontrol_v1.service.PagoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CuotaMensualRepository cuotaMensualRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final EstadoPagoRepository estadoPagoRepository;
    private final UsuarioCuotaRepository usuarioCuotaRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public PagoResponse registrarPago(RegistroPagoSolicitud solicitud, String usuarioLogueado) {

        // ================================
        // 1) Calcular fecha lógica del pago
        // ================================
        LocalDate fechaPagoLogica;

        if (solicitud.getFechaPagoManual() != null) {
            fechaPagoLogica = solicitud.getFechaPagoManual();

            // ❌ No permitir fechas futuras
            if (fechaPagoLogica.isAfter(LocalDate.now())) {
                throw new RuntimeException("La fecha de pago no puede ser futura");
            }
        } else {
            // Si no mandás nada desde el front, sigue funcionando como antes
            fechaPagoLogica = OffsetDateTime.now().toLocalDate();
        }

        // 🔹 Buscar entidades relacionadas
        Usuario usuario = usuarioRepository.findById(solicitud.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CuotaMensual cuota = cuotaMensualRepository.findById(solicitud.getCuotaMensualId())
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));

        MetodoPago metodo = metodoPagoRepository.findById(solicitud.getMetodoPagoId())
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        // 🔹 Validar duplicado: no se puede pagar 2 veces la misma cuota
        pagoRepository.findByUsuarioIdAndCuotaMensualId(usuario.getId(), cuota.getId())
                .ifPresent(p -> { throw new RuntimeException("La cuota ya fue pagada"); });

        // ================================
        // 2) Calcular si está fuera de término
        // ================================
        LocalDate fechaLimite = cuota.getFechaLimite() != null
                ? cuota.getFechaLimite().toLocalDate()
                : null;

        boolean fueraDeTermino = false;
        if (fechaLimite != null) {
            // Si la fecha del pago (manual o actual) es DESPUÉS del límite → fuera de término
            fueraDeTermino = fechaPagoLogica.isAfter(fechaLimite);
        }

        // 🔹 Obtener estado correcto (PAGADO o PAGADO_CON_RETRASO)
        EstadoPago estadoPago = estadoPagoRepository.findByNombre(
                fueraDeTermino ? "PAGADO_CON_RETRASO" : "PAGADO")
                .orElseThrow(() -> new RuntimeException("EstadoPago no encontrado"));

        // 🔹 Buscar cobrador (el usuario autenticado)
        Usuario cobrador = usuarioRepository.findByDni(usuarioLogueado)
                .orElseThrow(() -> new RuntimeException("Usuario cobrador no encontrado"));

        // ================================
        // 3) Convertir LocalDate a OffsetDateTime para guardar
        // ================================
        OffsetDateTime fechaPagoPersistida = fechaPagoLogica
                .atTime(LocalTime.now()) // o LocalTime.MIDNIGHT si querés 00:00
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime();

        // 🔹 Crear y guardar el pago
        Pago pago = Pago.builder()
                .usuario(usuario)
                .cuotaMensual(cuota)
                .metodoPago(metodo)
                .estado(estadoPago)
                .cobradoPor(cobrador)
                .fechaPago(fechaPagoPersistida)
                .monto(solicitud.getMonto())
                .fueraDeTermino(fueraDeTermino)
                .build();

        pagoRepository.save(pago);

        // 🔹 Actualizar estado de UsuarioCuota → PAGADA
        UsuarioCuota usuarioCuota = usuarioCuotaRepository
                .findByUsuarioIdAndCuotaId(usuario.getId(), cuota.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no tiene asignada esta cuota"));

        usuarioCuota.setEstado(UsuarioCuotaEstado.PAGADA);
        usuarioCuotaRepository.save(usuarioCuota);

        // 🔹 Enviar comprobante PDF al correo del socio (asíncrono)
        emailService.enviarComprobantePagoConPdf(pago);

        // 🔹 Devolver respuesta con fecha límite incluida (no tocamos esto)
        return new PagoResponse(
                pago.getId(),
                usuario.getId(),
                cuota.getId(),
                pago.getMonto(),
                metodo.getNombre(),
                estadoPago.getNombre(),
                pago.getFechaPago(),
                cobrador.getPersona().getNombre() + " " + cobrador.getPersona().getApellido(),
                pago.isFueraDeTermino(),
                cuota.getFechaLimite() != null ? cuota.getFechaLimite().toLocalDate() : null
        );
    }
}
