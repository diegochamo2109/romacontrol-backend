

package com.romacontrol.romacontrol_v1.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.romacontrol.romacontrol_v1.model.Asistencia;
import com.romacontrol.romacontrol_v1.model.Usuario;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    boolean existsByUsuarioAndFechaRegistro(Usuario usuario, LocalDate fechaRegistro);
     // 🔹 Devuelve todas las asistencias de un usuario
    List<Asistencia> findByUsuarioId(Long usuarioId);
    List<Asistencia> findByUsuarioAndFechaRegistroAndHoraSalidaIsNull(Usuario usuario, LocalDate fechaRegistro);

    List<Asistencia> findByFechaRegistroAndHoraSalidaIsNull(LocalDate fechaRegistro);
    List<Asistencia> findByUsuarioIdOrderByFechaRegistroDescHoraRegistroDesc(Long usuarioId);
    List<Asistencia> findByUsuarioIdAndFechaRegistroBetweenOrderByFechaRegistroDescHoraRegistroDesc(Long usuarioId, LocalDate desde, LocalDate hasta);


}
