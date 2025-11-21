package com.romacontrol.romacontrol_v1.repository.rol;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romacontrol.romacontrol_v1.model.RolPermiso;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {

    Optional<RolPermiso> findByRolIdAndPermisoId(Long rolId, Long permisoId);

    List<RolPermiso> findByRolId(Long rolId);

    List<RolPermiso> findByPermisoId(Long permisoId);
}
