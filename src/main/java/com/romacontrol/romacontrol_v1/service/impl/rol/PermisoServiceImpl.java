

package com.romacontrol.romacontrol_v1.service.impl.rol;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.rol.PermisoCrearSolicitud;
import com.romacontrol.romacontrol_v1.dto.rol.PermisoEditarSolicitud;
import com.romacontrol.romacontrol_v1.dto.rol.PermisoRespuesta;
import com.romacontrol.romacontrol_v1.model.Permiso;
import com.romacontrol.romacontrol_v1.repository.rol.PermisoRepository;
import com.romacontrol.romacontrol_v1.service.rol.PermisoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PermisoServiceImpl implements PermisoService {

    private final PermisoRepository permisoRepository;

    @Override
    public PermisoRespuesta crear(PermisoCrearSolicitud solicitud) {
        String rutaNormalizada = solicitud.getRuta().trim();

        if (permisoRepository.existsByRutaIgnoreCase(rutaNormalizada)) {
            throw new IllegalArgumentException("Ya existe un permiso con la ruta: " + rutaNormalizada);
        }

        Permiso permiso = Permiso.builder()
                .ruta(rutaNormalizada)
                .titulo(solicitud.getTitulo())       // 👈 NUEVO
                .descripcion(solicitud.getDescripcion())
                .activo(true)
                .build();

        permiso = permisoRepository.save(permiso);
        return mapearRespuesta(permiso);
    }

    @Override
    public PermisoRespuesta editar(Long id, PermisoEditarSolicitud solicitud) {
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado con id " + id));

        String rutaNormalizada = solicitud.getRuta().trim();
        if (permisoRepository.existsByRutaIgnoreCaseAndIdNot(rutaNormalizada, id)) {
            throw new IllegalArgumentException("Ya existe otro permiso con la ruta: " + rutaNormalizada);
        }

        permiso.setRuta(rutaNormalizada);
        permiso.setTitulo(solicitud.getTitulo());        // 👈 NUEVO
        permiso.setDescripcion(solicitud.getDescripcion());

        if (solicitud.getActivo() != null) {
            permiso.setActivo(solicitud.getActivo());
        }

        return mapearRespuesta(permiso);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermisoRespuesta> listar() {
        return permisoRepository.findAll().stream()
                .sorted(Comparator.comparing(Permiso::getRuta))
                .map(this::mapearRespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PermisoRespuesta obtenerPorId(Long id) {
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado con id " + id));
        return mapearRespuesta(permiso);
    }

    private PermisoRespuesta mapearRespuesta(Permiso p) {
        return PermisoRespuesta.builder()
                .id(p.getId())
                .ruta(p.getRuta())
                .titulo(p.getTitulo())             // 👈 NUEVO
                .descripcion(p.getDescripcion())
                .activo(p.isActivo())
                .build();
    }
}
