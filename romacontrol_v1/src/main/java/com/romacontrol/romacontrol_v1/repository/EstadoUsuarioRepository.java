package com.romacontrol.romacontrol_v1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.romacontrol.romacontrol_v1.model.EstadoCuota;
import com.romacontrol.romacontrol_v1.model.EstadoUsuario;

public interface EstadoUsuarioRepository extends JpaRepository<EstadoUsuario, Long> {
    Optional<EstadoUsuario> findByNombre(String nombre);
    
    
  List<EstadoCuota> findAllByOrderByNombreAsc();
}
