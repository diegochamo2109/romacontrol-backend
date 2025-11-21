package com.romacontrol.romacontrol_v1.service;

import java.text.Normalizer;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;                 // NUEVO
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException; // NUEVO

import com.romacontrol.romacontrol_v1.dto.UsuarioCreateRequest;
import com.romacontrol.romacontrol_v1.dto.UsuarioDetailResponse;
import com.romacontrol.romacontrol_v1.dto.UsuarioListItem;
import com.romacontrol.romacontrol_v1.dto.UsuarioResponse;
import com.romacontrol.romacontrol_v1.dto.UsuarioUpdateRequest;
import com.romacontrol.romacontrol_v1.exception.ConflictException;
import com.romacontrol.romacontrol_v1.exception.NotFoundException;
import com.romacontrol.romacontrol_v1.model.ContactoUrgencia;
import com.romacontrol.romacontrol_v1.model.CuotaMensual;
import com.romacontrol.romacontrol_v1.model.Genero;
import com.romacontrol.romacontrol_v1.model.Localidad;
import com.romacontrol.romacontrol_v1.model.Persona;
import com.romacontrol.romacontrol_v1.model.Rol;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.model.UsuarioCuota;
import com.romacontrol.romacontrol_v1.model.UsuarioCuotaEstado;
import com.romacontrol.romacontrol_v1.repository.ContactoUrgenciaRepository;
import com.romacontrol.romacontrol_v1.repository.CuotaMensualRepository;
import com.romacontrol.romacontrol_v1.repository.EstadoUsuarioRepository;
import com.romacontrol.romacontrol_v1.repository.GeneroRepository;
import com.romacontrol.romacontrol_v1.repository.LocalidadRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioCuotaRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;
import com.romacontrol.romacontrol_v1.repository.rol.RolRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;




@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final ContactoUrgenciaRepository contactoRepo;
    private final GeneroRepository generoRepo;
    private final LocalidadRepository localidadRepo;
    private final EstadoUsuarioRepository estadoUsuarioRepo;
    private final RolRepository rolRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CuotaMensualRepository cuotaMensualRepo;
    private final UsuarioCuotaRepository usuarioCuotaRepo;

    
    // ===========================
    // CREAR usuario 

    // ===========================
    @Transactional
    public UsuarioResponse crearConFoto(UsuarioCreateRequest req, Long creadoPorId, byte[] fotoBytes) {
        final String dni = req.dni().trim();
        final String pinPlano = req.pin().trim();

        if (usuarioRepo.existsByDni(dni)) {
            throw new ConflictException("El DNI ya existe 173: " + dni);
        }

        // NUEVO: roles → ¿es Socio?
        Set<Rol> roles = new HashSet<>(rolRepo.findAllById(req.rolIds()));
        boolean esSocio = hasRolSocio(roles);

        // NUEVO: cuota solo obligatoria si es Socio
        CuotaMensual cuota = null;
        if (esSocio) {
            if (req.cuotaMensualId() == null) {
                throw badRequest("cuotaMensualId es obligatorio para el rol Socio LINEA 81");
            }
            cuota = cuotaMensualRepo.findById(req.cuotaMensualId())
                    .orElseThrow(() -> new NotFoundException("Cuota mensual no encontrada"));
        } else if (req.cuotaMensualId() != null) {
            cuota = cuotaMensualRepo.findById(req.cuotaMensualId())
                    .orElseThrow(() -> new NotFoundException("Cuota mensual no encontrada"));
        }

        Genero genero = generoRepo.findById(req.persona().generoId())
                .orElseThrow(() -> new NotFoundException("Género no encontrado"));

        Localidad locPersona = localidadRepo.findById(req.persona().localidadId())
                .orElseThrow(() -> new NotFoundException("Localidad (persona) no encontrada"));

        Localidad locContacto = localidadRepo.findById(req.contacto().localidadId())
                .orElseThrow(() -> new NotFoundException("Localidad (contacto) no encontrada"));

        Persona persona = Persona.builder()
                .nombre(req.persona().nombre())
                .apellido(req.persona().apellido())
                .fechaNacimiento(req.persona().fechaNacimiento())
                .domicilio(req.persona().domicilio())
                .telefonoArea(req.persona().telefonoArea())
                .telefonoNumero(req.persona().telefonoNumero())
                .email(req.persona().email())
                .genero(genero)
                .localidad(locPersona)
                .build();

        if (fotoBytes != null && fotoBytes.length > 0) {
            persona.setFotoPerfil(fotoBytes);
        }

        Usuario usuario = Usuario.builder()
                .dni(dni)
                .pin(passwordEncoder.encode(pinPlano))
                .persona(persona)
                .activo(true)
                .cuotaAsignada(cuota) // CAMBIO: puede ser null si no es Socio
                .fechaCreacion(OffsetDateTime.now())
                .roles(roles)
                .build();

        estadoUsuarioRepo.findByNombre("ACTIVO").ifPresent(usuario::setEstadoUsuario);

        if (creadoPorId != null) {
            try {
                var creadorRef = usuarioRepo.getReferenceById(creadoPorId);
                usuario.setCreadoPor(creadorRef);
            } catch (EntityNotFoundException ex) {
                throw new NotFoundException("Usuario creador no existe (id=" + creadoPorId + ")");
            }
        }

        Usuario saved = usuarioRepo.save(usuario);

        // Registrar contacto de urgencia
        ContactoUrgencia contacto = ContactoUrgencia.builder()
                .nombre(req.contacto().nombre())
                .apellido(req.contacto().apellido())
                .telefonoArea(req.contacto().telefonoArea())
                .telefonoNumero(req.contacto().telefonoNumero())
                .relacion(req.contacto().relacion())
                .localidad(locContacto)
                .persona(saved.getPersona())
                .build();

        contactoRepo.save(contacto);

        // CAMBIO: Crear usuario_cuota SOLO si hay cuota (i.e., es Socio o enviaron una)
        if (cuota != null) { // NUEVO
            UsuarioCuota uc = new UsuarioCuota();
            uc.setUsuario(saved);
            uc.setCuota(cuota);
            uc.setEstado(UsuarioCuotaEstado.PENDIENTE); // estado inicial
            uc.setFechaAsignacion(OffsetDateTime.now());
            usuarioCuotaRepo.save(uc);
        }

        try {
            String destinatario = saved.getPersona() != null ? saved.getPersona().getEmail() : null;
            if (destinatario != null && !destinatario.isBlank()) {
                emailService.enviarBienvenida(destinatario, dni, pinPlano);
            }
        } catch (Exception ignored) { }

        return new UsuarioResponse(
                saved.getId(),
                saved.getDni(),
                saved.getPersona().getNombre(),
                saved.getPersona().getApellido()
        );
    }

    // ===========================
    // LISTAR
    // ===========================
    @Transactional(readOnly = true)
    public List<UsuarioListItem> listar(Boolean activo) {
        final List<Usuario> usuarios =
                (activo == null) ? usuarioRepo.findAll()
                        : (Boolean.TRUE.equals(activo) ? usuarioRepo.findByActivoTrue()
                        : usuarioRepo.findByActivoFalse());

        return usuarios.stream()
                .map(this::toListItem)
                .toList();
    }

    private UsuarioListItem toListItem(Usuario u) {
        var p = u.getPersona();

        final String nombreCompleto = (p == null)
                ? "-"
                : String.format("%s, %s",
                nullSafe(p.getApellido()),
                nullSafe(p.getNombre())).trim();

        final List<String> roles = (u.getRoles() == null)
                ? List.of()
                : u.getRoles().stream()
                .map(r -> nullSafe(r.getNombre()))
                .filter(s -> !s.isBlank())
                .toList();

        String creadorDni = null, creadorNombre = null;
        if (u.getCreadoPor() != null) {
            creadorDni = u.getCreadoPor().getDni();
            var cp = u.getCreadoPor().getPersona();
            if (cp != null) {
                creadorNombre = (nullSafe(cp.getApellido()) + ", " + nullSafe(cp.getNombre())).trim();
            }
        }

        return UsuarioListItem.builder()
                .id(u.getId())
                .dni(u.getDni())
                .nombreCompleto(nombreCompleto)
                .roles(roles)
                .activo(u.isActivo())
                .fechaCreacion(u.getFechaCreacion())
                .creadoPorDni(creadorDni)
                .creadoPorNombre(creadorNombre)
                .build();
    }

    // ===========================
    // DETALLE
    // ===========================
    @Transactional(readOnly = true)
    public UsuarioDetailResponse detalle(Long id) {
        Usuario u = usuarioRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado id=" + id));

        var p = u.getPersona();

        var contactoOpt = (p == null)
                ? java.util.Optional.<ContactoUrgencia>empty()
                : contactoRepo.findFirstByPersonaIdOrderByIdDesc(p.getId());

        List<Long> rolIds = (u.getRoles() == null)
                ? List.of()
                : u.getRoles().stream().map(Rol::getId).filter(Objects::nonNull).toList();

        String creadorNombre = null, creadorDni = null;
        Long creadorId = null;
        if (u.getCreadoPor() != null) {
            creadorDni = u.getCreadoPor().getDni();
            var cp = u.getCreadoPor().getPersona();
            if (cp != null) {
                creadorNombre = String.format("%s, %s",
                        nullSafe(cp.getApellido()), nullSafe(cp.getNombre())).trim();
            }
            creadorId = u.getCreadoPor().getId();
        }

        return UsuarioDetailResponse.builder()
                .id(u.getId())
                .dni(u.getDni())
                .activo(u.isActivo())
                .fechaCreacion(u.getFechaCreacion())

                .cuotaMensualId(u.getCuotaAsignada() != null ? u.getCuotaAsignada().getId() : null)
                .cuotaDescripcion(u.getCuotaAsignada() != null ? u.getCuotaAsignada().getDescripcion() : null)

                // Persona
                .nombre(p != null ? p.getNombre() : null)
                .apellido(p != null ? p.getApellido() : null)
                .fechaNacimiento(p != null ? p.getFechaNacimiento() : null)
                .domicilio(p != null ? p.getDomicilio() : null)
                .telefonoArea(p != null ? p.getTelefonoArea() : null)
                .telefonoNumero(p != null ? p.getTelefonoNumero() : null)
                .email(p != null ? p.getEmail() : null)
                .generoId(p != null && p.getGenero() != null ? p.getGenero().getId() : null)
                .generoNombre(p != null && p.getGenero() != null ? p.getGenero().getNombre() : null)

                .localidadId(p != null && p.getLocalidad() != null ? p.getLocalidad().getId() : null)
                .provinciaId(
                        p != null && p.getLocalidad() != null && p.getLocalidad().getProvincia() != null
                                ? p.getLocalidad().getProvincia().getId()
                                : null)

                .contactoLocalidadId(
                        contactoOpt.map(c -> c.getLocalidad() != null ? c.getLocalidad().getId() : null).orElse(null))

                .contactoProvinciaId(
                        contactoOpt.map(c -> c.getLocalidad() != null && c.getLocalidad().getProvincia() != null
                                ? c.getLocalidad().getProvincia().getId()
                                : null).orElse(null))

                // Contacto de urgencia
                .contactoNombre(contactoOpt.map(ContactoUrgencia::getNombre).orElse(null))
                .contactoApellido(contactoOpt.map(ContactoUrgencia::getApellido).orElse(null))
                .contactoTelefonoArea(contactoOpt.map(ContactoUrgencia::getTelefonoArea).orElse(null))
                .contactoTelefonoNumero(contactoOpt.map(ContactoUrgencia::getTelefonoNumero).orElse(null))
                .contactoRelacion(contactoOpt.map(ContactoUrgencia::getRelacion).orElse(null))

                // Roles
                .rolIds(rolIds)
                .rolNombres(u.getRoles().stream()
                        .map(Rol::getDescripcion)
                        .collect(Collectors.toList()))

                // Auditoría
                .creadoPorId(creadorId)
                .creadoPorNombre(creadorNombre)
                .creadoPorDni(creadorDni)

                .build();
    }

@Transactional
public UsuarioDetailResponse cambiarActivo(Long id, boolean activo, Long editorId) {

    Usuario u = usuarioRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Usuario no encontrado id=" + id));

    boolean estabaInactivo = !u.isActivo();
    boolean pasaAActivo = activo;

    u.setActivo(activo);
    u.setFechaModificacion(OffsetDateTime.now());

    // =============================================================
    // 🔥 REGLA para asignar automáticamente CUOTA_DEL_MES
    // =============================================================
    if (estabaInactivo && pasaAActivo) {

        // -----------------------------------------
        // 1) Tiene pagos registrados?
        // -----------------------------------------
        List<UsuarioCuota> pagos = usuarioCuotaRepo.findByUsuarioIdAndEstadoIn(
                u.getId(),
                List.of(
                        UsuarioCuotaEstado.PAGADA,
                        UsuarioCuotaEstado.PAGADA_FUERA_DE_TERMINO
                )
        );
        boolean tienePagos = !pagos.isEmpty();

        // -----------------------------------------
        // 2) Tiene cuotas pendientes o anuladas?
        // -----------------------------------------
        List<UsuarioCuota> pendientes = usuarioCuotaRepo.findByUsuarioIdAndEstadoIn(
                u.getId(),
                List.of(
                        UsuarioCuotaEstado.PENDIENTE,
                        UsuarioCuotaEstado.ANULADA
                )
        );
        boolean tienePendientes = !pendientes.isEmpty();

        // -----------------------------------------
        // 3) Si NO tiene pagos NI pendientes → asignar cuota del mes
        // -----------------------------------------
        if (!tienePagos && !tienePendientes) {

            var cuotaDelMes = cuotaMensualRepo
                    .findFirstByEstadoCuota_NombreIgnoreCaseOrderByFechaAltaDesc("CUOTA_DEL_MES")
                    .orElse(null);

            if (cuotaDelMes != null) {

                UsuarioCuota uc = new UsuarioCuota();
                uc.setUsuario(u);
                uc.setCuota(cuotaDelMes);
                uc.setEstado(UsuarioCuotaEstado.PENDIENTE);
                uc.setFechaAsignacion(OffsetDateTime.now());
                usuarioCuotaRepo.save(uc);

                log.info("🔔 Usuario {} reactivado → asignada CUOTA_DEL_MES ({})",
                        u.getDni(), cuotaDelMes.getDescripcion());
            } else {
                log.warn("⚠️ Usuario {} reactivado pero NO hay CUOTA_DEL_MES disponible.", u.getDni());
            }

        } else {
            log.info("ℹ️ Usuario {} reactivado → NO se asignó cuota (tiene pagos o pendientes).",
                    u.getDni());
        }
    }

    usuarioRepo.save(u);
    return detalle(u.getId());
}



        // ===========================
        // EDITAR usuario
        // ===========================
        @Transactional
        public UsuarioDetailResponse editar(Long id, UsuarioUpdateRequest req, Long editorId) {
            Usuario u = usuarioRepo.findById(id)
                    .orElseThrow(() -> new NotFoundException("Usuario no encontrado id=" + id));

            // CAMBIO: primero resolvemos roles a aplicar para saber si será Socio
            List<Long> rolIds = req.getRolIds();
            Set<Rol> nuevosRoles;
            if (rolIds == null || rolIds.isEmpty()) {
                nuevosRoles = new HashSet<>();
            } else {
                List<Rol> encontrados = rolRepo.findAllById(rolIds);
                if (encontrados.size() != rolIds.size()) {
                    throw new NotFoundException("Uno o más roles no existen");
                }
                nuevosRoles = new HashSet<>(encontrados);
            }
            boolean esSocio = hasRolSocio(nuevosRoles); // NUEVO

            // CAMBIO: cuota obligatoria solo si (nuevos) roles incluyen Socio
    // ===========================
    // 🚨 Validación: cuota obligatoria solo si (nuevos) roles incluyen Socio
    // ===========================
    // ===========================
    // CAMBIO: cuota obligatoria solo si (nuevos) roles incluyen Socio
    // Si no se envía, se asigna automáticamente la cuota ACTIVA más reciente
    // ===========================
    CuotaMensual cuota = null;

    if (esSocio) {
        if (req.getCuotaMensualId() == null) {
            // Busca la cuota activa más reciente
        cuota = cuotaMensualRepo
        .findFirstByEstadoCuota_NombreIgnoreCaseOrderByFechaAltaDesc("ACTIVA")
        .orElseThrow(() -> badRequest("No hay cuota activa disponible para asignar automáticamente"));



            log.warn("💡 [LINEA 354] Usuario id={} pasó a rol SOCIO sin cuota enviada. Se le asignó automáticamente la cuota '{}'",
                    id, cuota.getDescripcion());
        } else {
            cuota = cuotaMensualRepo.findById(req.getCuotaMensualId())
                    .orElseThrow(() -> new NotFoundException("Cuota mensual no encontrada"));
        }
    }




            // Persona
            var p = u.getPersona();
            if (p == null) {
                throw new NotFoundException("Persona asociada al usuario inexistente");
            }
            var per = req.getPersona();

            p.setNombre(per.getNombre());
            p.setApellido(per.getApellido());
            p.setFechaNacimiento(per.getFechaNacimiento());
            p.setDomicilio(per.getDomicilio());
            p.setTelefonoArea(per.getTelefonoArea());
            p.setTelefonoNumero(per.getTelefonoNumero());
        p.setEmail(per.getEmail());

        if (per.getGeneroId() != null) {
            Genero genero = generoRepo.findById(per.getGeneroId())
                    .orElseThrow(() -> new NotFoundException("Género no encontrado"));
            p.setGenero(genero);
        }

        if (per.getLocalidadId() != null) {
            Localidad locPersona = localidadRepo.findById(per.getLocalidadId())
                    .orElseThrow(() -> new NotFoundException("Localidad (persona) no encontrada"));
            p.setLocalidad(locPersona);
        }

        // Contacto
        var conReq = req.getContacto();
        ContactoUrgencia contacto = contactoRepo.findFirstByPersonaIdOrderByIdDesc(p.getId()).orElse(null);
        if (contacto == null) {
            contacto = new ContactoUrgencia();
            contacto.setPersona(p);
        }
        contacto.setNombre(conReq.getNombre());
        contacto.setApellido(conReq.getApellido());
        contacto.setTelefonoArea(conReq.getTelefonoArea());
        contacto.setTelefonoNumero(conReq.getTelefonoNumero());
        contacto.setRelacion(conReq.getRelacion());
        if (conReq.getLocalidadId() != null) {
            Localidad locContacto = localidadRepo.findById(conReq.getLocalidadId())
                    .orElseThrow(() -> new NotFoundException("Localidad (contacto) no encontrada"));
            contacto.setLocalidad(locContacto);
        }
        contactoRepo.save(contacto);

        // CAMBIO: aplicar roles y cuota
        u.setRoles(nuevosRoles);
        u.setCuotaAsignada(cuota); // puede quedar null si no es Socio

        if (req.getPin() != null && !req.getPin().isBlank()) {
            u.setPin(passwordEncoder.encode(req.getPin()));
        }

        u.setFechaModificacion(OffsetDateTime.now());
        usuarioRepo.save(u);

        // (Opcional) Si querés, aquí podrías actualizar/crear UsuarioCuota cuando hay cuota,
        // o cancelar las pendientes cuando deja de ser Socio.

        return detalle(u.getId());
    }

    // ===========================
    // Obtener foto
    // ===========================
    @Transactional(readOnly = true)
    public byte[] obtenerFotoPerfil(Long usuarioId) {
        Usuario u = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado id=" + usuarioId));
        var p = u.getPersona();
        if (p == null || p.getFotoPerfil() == null || p.getFotoPerfil().length == 0) {
            return null;
        }
        return p.getFotoPerfil();
    }

    // ===========================
    // helpers
    // ===========================
    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

    // ===========================
// NUEVO: Buscar por DNI, nombre o apellido
// ===========================
@Transactional(readOnly = true)
public List<UsuarioListItem> buscarPorDniONombreOApellido(String query) {

    if (query == null || query.isBlank()) {
        throw badRequest("Debe ingresar un término de búsqueda");
    }

    query = query.trim();

    List<Usuario> resultados;

    // Si query es numérico → DNI
    if (query.matches("\\d+")) {
        resultados = usuarioRepo.buscarPorDni(query);
    } 
    else {
        resultados = usuarioRepo.buscarPorNombreApellido(query.toLowerCase());
    }

    return resultados.stream()
            .map(this::toListItem)
            .toList();
}




    @Transactional(readOnly = true)
    public Usuario buscarPorDni(String dni) {
        return usuarioRepo.findByDni(dni)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado (DNI: " + dni + ")"));
    }

    // ===== NUEVO: helpers de validación de rol/cuota =====

    /** Devuelve true si alguno de los roles tiene nombre o descripción "Socio" (case/acentos-insensible). */
    private boolean hasRolSocio(Set<Rol> roles) {
        if (roles == null || roles.isEmpty()) return false;
        return roles.stream().anyMatch(r -> {
            String n = normalize(r.getNombre());
            String d = normalize(r.getDescripcion());
            return "socio".equals(n) || "socio".equals(d) || n.contains("socio") || d.contains("socio");
        });
    }

    /** Normaliza texto a minúsculas sin acentos para comparaciones robustas. */
    private static String normalize(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}", "").toLowerCase().trim();
    }

    /** Construye un 400 Bad Request con mensaje uniforme. */
    private static ResponseStatusException badRequest(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
    
}
