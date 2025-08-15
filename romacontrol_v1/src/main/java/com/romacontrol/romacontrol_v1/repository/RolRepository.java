
package com.romacontrol.romacontrol_v1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.romacontrol.romacontrol_v1.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
}
