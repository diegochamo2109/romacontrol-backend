package com.romacontrol.romacontrol_v1.service.impl.socio;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.socio.MisDatosSocioUpdateRequest;
import com.romacontrol.romacontrol_v1.model.ContactoUrgencia;
import com.romacontrol.romacontrol_v1.model.Persona;
import com.romacontrol.romacontrol_v1.model.Usuario;
import com.romacontrol.romacontrol_v1.repository.GeneroRepository;
import com.romacontrol.romacontrol_v1.repository.LocalidadRepository;
import com.romacontrol.romacontrol_v1.repository.ProvinciaRepository;
import com.romacontrol.romacontrol_v1.repository.UsuarioRepository;
import com.romacontrol.romacontrol_v1.service.socio.SocioGuardarService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SocioGuardarServiceImpl implements SocioGuardarService {

    private final UsuarioRepository usuarioRepo;
    private final ProvinciaRepository provinciaRepo;
    private final LocalidadRepository localidadRepo;
    private final GeneroRepository generoRepo;
    private final PasswordEncoder passwordEncoder;

    // =======================================================
    // 🧩 Obtener usuario autenticado
    // =======================================================
    private Usuario obtenerUsuarioActual() {
        String dni = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        return usuarioRepo.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + dni));
    }

    // =======================================================
    // 💾 ACTUALIZAR MIS DATOS (SOCIO)
    // =======================================================
    @Override
    public void actualizarMisDatos(@Valid MisDatosSocioUpdateRequest req) {

        Usuario usuario = obtenerUsuarioActual();
        Persona persona = usuario.getPersona();

        if (persona == null) {
            throw new RuntimeException("El usuario no tiene datos personales cargados");
        }

        // =============================
        // 📌 ACTUALIZAR PIN (si viene)
        // =============================
        if (req.getPin() != null && !req.getPin().isBlank()) {
            usuario.setPin(passwordEncoder.encode(req.getPin()));
        }

        // =============================
        // 📌 ACTUALIZAR PERSONA
        // =============================
        var p = req.getPersona();

        persona.setNombre(p.getNombre());
        persona.setApellido(p.getApellido());
        persona.setFechaNacimiento(p.getFechaNacimiento());
        persona.setEmail(p.getEmail());
        persona.setTelefonoArea(p.getTelefonoArea());
        persona.setTelefonoNumero(p.getTelefonoNumero());

        // Domicilio = calle + número
        persona.setDomicilio(p.getCalle() + " " + p.getNumero());

        // Provincia y Localidad
        var provincia = provinciaRepo.findById(p.getProvinciaId())
                .orElseThrow(() -> new RuntimeException("Provincia no encontrada"));

        var localidad = localidadRepo.findById(p.getLocalidadId())
                .orElseThrow(() -> new RuntimeException("Localidad no encontrada"));

        persona.setLocalidad(localidad);

        // Género
        var genero = generoRepo.findById(p.getGeneroId())
                .orElseThrow(() -> new RuntimeException("Género no encontrado"));
        persona.setGenero(genero);

        // =============================
        // 📌 CONTACTO DE URGENCIA
        // =============================
        var c = req.getContacto();
        ContactoUrgencia contacto = persona.getContactoUrgencia();

        if (contacto == null) {
            contacto = new ContactoUrgencia();
            contacto.setPersona(persona);
        }

        contacto.setNombre(c.getNombre());
        contacto.setApellido(c.getApellido());
        contacto.setRelacion(c.getRelacion());
        contacto.setTelefonoArea(c.getTelefonoArea());
        contacto.setTelefonoNumero(c.getTelefonoNumero());

        var provinciaContacto = provinciaRepo.findById(c.getProvinciaId())
                .orElseThrow(() -> new RuntimeException("Provincia contacto no encontrada"));

        var localidadContacto = localidadRepo.findById(c.getLocalidadId())
                .orElseThrow(() -> new RuntimeException("Localidad contacto no encontrada"));

        contacto.setLocalidad(localidadContacto);

        // =============================
        // 💾 GUARDAR TODO
        // =============================
        usuarioRepo.save(usuario);
    }
}
