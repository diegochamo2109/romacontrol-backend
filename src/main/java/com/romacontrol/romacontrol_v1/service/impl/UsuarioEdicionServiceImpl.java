package com.romacontrol.romacontrol_v1.service.impl;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.UsuarioDetailResponse;
import com.romacontrol.romacontrol_v1.dto.UsuarioUpdateRequest;
import com.romacontrol.romacontrol_v1.exception.NotFoundException;
import com.romacontrol.romacontrol_v1.model.ContactoUrgencia;
import com.romacontrol.romacontrol_v1.model.CuotaMensual;
import com.romacontrol.romacontrol_v1.model.Genero;
import com.romacontrol.romacontrol_v1.model.Localidad;
import com.romacontrol.romacontrol_v1.model.Persona;
import com.romacontrol.romacontrol_v1.model.Rol;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.repository.ContactoUrgenciaRepository;
import com.romacontrol.romacontrol_v1.repository.CuotaMensualRepository;
import com.romacontrol.romacontrol_v1.repository.GeneroRepository;
import com.romacontrol.romacontrol_v1.repository.LocalidadRepository;
import com.romacontrol.romacontrol_v1.repository.RolRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;
import com.romacontrol.romacontrol_v1.service.UsuarioEdicionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioEdicionServiceImpl implements UsuarioEdicionService {

    private final UsuarioRepository usuarioRepo;
    private final ContactoUrgenciaRepository contactoRepo;
    private final GeneroRepository generoRepo;
    private final LocalidadRepository localidadRepo;
    private final RolRepository rolRepo;
    private final CuotaMensualRepository cuotaMensualRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioDetailResponse editar(Long id, UsuarioUpdateRequest req, Long editorId) {
        Usuario usuario = usuarioRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Usuario no encontrado id=" + id));

        Persona persona = usuario.getPersona();
        if (persona == null) {
            throw new NotFoundException("Persona asociada no encontrada");
        }

        // 1. Actualizar datos personales
        var per = req.getPersona();
        persona.setNombre(per.getNombre());
        persona.setApellido(per.getApellido());
        persona.setFechaNacimiento(per.getFechaNacimiento());
        persona.setDomicilio(per.getDomicilio());
        persona.setTelefonoArea(per.getTelefonoArea());
        persona.setTelefonoNumero(per.getTelefonoNumero());
        persona.setEmail(per.getEmail());

        if (per.getGeneroId() != null) {
            Genero genero = generoRepo.findById(per.getGeneroId())
                .orElseThrow(() -> new NotFoundException("Género no encontrado"));
            persona.setGenero(genero);
        }
        if (per.getLocalidadId() != null) {
            Localidad localidad = localidadRepo.findById(per.getLocalidadId())
                .orElseThrow(() -> new NotFoundException("Localidad no encontrada"));
            persona.setLocalidad(localidad);
        }

        // 2. Actualizar contacto urgencia
        var c = req.getContacto();
        ContactoUrgencia contacto = contactoRepo.findFirstByPersonaIdOrderByIdDesc(persona.getId())
                .orElse(new ContactoUrgencia());

        contacto.setNombre(c.getNombre());
        contacto.setApellido(c.getApellido());
        contacto.setTelefonoArea(c.getTelefonoArea());
        contacto.setTelefonoNumero(c.getTelefonoNumero());
        contacto.setRelacion(c.getRelacion());
        contacto.setPersona(persona);

        if (c.getLocalidadId() != null) {
            Localidad locContacto = localidadRepo.findById(c.getLocalidadId())
                .orElseThrow(() -> new NotFoundException("Localidad (contacto) no encontrada"));
            contacto.setLocalidad(locContacto);
        }
        contactoRepo.save(contacto);

        // 3. Actualizar cuota
        CuotaMensual cuota = cuotaMensualRepo.findById(req.getCuotaMensualId())
            .orElseThrow(() -> new NotFoundException("Cuota mensual no encontrada"));
        usuario.setCuotaAsignada(cuota);

        // 4. Actualizar roles
        List<Long> rolIds = req.getRolIds();
        if (rolIds == null || rolIds.isEmpty()) {
            usuario.setRoles(new HashSet<>());
        } else {
            List<Rol> encontrados = rolRepo.findAllById(rolIds);
            if (encontrados.size() != rolIds.size()) {
                throw new NotFoundException("Uno o más roles no existen");
            }
            usuario.setRoles(new HashSet<>(encontrados));
        }

        // 5. Actualizar PIN (opcional)
        if (req.getPin() != null && !req.getPin().isBlank()) {
            usuario.setPin(passwordEncoder.encode(req.getPin()));
        }

        // 6. Actualizar estado activo si se envía
        if (req.getActivo() != null) {
            usuario.setActivo(req.getActivo());
        }

        usuario.setFechaModificacion(OffsetDateTime.now());
        usuarioRepo.save(usuario);

        // Devolver detalle actualizado (usamos detalle() del service original si hace falta)
        return UsuarioDetailResponse.builder()
            .id(usuario.getId())
            .dni(usuario.getDni())
            .nombre(usuario.getPersona().getNombre())
            .apellido(usuario.getPersona().getApellido())
            .activo(usuario.isActivo())
            .fechaCreacion(usuario.getFechaCreacion())
            .cuotaMensualId(usuario.getCuotaAsignada().getId())
            .cuotaDescripcion(usuario.getCuotaAsignada().getDescripcion())
            .rolIds(usuario.getRoles().stream().map(Rol::getId).filter(Objects::nonNull).toList())
            .rolNombres(usuario.getRoles().stream().map(Rol::getDescripcion).toList())
            .build();
    }
}