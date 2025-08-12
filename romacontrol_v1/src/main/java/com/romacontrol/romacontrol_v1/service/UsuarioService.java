package com.romacontrol.romacontrol_v1.service;

import java.time.OffsetDateTime;
import java.util.HashSet;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.UsuarioCreateRequest;
import com.romacontrol.romacontrol_v1.dto.UsuarioResponse;
import com.romacontrol.romacontrol_v1.exception.ConflictException;
import com.romacontrol.romacontrol_v1.exception.NotFoundException;
import com.romacontrol.romacontrol_v1.model.ContactoUrgencia;
import com.romacontrol.romacontrol_v1.model.Genero;
import com.romacontrol.romacontrol_v1.model.Localidad;
import com.romacontrol.romacontrol_v1.model.Persona;
import com.romacontrol.romacontrol_v1.model.TipoCuota;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.repository.ContactoUrgenciaRepository;
import com.romacontrol.romacontrol_v1.repository.EstadoUsuarioRepository;
import com.romacontrol.romacontrol_v1.repository.GeneroRepository;
import com.romacontrol.romacontrol_v1.repository.LocalidadRepository;
import com.romacontrol.romacontrol_v1.repository.RolRepository;
import com.romacontrol.romacontrol_v1.repository.TipoCuotaRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;

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

  @Transactional
  public UsuarioResponse crear(UsuarioCreateRequest req, Long creadoPorId) {

    if (usuarioRepo.existsByDni(req.dni())) {
      throw new ConflictException("El DNI ya existe: " + req.dni());
    }

    Genero genero = generoRepo.findById(req.persona().generoId())
        .orElseThrow(() -> new NotFoundException("Género no encontrado"));

    Localidad locPersona = localidadRepo.findById(req.persona().localidadId())
        .orElseThrow(() -> new NotFoundException("Localidad (persona) no encontrada"));

    Localidad locContacto = localidadRepo.findById(req.contacto().localidadId())
        .orElseThrow(() -> new NotFoundException("Localidad (contacto) no encontrada"));

    TipoCuota tipoCuota = tipoCuotaRepo.findById(req.tipoCuotaId())
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

    Usuario usuario = Usuario.builder()
        .dni(req.dni())
        .pin(passwordEncoder.encode(req.pin()))
        .persona(persona)               // se persiste por CascadeType.ALL en Usuario.persona
        .activo(true)
        .tipoCuota(tipoCuota)
        .fechaCreacion(OffsetDateTime.now())
        .roles(new HashSet<>(rolRepo.findAllById(req.rolIds())))
        .build();

    estadoUsuarioRepo.findByNombre("ACTIVO").ifPresent(usuario::setEstadoUsuario);

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

    return new UsuarioResponse(saved.getId(),
        saved.getDni(),
        saved.getPersona().getNombre(),
        saved.getPersona().getApellido());
  }
}
