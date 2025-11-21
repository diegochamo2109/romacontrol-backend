package com.romacontrol.romacontrol_v1.repository.cierre;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romacontrol.romacontrol_v1.model.cierre.CierreCompleto;

@Repository
public interface CierreCompletoRepository extends JpaRepository<CierreCompleto, Long> {

    // ⭐ CORRECTO para buscar cierres completos del día
    List<CierreCompleto> findByFechaHoraCierreBetween(OffsetDateTime inicio, OffsetDateTime fin);

    // ⭐ Saber si ya existe un cierre completo ese día
    boolean existsByFechaHoraCierreBetween(OffsetDateTime inicio, OffsetDateTime fin);

}
