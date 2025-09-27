package com.romacontrol.romacontrol_v1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romacontrol.romacontrol_v1.model.UsuarioCuota;
import com.romacontrol.romacontrol_v1.model.UsuarioCuotaEstado;

@Repository
public interface UsuarioCuotaRepository extends JpaRepository<UsuarioCuota, Long> {

  Optional<UsuarioCuota> findByUsuarioIdAndCuotaId(Long usuarioId, Long cuotaId);

  boolean existsByUsuarioIdAndCuotaId(Long usuarioId, Long cuotaId);

  List<UsuarioCuota> findByUsuario_DniAndEstadoIn(String dni, List<UsuarioCuotaEstado> estados);

  List<UsuarioCuota> findByUsuarioIdAndEstadoIn(Long usuarioId, List<UsuarioCuotaEstado> estados);
}
