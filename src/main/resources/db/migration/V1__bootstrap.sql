-- ================================
-- ROMACONTROL_V1 - BOOTSTRAP DB
-- ================================

-- 1) Catálogos iniciales (IDs fijos para poder usar índices parciales)
INSERT INTO estado_pago (id, nombre) VALUES
  (1, 'PENDIENTE'),
  (2, 'PAGADO'),
  (3, 'ANULADO')
ON CONFLICT (id) DO NOTHING;

INSERT INTO metodo_pago (id, nombre) VALUES
  (1, 'EFECTIVO'),
  (2, 'TRANSFERENCIA'),
  (3, 'TARJETA'),
  (4, 'DEBITO')
ON CONFLICT (id) DO NOTHING;

-- 2) Índices de rendimiento

-- Usuario
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='usuario' AND indexname='uk_usuario_dni') THEN
    -- Único por DNI (ya está como unique constraint en JPA, reforzamos)
    EXECUTE 'CREATE UNIQUE INDEX uk_usuario_dni ON usuario (dni)';
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='usuario' AND indexname='idx_usuario_activo_true') THEN
    EXECUTE 'CREATE INDEX idx_usuario_activo_true ON usuario (activo) WHERE activo = true';
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='usuario' AND indexname='idx_usuario_estado') THEN
    EXECUTE 'CREATE INDEX idx_usuario_estado ON usuario (estado_usuario_id)';
  END IF;
END$$;

-- Persona: búsquedas por prefijo (LIKE 'Die%')
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='persona' AND indexname='idx_persona_apellido_prefix') THEN
    EXECUTE 'CREATE INDEX idx_persona_apellido_prefix ON persona (apellido text_pattern_ops)';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='persona' AND indexname='idx_persona_nombre_prefix') THEN
    EXECUTE 'CREATE INDEX idx_persona_nombre_prefix ON persona (nombre text_pattern_ops)';
  END IF;
END$$;

-- Usuario-Rol
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='usuario_roles' AND indexname='idx_usuario_rol_rol') THEN
    EXECUTE 'CREATE INDEX idx_usuario_rol_rol ON usuario_roles (rol_id, usuario_id)';
  END IF;
END$$;

-- Cuota mensual
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='cuota_mensual' AND indexname='idx_cuota_estado_tipo') THEN
    EXECUTE 'CREATE INDEX idx_cuota_estado_tipo ON cuota_mensual (estado_cuota_id, tipo_cuota_id)';
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='cuota_mensual' AND indexname='idx_cuota_fechas') THEN
    EXECUTE 'CREATE INDEX idx_cuota_fechas ON cuota_mensual (fecha_limite, fecha_alta)';
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='cuota_mensual' AND indexname='idx_cuota_activa_true') THEN
    EXECUTE 'CREATE INDEX idx_cuota_activa_true ON cuota_mensual (activa) WHERE activa = true';
  END IF;
END$$;

-- Pago
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='pago' AND indexname='idx_pago_estado_fecha') THEN
    EXECUTE 'CREATE INDEX idx_pago_estado_fecha ON pago (estado_pago_id, fecha_pago)';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='pago' AND indexname='idx_pago_cobrador_fecha') THEN
    EXECUTE 'CREATE INDEX idx_pago_cobrador_fecha ON pago (cobrado_por, fecha_pago)';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='pago' AND indexname='idx_pago_metodo_fecha') THEN
    EXECUTE 'CREATE INDEX idx_pago_metodo_fecha ON pago (metodo_pago_id, fecha_pago)';
  END IF;
END$$;

-- Unicidad condicional: (usuario, cuota) sólo cuando estado = PAGADO (id=2)
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='pago' AND indexname='uk_pago_unico_pagado') THEN
    EXECUTE 'CREATE UNIQUE INDEX uk_pago_unico_pagado ON pago (usuario_id, cuota_mensual_id) WHERE estado_pago_id = 2';
  END IF;
END$$;

-- Caja / CajaPago
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='caja' AND indexname='idx_caja_usuario') THEN
    EXECUTE 'CREATE INDEX idx_caja_usuario ON caja (usuario_id)';
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='caja_pago' AND indexname='idx_caja_pago_caja') THEN
    EXECUTE 'CREATE INDEX idx_caja_pago_caja ON caja_pago (caja_id)';
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='caja_pago' AND indexname='idx_caja_pago_pago') THEN
    EXECUTE 'CREATE INDEX idx_caja_pago_pago ON caja_pago (pago_id)';
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE tablename='caja_pago' AND indexname='uk_caja_pago_pago_unico') THEN
    EXECUTE 'CREATE UNIQUE INDEX uk_caja_pago_pago_unico ON caja_pago (pago_id)';
  END IF;
END$$;
