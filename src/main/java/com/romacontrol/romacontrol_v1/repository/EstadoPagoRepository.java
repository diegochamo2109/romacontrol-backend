package com.romacontrol.romacontrol_v1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romacontrol.romacontrol_v1.model.EstadoPago;

@Repository
public interface EstadoPagoRepository extends JpaRepository<EstadoPago, Long> {
  Optional<EstadoPago> findByNombreIgnoreCase(String nombre);
}
