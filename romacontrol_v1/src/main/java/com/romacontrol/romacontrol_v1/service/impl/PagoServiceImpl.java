package com.romacontrol.romacontrol_v1.service.impl;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.PagoCreateRequest;
import com.romacontrol.romacontrol_v1.dto.PagoDetailResponse;
import com.romacontrol.romacontrol_v1.model.Pago;
import com.romacontrol.romacontrol_v1.model.UsuarioCuotaEstado;
import com.romacontrol.romacontrol_v1.repository.EstadoPagoRepository;
import com.romacontrol.romacontrol_v1.repository.MetodoPagoRepository;
import com.romacontrol.romacontrol_v1.repository.PagoRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioCuotaRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements com.romacontrol.romacontrol_v1.service.PagoService {

  private final UsuarioRepository usuarioRepo;
  private final UsuarioCuotaRepository usuarioCuotaRepo;
  private final MetodoPagoRepository metodoPagoRepo;
  private final EstadoPagoRepository estadoPagoRepo;
  private final PagoRepository pagoRepo;

  @Transactional
  @Override
  public PagoDetailResponse registrarPago(PagoCreateRequest req, String usernameActual) {
    // 1) Resolver usuario
    var usuario = (req.getUsuarioId() != null)
        ? usuarioRepo.findById(req.getUsuarioId())
            .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"))
        : usuarioRepo.findByDni(req.getDni())
            .orElseThrow(() -> new IllegalArgumentException("DNI no encontrado"));

    // 2) Verificar que la cuota esté asignada
    var uc = usuarioCuotaRepo.findByUsuarioIdAndCuotaId(
        usuario.getId(), req.getCuotaMensualId()
    ).orElseThrow(() -> new IllegalStateException("El usuario no tiene asignada esta cuota"));

    if (uc.getEstado() == UsuarioCuotaEstado.PAGADA) {
      throw new IllegalStateException("La cuota ya fue pagada por este usuario");
    }

    // 3) Obtener datos auxiliares
    var cobrador = usuarioRepo.findByDni(usernameActual)
        .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));
    var metodo = metodoPagoRepo.findById(req.getMetodoPagoId())
        .orElseThrow(() -> new IllegalArgumentException("Método de pago no existe"));
    var estadoPagado = estadoPagoRepo.findByNombreIgnoreCase("PAGADO")
        .orElseThrow(() -> new IllegalStateException("Falta estado PAGADO en tabla estado_pago"));

    var ahora = OffsetDateTime.now();
    var cuota = uc.getCuota();
    boolean fueraDeTermino = ahora.isAfter(cuota.getFechaLimite());

    // 4) Registrar pago
    var pago = Pago.builder()
        .usuario(usuario)
        .cuotaMensual(cuota)
        .metodoPago(metodo)
        .estado(estadoPagado)
        .cobradoPor(cobrador)
        .fechaPago(ahora)
        .monto(cuota.getImporte())
        .fueraDeTermino(fueraDeTermino)
        .build();
    pago = pagoRepo.save(pago);

    // 5) Actualizar estado en usuario-cuota
    uc.setEstado(UsuarioCuotaEstado.PAGADA);
    uc.setConRetraso(fueraDeTermino);
    uc.setFechaCambioEstado(ahora);
    usuarioCuotaRepo.save(uc); // ✅ agregado para persistir cambios

    // 6) Devolver respuesta detallada
    return PagoDetailResponse.builder()
        .id(pago.getId())
        .dni(usuario.getDni())
        .usuarioNombre(usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido())
        .cuotaMensualId(cuota.getId())
        
        .cuotaDescripcion(cuota.getDescripcion())
        .fechaPago(pago.getFechaPago())
        .monto(pago.getMonto())
        .fueraDeTermino(pago.isFueraDeTermino())
        .metodoPago(metodo.getNombre())
        .build();
  }
}
