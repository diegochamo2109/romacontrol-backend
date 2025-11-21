package com.romacontrol.romacontrol_v1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romacontrol.romacontrol_v1.model.TipoGasto;

@Repository
public interface TipoGastoRepository extends JpaRepository<TipoGasto, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
}
