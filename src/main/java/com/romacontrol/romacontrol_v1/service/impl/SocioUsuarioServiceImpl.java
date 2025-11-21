package com.romacontrol.romacontrol_v1.service.impl;

import java.io.IOException;
import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.romacontrol.romacontrol_v1.dto.SocioUsuarioResponse;
import com.romacontrol.romacontrol_v1.dto.UsuarioUpdateRequest;
import com.romacontrol.romacontrol_v1.exception.NotFoundException;
import com.romacontrol.romacontrol_v1.model.ContactoUrgencia;
import com.romacontrol.romacontrol_v1.model.Genero;
import com.romacontrol.romacontrol_v1.model.Localidad;
import com.romacontrol.romacontrol_v1.model.Persona;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.repository.ContactoUrgenciaRepository;
import com.romacontrol.romacontrol_v1.repository.GeneroRepository;
import com.romacontrol.romacontrol_v1.repository.LocalidadRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;
import com.romacontrol.romacontrol_v1.service.SocioUsuarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SocioUsuarioServiceImpl implements SocioUsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final ContactoUrgenciaRepository contactoRepo;
    private final GeneroRepository generoRepo;
    private final LocalidadRepository localidadRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public SocioUsuarioResponse obtenerMisDatos(String dni) {
        Usuario u = usuarioRepo.findByDni(dni)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + dni));

        Persona p = u.getPersona();
        if (p == null)
            throw new NotFoundException("El usuario no tiene datos personales");

        var contactoOpt = contactoRepo.findFirstByPersonaIdOrderByIdDesc(p.getId());

        return SocioUsuarioResponse.builder()
                .id(u.getId())
                .dni(u.getDni())
                .nombre(p.getNombre())
                .apellido(p.getApellido())
                .fechaNacimiento(p.getFechaNacimiento())
                .domicilio(p.getDomicilio())
                .telefonoArea(p.getTelefonoArea())
                .telefonoNumero(p.getTelefonoNumero())
                .email(p.getEmail())
                .genero(p.getGenero() != null ? p.getGenero().getNombre() : null)
                .localidad(p.getLocalidad() != null ? p.getLocalidad().getNombre() : null)
                .provincia(p.getLocalidad() != null && p.getLocalidad().getProvincia() != null
                        ? p.getLocalidad().getProvincia().getNombre()
                        : null)
                .contactoNombre(contactoOpt.map(ContactoUrgencia::getNombre).orElse(null))
                .contactoApellido(contactoOpt.map(ContactoUrgencia::getApellido).orElse(null))
                .contactoTelefonoArea(contactoOpt.map(ContactoUrgencia::getTelefonoArea).orElse(null))
                .contactoTelefonoNumero(contactoOpt.map(ContactoUrgencia::getTelefonoNumero).orElse(null))
                .contactoRelacion(contactoOpt.map(ContactoUrgencia::getRelacion).orElse(null))
                .fotoPerfilBase64(p.getFotoPerfil() != null
                        ? Base64.getEncoder().encodeToString(p.getFotoPerfil())
                        : null)
                .build();
    }

    @Override
    @Transactional
    public void editarMisDatos(String dni, UsuarioUpdateRequest req) {
        Usuario u = usuarioRepo.findByDni(dni)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + dni));

        Persona p = u.getPersona();
        if (p == null)
            throw new NotFoundException("Persona no asociada");

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
            Localidad loc = localidadRepo.findById(per.getLocalidadId())
                    .orElseThrow(() -> new NotFoundException("Localidad no encontrada"));
            p.setLocalidad(loc);
        }

        var conReq = req.getContacto();
        ContactoUrgencia contacto = contactoRepo.findFirstByPersonaIdOrderByIdDesc(p.getId()).orElse(new ContactoUrgencia());
        contacto.setPersona(p);
        contacto.setNombre(conReq.getNombre());
        contacto.setApellido(conReq.getApellido());
        contacto.setTelefonoArea(conReq.getTelefonoArea());
        contacto.setTelefonoNumero(conReq.getTelefonoNumero());
        contacto.setRelacion(conReq.getRelacion());
        contactoRepo.save(contacto);

        if (req.getPin() != null && !req.getPin().isBlank()) {
            u.setPin(passwordEncoder.encode(req.getPin()));
        }

        usuarioRepo.save(u);
    }

    @Override
    @Transactional
    public Usuario actualizarFoto(String dni, MultipartFile file) throws IOException {
        Usuario u = usuarioRepo.findByDni(dni)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado: " + dni));
        Persona p = u.getPersona();
        if (p == null)
            throw new NotFoundException("Persona no asociada");
        byte[] bytes = file.getBytes();
        if (bytes.length == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo está vacío");
        p.setFotoPerfil(bytes);
        usuarioRepo.save(u);
        return u;
    }
}
