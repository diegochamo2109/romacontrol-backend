

package com.romacontrol.romacontrol_v1.service.impl.rol;



import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.dto.rol.CambioEstadoRolSolicitud;
import com.romacontrol.romacontrol_v1.dto.rol.PermisoRolRespuesta;
import com.romacontrol.romacontrol_v1.dto.rol.RolCrearSolicitud;
import com.romacontrol.romacontrol_v1.dto.rol.RolDetalleRespuesta;
import com.romacontrol.romacontrol_v1.dto.rol.RolEditarSolicitud;
import com.romacontrol.romacontrol_v1.dto.rol.RolListadoRespuesta;
import com.romacontrol.romacontrol_v1.model.Permiso;
import com.romacontrol.romacontrol_v1.model.Rol;
import com.romacontrol.romacontrol_v1.model.RolPermiso;
import com.romacontrol.romacontrol_v1.repository.rol.PermisoRepository;
import com.romacontrol.romacontrol_v1.repository.rol.RolPermisoRepository;
import com.romacontrol.romacontrol_v1.repository.rol.RolRepository;
import com.romacontrol.romacontrol_v1.service.rol.RolService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final RolPermisoRepository rolPermisoRepository;

    @Override
    public RolDetalleRespuesta crear(RolCrearSolicitud solicitud) {
        String nombreNormalizado = solicitud.getNombre().trim().toUpperCase();

        if (rolRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre: " + nombreNormalizado);
        }

        Rol rol = Rol.builder()
                .nombre(nombreNormalizado)
                .descripcion(solicitud.getDescripcion())
                .activo(true)
                .build();

        rol = rolRepository.save(rol);

        asociarPermisosAlRol(rol, solicitud.getPermisosIds());

        return mapearDetalle(rol);
    }

    @Override
    public RolDetalleRespuesta editar(Long id, RolEditarSolicitud solicitud) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con id " + id));

        String nombreNormalizado = solicitud.getNombre().trim().toUpperCase();
        if (rolRepository.existsByNombreIgnoreCaseAndIdNot(nombreNormalizado, id)) {
            throw new IllegalArgumentException("Ya existe otro rol con el nombre: " + nombreNormalizado);
        }

        rol.setNombre(nombreNormalizado);
        rol.setDescripcion(solicitud.getDescripcion());

        if (solicitud.getActivo() != null) {
            rol.setActivo(solicitud.getActivo());
        }

        sincronizarPermisosDelRol(rol, solicitud.getPermisosIds());

        return mapearDetalle(rol);
    }

    @Override
    public void cambiarEstado(Long id, CambioEstadoRolSolicitud solicitud) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con id " + id));

        rol.setActivo(solicitud.getActivo());
        rolRepository.save(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolListadoRespuesta> listar() {
        return rolRepository.findAll().stream()
                .sorted(Comparator.comparing(Rol::getId))
                .map(rol -> {
                    int cantidad = (int) rol.getRolPermisos().stream()
                            .filter(RolPermiso::isHabilitado)
                            .count();

                    return RolListadoRespuesta.builder()
                            .id(rol.getId())
                            .nombre(rol.getNombre())
                            .descripcion(rol.getDescripcion())
                            .fechaCreacion(rol.getFechaCreacion())
                            .activo(rol.isActivo())
                            .cantidadPermisosHabilitados(cantidad)
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RolDetalleRespuesta obtenerDetalle(Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con id " + id));

        return mapearDetalle(rol);
    }

    // ==============================================
    // 🔹 Métodos privados
    // ==============================================

    private void asociarPermisosAlRol(Rol rol, List<Long> permisosIds) {
        if (permisosIds == null || permisosIds.isEmpty()) {
            return;
        }

        for (Long permisoId : permisosIds) {
            Permiso permiso = permisoRepository.findById(permisoId)
                    .orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado con id " + permisoId));

            RolPermiso rp = RolPermiso.builder()
                    .rol(rol)
                    .permiso(permiso)
                    .habilitado(true)
                    .build();

            rol.agregarRolPermiso(rp);
        }
    }

    /**
     * Recibe la lista final de permisos que debe tener el rol
     * y sincroniza la tabla rol_permiso (habilitado true/false o crea nuevos).
     */
    private void sincronizarPermisosDelRol(Rol rol, List<Long> permisosIds) {
        Set<Long> idsDeseados = new HashSet<>();
        if (permisosIds != null) {
            idsDeseados.addAll(permisosIds);
        }

        // Deshabilitar o mantener los existentes
        for (RolPermiso rp : new ArrayList<>(rol.getRolPermisos())) {
            Long idPermiso = rp.getPermiso().getId();
            if (idsDeseados.contains(idPermiso)) {
                // sigue existiendo -> habilitado
                rp.setHabilitado(true);
                idsDeseados.remove(idPermiso);
            } else {
                // ya no está en la lista -> deshabilitar
                rp.setHabilitado(false);
            }
        }

        // Crear nuevos vínculos para los IDs restantes
        for (Long nuevoIdPermiso : idsDeseados) {
            Permiso permiso = permisoRepository.findById(nuevoIdPermiso)
                    .orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado con id " + nuevoIdPermiso));

            RolPermiso rpNuevo = RolPermiso.builder()
                    .rol(rol)
                    .permiso(permiso)
                    .habilitado(true)
                    .build();

            rol.agregarRolPermiso(rpNuevo);
        }
    }

    private RolDetalleRespuesta mapearDetalle(Rol rol) {
        List<PermisoRolRespuesta> permisos = rol.getRolPermisos().stream()
                .map(rp -> PermisoRolRespuesta.builder()
                        .idPermiso(rp.getPermiso().getId())
                        .ruta(rp.getPermiso().getRuta())
                        .descripcion(rp.getPermiso().getDescripcion())
                        .activoPermiso(rp.getPermiso().isActivo())
                        .habilitadoEnRol(rp.isHabilitado())
                        .build())
                .sorted(Comparator.comparing(PermisoRolRespuesta::getRuta))
                .toList();

        return RolDetalleRespuesta.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .fechaCreacion(rol.getFechaCreacion())
                .activo(rol.isActivo())
                .permisos(permisos)
                .build();
    }
}
