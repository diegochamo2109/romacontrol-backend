

package com.romacontrol.romacontrol_v1.service.rol;

import java.util.List;

import com.romacontrol.romacontrol_v1.dto.rol.PermisoCrearSolicitud;
import com.romacontrol.romacontrol_v1.dto.rol.PermisoEditarSolicitud;
import com.romacontrol.romacontrol_v1.dto.rol.PermisoRespuesta;

public interface PermisoService {

    PermisoRespuesta crear(PermisoCrearSolicitud solicitud);

    PermisoRespuesta editar(Long id, PermisoEditarSolicitud solicitud);

    List<PermisoRespuesta> listar();

    PermisoRespuesta obtenerPorId(Long id);
}
