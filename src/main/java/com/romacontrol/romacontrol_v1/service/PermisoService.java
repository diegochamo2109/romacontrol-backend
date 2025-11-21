package com.romacontrol.romacontrol_v1.service;

import java.util.List;

import com.romacontrol.romacontrol_v1.model.Permiso;

public interface PermisoService {

  // 🔹 Listar todos los permisos
  List<Permiso> listarTodos();

  // 🔹 Crear un nuevo permiso
  Permiso crearPermiso(Permiso permiso);

  // 🔹 Obtener un permiso por su ID
  Permiso obtenerPorId(Long id);

  // 🔹 Actualizar un permiso existente
  Permiso actualizarPermiso(Long id, Permiso permiso);

  // 🔹 Eliminar un permiso
  void eliminarPermiso(Long id);
}
