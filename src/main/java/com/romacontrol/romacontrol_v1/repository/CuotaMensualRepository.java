package com.romacontrol.romacontrol_v1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romacontrol.romacontrol_v1.model.CuotaMensual;

@Repository
public interface CuotaMensualRepository extends JpaRepository<CuotaMensual, Long> {
  // Agregá métodos de filtrado por fechas/estado si los necesitás luego
  boolean existsByDescripcion(String descripcion); // ✅ ESTA LÍNEA FALTABA
   List<CuotaMensual> findByActivaTrueOrderByFechaLimiteAsc(); // ✅ CORRECTO

}
