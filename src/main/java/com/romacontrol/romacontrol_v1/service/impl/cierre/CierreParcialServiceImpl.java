package com.romacontrol.romacontrol_v1.service.impl.cierre;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.cierre.CierreParcialRequest;
import com.romacontrol.romacontrol_v1.dto.cierre.CierreParcialResponse;
import com.romacontrol.romacontrol_v1.model.Pago;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.model.cierre.CierreParcial;
import com.romacontrol.romacontrol_v1.repository.PagoRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;
import com.romacontrol.romacontrol_v1.repository.cierre.CierreParcialRepository;
import com.romacontrol.romacontrol_v1.service.cierre.CierreParcialService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CierreParcialServiceImpl implements CierreParcialService {

    private final CierreParcialRepository cierreParcialRepository;
    private final PagoRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;

    // ============================================================
    // 1) OBTENER CIERRE DE UN USUARIO EN UNA FECHA
    // ============================================================
    @Override
    public List<CierreParcialResponse> obtenerCierreDeUsuario(LocalDate fecha, Long usuarioId) {

        List<CierreParcial> lista = cierreParcialRepository.findByUsuario_IdAndFecha(usuarioId, fecha);

        return lista.stream()
                .map(CierreParcialResponse::fromEntity)
                .toList();
    }

    // ============================================================
    // 2) OBTENER TODOS LOS CIERRES DE UNA FECHA
    // ============================================================
    @Override
    public List<CierreParcialResponse> obtenerCierresDeFecha(LocalDate fecha) {

        return cierreParcialRepository.findByFecha(fecha)
                .stream()
                .map(CierreParcialResponse::fromEntity)
                .toList();
    }

    // ============================================================
    // 3) GENERAR CIERRE PARCIAL
    // ============================================================
    @Override
    public CierreParcialResponse generarCierre(CierreParcialRequest request) {

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LocalDate fecha = request.getFecha();

        // ❗ Validar si ya se hizo cierre hoy
        if (existeCierreHoy(usuario.getId())) {
            throw new RuntimeException("El usuario ya realizó el cierre hoy.");
        }

        // ❗ Obtener pagos del día
        List<Pago> pagos = pagoRepository.findByFechaPagoAndCobradoPorUsername(fecha, usuario.getUsername());
        if (pagos.isEmpty()) {
            throw new RuntimeException("No hay pagos para cerrar en esta fecha.");
        }

        // Total del día
        var totalDelDia = pagos.stream()
                .map(Pago::getMonto)
                .reduce((a, b) -> a.add(b))
                .orElseThrow();

        // Crear cierre
        CierreParcial cierre = CierreParcial.builder()
                .usuario(usuario)
                .fecha(fecha)
                .fechaHoraCierre(LocalDateTime.now())
                .totalDelDia(totalDelDia)
                .build();

        cierreParcialRepository.save(cierre);

        return CierreParcialResponse.fromEntity(cierre);
    }

    // ============================================================
    // 4) EXISTE CIERRE HOY?
    // ============================================================
    @Override
    public boolean existeCierreHoy(Long usuarioId) {
        LocalDate hoy = LocalDate.now();
        return cierreParcialRepository.existsByUsuario_IdAndFecha(usuarioId, hoy);
    }
}
