package com.romacontrol.romacontrol_v1.repository.cierre;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.romacontrol.romacontrol_v1.model.cierre.CierreParcial;

public interface CierreParcialRepository extends JpaRepository<CierreParcial, Long> {

    // Buscar los cierres de un usuario en una fecha
    List<CierreParcial> findByUsuario_IdAndFecha(Long usuarioId, LocalDate fecha);

    // Buscar todos los cierres de una fecha
    List<CierreParcial> findByFecha(LocalDate fecha);

    // Saber si ya existe cierre en esa fecha para ese usuario
    boolean existsByUsuario_IdAndFecha(Long usuarioId, LocalDate fecha);
}
