-- Genero
INSERT INTO genero (id, nombre) VALUES
  (1,'MASCULINO'), (2,'FEMENINO'), (3,'OTRO')
ON CONFLICT (id) DO NOTHING;

-- Estado usuario
INSERT INTO estado_usuario (id, nombre) VALUES
  (1,'ACTIVO'), (2,'INACTIVO')
ON CONFLICT (id) DO NOTHING;

-- Roles
INSERT INTO rol (id, nombre, activo) VALUES
  (1,'ADMIN', true), (2,'PROFESOR', true)
ON CONFLICT (id) DO NOTHING;

-- Tipo de cuota (ejemplos)
INSERT INTO tipo_cuota (id, nombre) VALUES
  (1,'MENSUAL'), (2,'PROMO')
ON CONFLICT (id) DO NOTHING;
