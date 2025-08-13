package com.romacontrol.romacontrol_v1.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.romacontrol.romacontrol_v1.model.ContactoUrgencia;

public interface ContactoUrgenciaRepository extends JpaRepository<ContactoUrgencia, Long> {

  // trae el último contacto cargado para una persona (sirve para detalle)
    Optional<ContactoUrgencia> findFirstByPersonaIdOrderByIdDesc(Long personaId);
}
