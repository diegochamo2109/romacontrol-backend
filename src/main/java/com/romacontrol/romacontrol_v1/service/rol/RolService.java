

package com.romacontrol.romacontrol_v1.service.rol;

import java.util.List;

import com.romacontrol.romacontrol_v1.dto.rol.CambioEstadoRolSolicitud;
import com.romacontrol.romacontrol_v1.dto.rol.RolCrearSolicitud;
import com.romacontrol.romacontrol_v1.dto.rol.RolDetalleRespuesta;
import com.romacontrol.romacontrol_v1.dto.rol.RolEditarSolicitud;
import com.romacontrol.romacontrol_v1.dto.rol.RolListadoRespuesta;

public interface RolService {

    RolDetalleRespuesta crear(RolCrearSolicitud solicitud);

    RolDetalleRespuesta editar(Long id, RolEditarSolicitud solicitud);

    void cambiarEstado(Long id, CambioEstadoRolSolicitud solicitud);

    List<RolListadoRespuesta> listar();

    RolDetalleRespuesta obtenerDetalle(Long id);
}
