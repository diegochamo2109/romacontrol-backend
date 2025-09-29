package com.romacontrol.romacontrol_v1.service.impl;

import java.time.OffsetDateTime;

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

    @Override
    @Transactional
    public PagoResponse registrarPago(RegistroPagoSolicitud solicitud, String usuarioLogueado) {
        Usuario usuario = usuarioRepository.findById(solicitud.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CuotaMensual cuota = cuotaMensualRepository.findById(solicitud.getCuotaMensualId())
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));

        MetodoPago metodo = metodoPagoRepository.findById(solicitud.getMetodoPagoId())
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        // Validar que no exista ya un pago registrado
        pagoRepository.findByUsuarioIdAndCuotaMensualId(usuario.getId(), cuota.getId())
                .ifPresent(p -> { throw new RuntimeException("La cuota ya fue pagada"); });

        // Determinar estado del pago y si está fuera de término
        boolean fueraDeTermino = OffsetDateTime.now().isAfter(cuota.getFechaLimite());
        EstadoPago estadoPago = estadoPagoRepository.findByNombre(
                fueraDeTermino ? "PAGADO_CON_RETRASO" : "PAGADO")
                .orElseThrow(() -> new RuntimeException("EstadoPago no encontrado"));

        // Buscar cobrador (se asume que el username es DNI)
        Usuario cobrador = usuarioRepository.findByDni(usuarioLogueado)
                .orElseThrow(() -> new RuntimeException("Usuario cobrador no encontrado"));

        // Crear y guardar pago
        Pago pago = Pago.builder()
                .usuario(usuario)
                .cuotaMensual(cuota)
                .metodoPago(metodo)
                .estado(estadoPago)
                .cobradoPor(cobrador)
                .fechaPago(OffsetDateTime.now())
                .monto(solicitud.getMonto())
                .fueraDeTermino(fueraDeTermino)
                .build();

        pagoRepository.save(pago);

        // 🔹 Actualizar estado en UsuarioCuota con el enum
        UsuarioCuota usuarioCuota = usuarioCuotaRepository
                .findByUsuarioIdAndCuotaId(usuario.getId(), cuota.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no tiene asignada esta cuota"));

        usuarioCuota.setEstado(UsuarioCuotaEstado.PAGADA);
        usuarioCuotaRepository.save(usuarioCuota);

        // Devolver respuesta
        return new PagoResponse(
                pago.getId(),
                usuario.getId(),
                cuota.getId(),
                pago.getMonto(),
                metodo.getNombre(),
                estadoPago.getNombre(),
                pago.getFechaPago(),
                cobrador.getPersona().getNombre() + " " + cobrador.getPersona().getApellido(),
                pago.isFueraDeTermino()
        );
    }
}
