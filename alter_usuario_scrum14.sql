ALTER TABLE Usuario ADD COLUMN nombre_usuario VARCHAR(50) NULL UNIQUE AFTER apellido;
ALTER TABLE Usuario ADD COLUMN alcance_datos VARCHAR(20) NOT NULL DEFAULT 'TODAS' AFTER activo;
ALTER TABLE Usuario ADD COLUMN id_planta_asignada INT NULL AFTER alcance_datos;
ALTER TABLE Usuario ADD CONSTRAINT fk_usuario_planta FOREIGN KEY (id_planta_asignada) REFERENCES Planta(id);
