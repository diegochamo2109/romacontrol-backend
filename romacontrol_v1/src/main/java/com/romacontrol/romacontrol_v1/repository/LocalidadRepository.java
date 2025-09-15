package com.romacontrol.romacontrol_v1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.romacontrol.romacontrol_v1.model.Localidad;

public interface LocalidadRepository extends JpaRepository<Localidad, Long> {
     List<Localidad> findAllByProvinciaIdOrderByNombreAsc(Long provinciaId);
}
