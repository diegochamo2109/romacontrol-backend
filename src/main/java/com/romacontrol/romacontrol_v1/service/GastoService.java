

package com.romacontrol.romacontrol_v1.service;

import java.util.List;

import com.romacontrol.romacontrol_v1.dto.CreacionGastoSolicitud;
import com.romacontrol.romacontrol_v1.dto.GastoEdicionSolicitud;
import com.romacontrol.romacontrol_v1.dto.GastoListadoRespuesta;
import com.romacontrol.romacontrol_v1.dto.GastoRespuesta;

/**
 * Servicio que gestiona la lógica de negocio de los gastos del sistema RomaControl.
 */
public interface GastoService {

    // =======================
    // 🔹 EXISTENTES
    // =======================
    GastoRespuesta crearGasto(CreacionGastoSolicitud solicitud, String dniUsuarioLogueado);

    List<GastoRespuesta> listarTodos();


    // =======================
    // 🔹 NUEVOS MÉTODOS
    // =======================

    /**
     * Devuelve una lista completa de gastos para el módulo "Gestionar Gastos",
     * incluyendo su estado lógico y datos del registrador.
     */
    List<GastoListadoRespuesta> listarParaGestion();

    /**
     * Edita los datos de un gasto existente (tipo de gasto, monto, descripción).
     */
    GastoListadoRespuesta editar(Long id, GastoEdicionSolicitud solicitud, String dniUsuarioLogueado);

    /**
     * Cambia el estado lógico de un gasto (activo/inactivo) sin eliminarlo físicamente.
     */
    void cambiarActivo(Long id, boolean activo, String dniUsuarioLogueado);
}
