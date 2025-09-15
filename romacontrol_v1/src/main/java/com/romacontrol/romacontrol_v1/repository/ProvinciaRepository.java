package com.romacontrol.romacontrol_v1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.romacontrol.romacontrol_v1.model.Provincia;

public interface ProvinciaRepository extends JpaRepository<Provincia, Long> {
        List<Provincia> findAllByOrderByNombreAsc();
}
