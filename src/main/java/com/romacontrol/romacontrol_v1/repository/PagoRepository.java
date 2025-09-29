package com.romacontrol.romacontrol_v1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.romacontrol.romacontrol_v1.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByUsuarioIdAndCuotaMensualId(Long usuarioId, Long cuotaMensualId);

}
