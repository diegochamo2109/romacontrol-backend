package com.romacontrol.romacontrol_v1.service;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.romacontrol.romacontrol_v1.dto.UsuarioDetailResponse;
import com.romacontrol.romacontrol_v1.dto.UsuarioUpdateRequest;
import com.romacontrol.romacontrol_v1.exception.NotFoundException;
import com.romacontrol.romacontrol_v1.model.ContactoUrgencia;
import com.romacontrol.romacontrol_v1.model.Genero;
import com.romacontrol.romacontrol_v1.model.Localidad;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.repository.ContactoUrgenciaRepository;
import com.romacontrol.romacontrol_v1.repository.GeneroRepository;
import com.romacontrol.romacontrol_v1.repository.LocalidadRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioSocioService {

    private final UsuarioRepository usuarioRepo;
    private final ContactoUrgenciaRepository contactoRepo;
    private final GeneroRepository generoRepo;
    private final LocalidadRepository localidadRepo;
    private final PasswordEncoder passwordEncoder;

    // ============================
    // OBTENER POR DNI
    // ============================
    @Transactional(readOnly = true)
    public UsuarioDetailResponse obtenerPorDni(String dni) {
        Usuario u = usuarioRepo.findByDni(dni)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado (DNI: " + dni + ")"));

        var p = u.getPersona();

        if (p == null)
            throw new NotFoundException("El usuario no tiene datos de persona asociados");

        var contactoOpt = contactoRepo.findFirstByPersonaIdOrderByIdDesc(p.getId());

        UsuarioDetailResponse dto = new UsuarioDetailResponse();
        dto.setId(u.getId());
        dto.setDni(u.getDni());
        dto.setNombre(p.getNombre());
        dto.setApellido(p.getApellido());
        dto.setFechaNacimiento(p.getFechaNacimiento());
        dto.setDomicilio(p.getDomicilio());
        dto.setTelefonoArea(p.getTelefonoArea());
        dto.setTelefonoNumero(p.getTelefonoNumero());
        dto.setEmail(p.getEmail());
        dto.setGeneroId(p.getGenero() != null ? p.getGenero().getId() : null);
        dto.setGeneroNombre(p.getGenero() != null ? p.getGenero().getNombre() : null);
        dto.setLocalidadId(p.getLocalidad() != null ? p.getLocalidad().getId() : null);
        dto.setProvinciaId(p.getLocalidad() != null && p.getLocalidad().getProvincia() != null
                ? p.getLocalidad().getProvincia().getId()
                : null);

        dto.setContactoNombre(contactoOpt.map(ContactoUrgencia::getNombre).orElse(null));
        dto.setContactoApellido(contactoOpt.map(ContactoUrgencia::getApellido).orElse(null));
        dto.setContactoTelefonoArea(contactoOpt.map(ContactoUrgencia::getTelefonoArea).orElse(null));
        dto.setContactoTelefonoNumero(contactoOpt.map(ContactoUrgencia::getTelefonoNumero).orElse(null));
        dto.setContactoRelacion(contactoOpt.map(ContactoUrgencia::getRelacion).orElse(null));
        dto.setContactoLocalidadId(contactoOpt.map(c -> c.getLocalidad() != null ? c.getLocalidad().getId() : null)
                .orElse(null));

        if (p.getFotoPerfil() != null) {
            dto.setFotoPerfilBase64(Base64.getEncoder().encodeToString(p.getFotoPerfil()));
        }

        return dto;
    }

    // ============================
    // EDITAR PROPIO PERFIL (solo datos permitidos)
    // ============================
    @Transactional
    public void editarPropioPerfil(String dni, UsuarioUpdateRequest req) {
        Usuario u = usuarioRepo.findByDni(dni)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado (DNI: " + dni + ")"));

        var p = u.getPersona();
        if (p == null)
            throw new NotFoundException("Persona asociada no encontrada");

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
            Localidad localidad = localidadRepo.findById(per.getLocalidadId())
                    .orElseThrow(() -> new NotFoundException("Localidad no encontrada"));
            p.setLocalidad(localidad);
        }

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
            Localidad loc = localidadRepo.findById(conReq.getLocalidadId())
                    .orElseThrow(() -> new NotFoundException("Localidad (contacto) no encontrada"));
            contacto.setLocalidad(loc);
        }

        contactoRepo.save(contacto);

        if (req.getPin() != null && !req.getPin().isBlank()) {
            u.setPin(passwordEncoder.encode(req.getPin()));
        }

        u.setFechaModificacion(OffsetDateTime.now());
        usuarioRepo.save(u);
    }

    // ============================
    // ACTUALIZAR FOTO PERFIL
    // ============================
    @Transactional
    public Usuario actualizarFoto(String dni, MultipartFile file) throws IOException {
        Usuario u = usuarioRepo.findByDni(dni)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado (DNI: " + dni + ")"));

        var p = u.getPersona();
        if (p == null)
            throw new NotFoundException("Persona asociada no encontrada");

        byte[] bytes = file.getBytes();
        if (bytes.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo de imagen está vacío");
        }

        p.setFotoPerfil(bytes);
        usuarioRepo.save(u);
        return u;
    }
}
