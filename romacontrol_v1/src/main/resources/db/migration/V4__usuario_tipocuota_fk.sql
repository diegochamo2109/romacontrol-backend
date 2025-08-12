DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name='usuario' AND column_name='tipo_cuota_id'
  ) THEN
    ALTER TABLE usuario ADD COLUMN tipo_cuota_id BIGINT;
    ALTER TABLE usuario
      ADD CONSTRAINT fk_usuario_tipo_cuota
      FOREIGN KEY (tipo_cuota_id) REFERENCES tipo_cuota(id);
    CREATE INDEX idx_usuario_tipo_cuota ON usuario(tipo_cuota_id);
  END IF;
END$$;
