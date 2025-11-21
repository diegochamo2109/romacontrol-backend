package com.romacontrol.romacontrol_v1.service.impl.socio;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.socio.AsistenciaSocioRespuesta;
import com.romacontrol.romacontrol_v1.dto.socio.CuotaSocioRespuesta;
import com.romacontrol.romacontrol_v1.dto.socio.MisDatosSocioRespuesta;
import com.romacontrol.romacontrol_v1.dto.socio.PagoSocioRespuesta;
import com.romacontrol.romacontrol_v1.model.Asistencia;
import com.romacontrol.romacontrol_v1.model.ContactoUrgencia;
import com.romacontrol.romacontrol_v1.model.CuotaMensual;
import com.romacontrol.romacontrol_v1.model.Persona;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.model.UsuarioCuota;
import com.romacontrol.romacontrol_v1.model.UsuarioCuotaEstado;
import com.romacontrol.romacontrol_v1.repository.AsistenciaRepository;
import com.romacontrol.romacontrol_v1.repository.PagoRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioCuotaRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;
import com.romacontrol.romacontrol_v1.service.socio.SocioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocioServiceImpl implements SocioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioCuotaRepository usuarioCuotaRepository;
    private final PagoRepository pagoRepository;
    private final AsistenciaRepository asistenciaRepository;

    // =====================================================
    // 🧩 Obtener usuario autenticado
    // =====================================================
    private Usuario obtenerUsuarioActual() {
        String dni = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        return usuarioRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + dni));
    }

    // =====================================================
    // 1) 📌 MIS DATOS
    // =====================================================
    @Override
    public MisDatosSocioRespuesta obtenerMisDatos() {

        Usuario usuario = obtenerUsuarioActual();
        Persona persona = usuario.getPersona();
        ContactoUrgencia contacto = persona != null ? persona.getContactoUrgencia() : null;

        String calle = null;
        String numero = null;

        if (persona != null && persona.getDomicilio() != null) {
            String[] partes = persona.getDomicilio().trim().split(" ");
            if (partes.length > 1) {
                numero = partes[partes.length - 1];
                calle = persona.getDomicilio().replace(" " + numero, "");
            } else {
                calle = persona.getDomicilio();
            }
        }

        String provinciaSocio = null;
        String localidadSocio = null;

        if (persona != null && persona.getLocalidad() != null) {
            localidadSocio = persona.getLocalidad().getNombre();
            if (persona.getLocalidad().getProvincia() != null) {
                provinciaSocio = persona.getLocalidad().getProvincia().getNombre();
            }
        }

        String provinciaContacto = null;
        String localidadContacto = null;

        if (contacto != null && contacto.getLocalidad() != null) {
            localidadContacto = contacto.getLocalidad().getNombre();
            if (contacto.getLocalidad().getProvincia() != null) {
                provinciaContacto = contacto.getLocalidad().getProvincia().getNombre();
            }
        }

        return MisDatosSocioRespuesta.builder()
                .nombre(persona != null ? persona.getNombre() : null)
                .apellido(persona != null ? persona.getApellido() : null)
                .dni(usuario.getDni())
                .calle(calle)
                .numero(numero)
                .fechaNacimiento(persona != null && persona.getFechaNacimiento() != null
                        ? persona.getFechaNacimiento().toString()
                        : null)
                .provincia(provinciaSocio)
                .localidad(localidadSocio)
                .genero(persona != null && persona.getGenero() != null
                        ? persona.getGenero().getNombre()
                        : null)
                .telefonoArea(persona != null ? persona.getTelefonoArea() : null)
                .telefonoNumero(persona != null ? persona.getTelefonoNumero() : null)
                .telefonoCompleto(
                        persona != null ? persona.getTelefonoArea() + " " + persona.getTelefonoNumero() : null)
                .email(persona != null ? persona.getEmail() : null)
                .contactoNombre(contacto != null ? contacto.getNombre() : null)
                .contactoApellido(contacto != null ? contacto.getApellido() : null)
                .contactoRelacion(contacto != null ? contacto.getRelacion() : null)
                .contactoProvincia(provinciaContacto)
                .contactoLocalidad(localidadContacto)
                .contactoTelefonoArea(contacto != null ? contacto.getTelefonoArea() : null)
                .contactoTelefonoNumero(contacto != null ? contacto.getTelefonoNumero() : null)
                .build();
    }

    // =====================================================
    // 2) 📌 MIS CUOTAS (pendientes + vencidas)
    // =====================================================
@Override
public List<CuotaSocioRespuesta> obtenerMisCuotasPendientesYVencidas() {

    Usuario usuario = obtenerUsuarioActual();
    LocalDate hoy = LocalDate.now();

    // Solo cuotas PENDIENTES o PAGADAS_FUERA_DE_TERMINO si querés incluir vencidas
    List<UsuarioCuota> cuotas = usuarioCuotaRepository.findByUsuarioIdAndEstadoIn(
            usuario.getId(),
            List.of(UsuarioCuotaEstado.PENDIENTE)
    );

    return cuotas.stream()
            .map(uc -> {
                CuotaMensual c = uc.getCuota();

                LocalDate limite = (c != null && c.getFechaLimite() != null)
                        ? c.getFechaLimite().toLocalDate()
                        : null;

                boolean estaVencida = false;
                if (limite != null) {
                    estaVencida = limite.isBefore(hoy);
                }

                return CuotaSocioRespuesta.builder()
                        .idUsuarioCuota(uc.getId())
                        .cuotaMensualId(c != null ? c.getId() : null)
                        .descripcionCuota(c != null ? c.getDescripcion() : null)
                        .importe(c != null ? c.getImporte() : null)
                        .fechaLimite(limite)

                        .estadoCuota(estaVencida ? "VENCIDA" : "PENDIENTE")
                        .pendiente(!estaVencida)
                        .vencida(estaVencida)
                        .cerrada(false)
                        .build();
            })
            .sorted(Comparator.comparing(
                    CuotaSocioRespuesta::getFechaLimite,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ))
            .collect(Collectors.toList());
}


    // =====================================================
    // 3) 📌 MIS PAGOS
    // =====================================================
    @Override
    public List<PagoSocioRespuesta> obtenerMisPagos() {

        Usuario usuario = obtenerUsuarioActual();

        return pagoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(p -> PagoSocioRespuesta.builder()
                        .idPago(p.getId())
                        .cuotaMensualId(
                                p.getCuotaMensual() != null ? p.getCuotaMensual().getId() : null)
                        .descripcionCuota(
                                p.getCuotaMensual() != null ? p.getCuotaMensual().getDescripcion() : null)
                        .montoPagado(p.getMonto())
                        .fechaPago(p.getFechaPago())
                        .metodoPago(
                                p.getMetodoPago() != null ? p.getMetodoPago().getNombre() : null)
                        .estadoPago(
                                p.getEstado() != null ? p.getEstado().getNombre() : null)
                        .cobradoPor(
                                p.getCobradoPor() != null && p.getCobradoPor().getPersona() != null
                                        ? p.getCobradoPor().getPersona().getNombre() + " "
                                                + p.getCobradoPor().getPersona().getApellido()
                                        : "Desconocido")
                        .build())
                .sorted(Comparator.comparing(PagoSocioRespuesta::getFechaPago).reversed())
                .collect(Collectors.toList());
    }

    // =====================================================
    // 4) 📌 MIS ASISTENCIAS
    // =====================================================
    @Override
    public List<AsistenciaSocioRespuesta> obtenerMisAsistencias() {

        Usuario usuario = obtenerUsuarioActual();

        List<Asistencia> lista = asistenciaRepository
                .findByUsuarioIdOrderByFechaRegistroDescHoraRegistroDesc(usuario.getId());

        return lista.stream()
                .map(a -> {

                    OffsetDateTime fechaHora = OffsetDateTime.of(
                            a.getFechaRegistro(),
                            a.getHoraRegistro(),
                            OffsetDateTime.now().getOffset());

                    String tipo = (a.getHoraSalida() == null) ? "ENTRADA" : "SALIDA";

                    return AsistenciaSocioRespuesta.builder()
                            .idAsistencia(a.getId())
                            .fechaHora(fechaHora)
                            .tipo(tipo)
                            .observacion(a.getObservacion())
                            .build();
                })
                .collect(Collectors.toList());
    }

}
