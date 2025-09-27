package com.romacontrol.romacontrol_v1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romacontrol.romacontrol_v1.model.MetodoPago;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
  Optional<MetodoPago> findByNombreIgnoreCase(String nombre);
}
