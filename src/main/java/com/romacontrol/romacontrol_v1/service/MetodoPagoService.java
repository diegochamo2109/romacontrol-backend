package com.romacontrol.romacontrol_v1.service;

import java.util.List;
import java.util.Optional;

import com.romacontrol.romacontrol_v1.model.MetodoPago;

public interface MetodoPagoService {

    MetodoPago crear(MetodoPago metodoPago);

    List<MetodoPago> listarTodos();

    Optional<MetodoPago> obtenerPorId(Long id);

    MetodoPago actualizar(Long id, MetodoPago metodoPagoActualizado);

    void eliminar(Long id);

    MetodoPago cambiarEstado(Long id, boolean activo);
}
