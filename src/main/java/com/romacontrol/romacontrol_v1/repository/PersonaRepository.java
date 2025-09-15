package com.romacontrol.romacontrol_v1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.romacontrol.romacontrol_v1.model.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long> {}
