package com.romacontrol.romacontrol_v1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romacontrol.romacontrol_v1.model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
  // Podés sumar queries por rango de fechas / cobrador / método de pago para reportes
}
