package com.romacontrol.romacontrol_v1.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romacontrol.romacontrol_v1.model.MetodoPago;
import com.romacontrol.romacontrol_v1.repository.MetodoPagoRepository;
import com.romacontrol.romacontrol_v1.service.MetodoPagoService;

@Service
@Transactional
public class MetodoPagoServiceImpl implements MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;

    public MetodoPagoServiceImpl(MetodoPagoRepository metodoPagoRepository) {
        this.metodoPagoRepository = metodoPagoRepository;
    }

    @Override
    public MetodoPago crear(MetodoPago metodoPago) {
        if (metodoPagoRepository.existsByNombreIgnoreCase(metodoPago.getNombre())) {
            throw new IllegalArgumentException("Ya existe un método de pago con ese nombre.");
        }
        return metodoPagoRepository.save(metodoPago);
    }

    @Override
    public List<MetodoPago> listarTodos() {
        return metodoPagoRepository.findAll();
    }

    @Override
    public Optional<MetodoPago> obtenerPorId(Long id) {
        return metodoPagoRepository.findById(id);
    }

    @Override
    public MetodoPago actualizar(Long id, MetodoPago metodoPagoActualizado) {
        MetodoPago metodoExistente = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Método de pago no encontrado"));

        // Evita duplicar nombres
        if (!metodoExistente.getNombre().equalsIgnoreCase(metodoPagoActualizado.getNombre()) &&
                metodoPagoRepository.existsByNombreIgnoreCase(metodoPagoActualizado.getNombre())) {
            throw new IllegalArgumentException("Ya existe otro método de pago con ese nombre.");
        }

        metodoExistente.setNombre(metodoPagoActualizado.getNombre());
        metodoExistente.setDescripcion(metodoPagoActualizado.getDescripcion());
        metodoExistente.setActivo(metodoPagoActualizado.isActivo());

        return metodoPagoRepository.save(metodoExistente);
    }

    @Override
    public void eliminar(Long id) {
        MetodoPago metodo = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Método de pago no encontrado"));
        metodoPagoRepository.delete(metodo);
    }

    @Override
    public MetodoPago cambiarEstado(Long id, boolean activo) {
        MetodoPago metodo = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Método de pago no encontrado"));

        metodo.setActivo(activo);
        return metodoPagoRepository.save(metodo);
    }
}
