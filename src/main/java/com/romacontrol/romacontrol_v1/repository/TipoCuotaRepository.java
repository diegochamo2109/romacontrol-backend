package com.romacontrol.romacontrol_v1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romacontrol.romacontrol_v1.model.TipoCuota;

@Repository
public interface TipoCuotaRepository extends JpaRepository<TipoCuota, Long> {
  // Optional<TipoCuota> findByNombreIgnoreCase(String nombre); // si más adelante lo necesitás
    List<TipoCuota> findAllByOrderByNombreAsc();
     Optional<TipoCuota> findByNombre(String nombre);  // 👈 agregado
     Optional<TipoCuota> findByNombreIgnoreCase(String nombre);
}
    