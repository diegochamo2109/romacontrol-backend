package com.romacontrol.romacontrol_v1.service;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.UsuarioCreateRequest;
import com.romacontrol.romacontrol_v1.dto.UsuarioDetailResponse;
import com.romacontrol.romacontrol_v1.dto.UsuarioListItem;
import com.romacontrol.romacontrol_v1.dto.UsuarioResponse;
import com.romacontrol.romacontrol_v1.exception.ConflictException;
import com.romacontrol.romacontrol_v1.exception.NotFoundException;
import com.romacontrol.romacontrol_v1.model.ContactoUrgencia;
import com.romacontrol.romacontrol_v1.model.Genero;
import com.romacontrol.romacontrol_v1.model.Localidad;
import com.romacontrol.romacontrol_v1.model.Persona;
import com.romacontrol.romacontrol_v1.model.Rol;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.repository.ContactoUrgenciaRepository;
import com.romacontrol.romacontrol_v1.repository.EstadoUsuarioRepository;
import com.romacontrol.romacontrol_v1.repository.GeneroRepository;
import com.romacontrol.romacontrol_v1.repository.LocalidadRepository;
import com.romacontrol.romacontrol_v1.repository.RolRepository;
import com.romacontrol.romacontrol_v1.repository.TipoCuotaRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

  private final UsuarioRepository usuarioRepo;
  private final ContactoUrgenciaRepository contactoRepo;
  private final GeneroRepository generoRepo;
  private final LocalidadRepository localidadRepo;
  private final EstadoUsuarioRepository estadoUsuarioRepo;
  private final RolRepository rolRepo;
  private final TipoCuotaRepository tipoCuotaRepo;
  private final PasswordEncoder passwordEncoder;

  // ===========================
  // CREAR (tu versión intacta)
  // ===========================
  @Transactional
  public UsuarioResponse crear(UsuarioCreateRequest req, Long creadoPorId) {
    final String dni = req.dni().trim();
    final String pinPlano = req.pin().trim();

    if (usuarioRepo.existsByDni(dni)) {
      throw new ConflictException("El DNI ya existe: " + dni);
    }

    Genero genero = generoRepo.findById(req.persona().generoId())
        .orElseThrow(() -> new NotFoundException("Género no encontrado"));

    Localidad locPersona = localidadRepo.findById(req.persona().localidadId())
        .orElseThrow(() -> new NotFoundException("Localidad (persona) no encontrada"));

    Localidad locContacto = localidadRepo.findById(req.contacto().localidadId())
        .orElseThrow(() -> new NotFoundException("Localidad (contacto) no encontrada"));

    var tipoCuota = tipoCuotaRepo.findById(req.tipoCuotaId())
        .orElseThrow(() -> new NotFoundException("Tipo de cuota no encontrado"));

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

    Set<Rol> roles = new HashSet<>();
    List<Long> rolIds = req.rolIds();
    if (rolIds != null && !rolIds.isEmpty()) {
      List<Rol> encontrados = rolRepo.findAllById(rolIds);
      if (encontrados.size() != rolIds.size()) {
        throw new NotFoundException("Uno o más roles no existen");
      }
      roles.addAll(encontrados);
    }

    Usuario usuario = Usuario.builder()
        .dni(dni)
        .pin(passwordEncoder.encode(pinPlano))
        .persona(persona)
        .activo(true)
        .tipoCuota(tipoCuota)
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
        .tipoCuotaId(u.getTipoCuota() != null ? u.getTipoCuota().getId() : null)

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

        .contactoNombre(contactoOpt.map(ContactoUrgencia::getNombre).orElse(null))
        .contactoApellido(contactoOpt.map(ContactoUrgencia::getApellido).orElse(null))
        .contactoTelefonoArea(contactoOpt.map(ContactoUrgencia::getTelefonoArea).orElse(null))
        .contactoTelefonoNumero(contactoOpt.map(ContactoUrgencia::getTelefonoNumero).orElse(null))
        .contactoRelacion(contactoOpt.map(ContactoUrgencia::getRelacion).orElse(null))

        .rolIds(rolIds)

        .creadoPorId(creadorId)
        .creadoPorNombre(creadorNombre)
        .creadoPorDni(creadorDni)
        .build();
  }

  // ===========================
  // helpers
  // ===========================
  private static String nullSafe(String s) { return s == null ? "" : s; }
}
