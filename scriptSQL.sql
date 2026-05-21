-- ============================================================
--  AquaVitae – Script de base de datos
--  GCP Cloud SQL + Firebase Auth
--  Versión: Mayo 2026 — correcciones aplicadas:
--    1. idx_planta_tipo_fecha en Estado_Planta
--    2. fuente_campo en Score_Config
--    3. activa incluido en trg_planta_delete
-- ============================================================

CREATE DATABASE IF NOT EXISTS AquaVitaeDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE AquaVitaeDB;

-- ------------------------------------------------------------
-- 1. EMPRESA
-- ------------------------------------------------------------
CREATE TABLE Empresa (
    id     INT          PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(300) NOT NULL
);

-- ------------------------------------------------------------
-- 2. ROL
-- ------------------------------------------------------------
CREATE TABLE Rol (
    id          INT          PRIMARY KEY AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300)
);

-- ------------------------------------------------------------
-- 3. PERMISO
-- ------------------------------------------------------------
CREATE TABLE Permiso (
    id          INT          PRIMARY KEY AUTO_INCREMENT,
    clave       VARCHAR(100) NOT NULL UNIQUE,
    modulo      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(600)
);

-- ------------------------------------------------------------
-- 4. ROL_PERMISO
-- ------------------------------------------------------------
CREATE TABLE Rol_Permiso (
    id_rol     INT NOT NULL,
    id_permiso INT NOT NULL,
    PRIMARY KEY (id_rol, id_permiso),
    FOREIGN KEY (id_rol)     REFERENCES Rol    (id),
    FOREIGN KEY (id_permiso) REFERENCES Permiso(id)
);

-- ------------------------------------------------------------
-- 5. REGION
-- ------------------------------------------------------------
CREATE TABLE Region (
    id     INT          PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    pais   VARCHAR(100) DEFAULT 'México'
);

-- ------------------------------------------------------------
-- 6. UBICACION
--    location_id  → parámetro para Open-Meteo y NASA POWER
--    id_estado_smn / id_municipio_smn → parámetros para SMN
-- ------------------------------------------------------------
CREATE TABLE Ubicacion (
    id               INT          PRIMARY KEY AUTO_INCREMENT,
    location_id      INT          UNIQUE,
    nombre           VARCHAR(150) NOT NULL,
    latitud          FLOAT        NOT NULL,
    longitud         FLOAT        NOT NULL,
    elevation        INT,
    id_estado_smn    INT,
    id_municipio_smn INT
);

-- ------------------------------------------------------------
-- 7. FUENTE_AGUA
--    Disponibilidad hídrica de una ubicación según CONAGUA.
--    Usada para el score de zonas candidatas (Dashboard 2).
--    disponibilidad: 'Alta' | 'Media' | 'Baja'
--    estatus_conagua: 'Sobreexplotado' | 'Equilibrio' | 'Subexplotado'
-- ------------------------------------------------------------
CREATE TABLE Fuente_Agua (
    id                   INT          PRIMARY KEY AUTO_INCREMENT,
    id_ubicacion         INT          NOT NULL,
    nombre               VARCHAR(150),
    tipo                 VARCHAR(50),             -- 'acuífero'|'superficial'|'pluvial'
    disponibilidad       VARCHAR(20)  NOT NULL,
    estatus_conagua      VARCHAR(100),
    volumen_concesion_m3 DECIMAL(15,2),
    fecha_actualizacion  TIMESTAMP,
    FOREIGN KEY (id_ubicacion) REFERENCES Ubicacion(id),
    INDEX idx_ubicacion (id_ubicacion)
);

-- ------------------------------------------------------------
-- 8. PLANTA
-- ------------------------------------------------------------
CREATE TABLE Planta (
    id                         INT           PRIMARY KEY AUTO_INCREMENT,
    nombre                     VARCHAR(300)  NOT NULL,
    id_empresa                 INT,
    id_ubicacion               INT,
    id_region                  INT,
    activa                     BOOL          DEFAULT TRUE,
    umbral_alerta              FLOAT         DEFAULT 0.75,
    costo_cierre_mxn           DECIMAL(15,2),
    costo_apertura_mxn         DECIMAL(15,2),
    costo_operacion_diaria_mxn DECIMAL(15,2),
    dias_reapertura_min        INT,
    dias_reapertura_max        INT,
    fuente_costos              VARCHAR(100),
    FOREIGN KEY (id_empresa)   REFERENCES Empresa  (id),
    FOREIGN KEY (id_ubicacion) REFERENCES Ubicacion(id),
    FOREIGN KEY (id_region)    REFERENCES Region   (id)
);

-- ------------------------------------------------------------
-- 9. ROL_REGION
-- ------------------------------------------------------------
CREATE TABLE Rol_Region (
    id_rol    INT NOT NULL,
    id_region INT NOT NULL,
    PRIMARY KEY (id_rol, id_region),
    FOREIGN KEY (id_rol)    REFERENCES Rol   (id),
    FOREIGN KEY (id_region) REFERENCES Region(id)
);

-- ------------------------------------------------------------
-- 10. ROL_PLANTA
-- ------------------------------------------------------------
CREATE TABLE Rol_Planta (
    id_rol    INT NOT NULL,
    id_planta INT NOT NULL,
    PRIMARY KEY (id_rol, id_planta),
    FOREIGN KEY (id_rol)    REFERENCES Rol   (id),
    FOREIGN KEY (id_planta) REFERENCES Planta(id)
);

-- ------------------------------------------------------------
-- 11. USUARIO
--     id  → PK interna para JOINs eficientes
--     uuid → UID de Firebase Auth (viene en el JWT)
-- ------------------------------------------------------------
CREATE TABLE Usuario (
    id         INT          PRIMARY KEY AUTO_INCREMENT,
    uuid       VARCHAR(36)  NOT NULL UNIQUE,
    nombre     VARCHAR(100) NOT NULL,
    apellido   VARCHAR(100) NOT NULL,
    correo     VARCHAR(100) NOT NULL UNIQUE,
    telefono   VARCHAR(20),
    id_empresa INT,
    id_rol     INT,
    activo     BOOL         DEFAULT TRUE,
    FOREIGN KEY (id_empresa) REFERENCES Empresa(id),
    FOREIGN KEY (id_rol)     REFERENCES Rol   (id)
);

-- ------------------------------------------------------------
-- 12. ESTADO_PLANTA
--     ✅ FIX: índice cambiado a (id_planta, tipo_dato, fecha_registro)
--     para que el query de tendencia 5 años sea eficiente:
--     WHERE id_planta=? AND tipo_dato='historico' AND fecha>=...
--
--     tipo_dato:
--       'historico'           → Open-Meteo Historical API (backfill)
--       'pronostico_openmeteo'→ Open-Meteo Forecast API (tiempo real)
--       'pronostico_smn'      → SMN API oficial México
--       'sensor'              → sensor físico en planta
--       'manual'              → captura manual por operador
-- ------------------------------------------------------------
CREATE TABLE Estado_Planta (
    id                  INT         PRIMARY KEY AUTO_INCREMENT,
    id_planta           INT         NOT NULL,
    fecha_registro      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    umbral              FLOAT,
    proyeccion_nivel_mm FLOAT,
    nivel_agua          FLOAT       NOT NULL,
    indice_hidrico      FLOAT       NOT NULL,
    evento_extremo      BOOL        DEFAULT FALSE,
    fuente              VARCHAR(50) DEFAULT 'OPEN_METEO',
    tipo_dato           VARCHAR(30) DEFAULT 'historico',
    unidad_nivel        VARCHAR(20) DEFAULT 'mm',
    FOREIGN KEY (id_planta) REFERENCES Planta(id),
    -- ✅ Índice compuesto que cubre el query de tendencia 5 años
    INDEX idx_planta_tipo_fecha (id_planta, tipo_dato, fecha_registro)
);

-- ------------------------------------------------------------
-- 13. SIMULACION
-- ------------------------------------------------------------
CREATE TABLE Simulacion (
    id             INT         PRIMARY KEY AUTO_INCREMENT,
    id_planta      INT         NOT NULL,
    fecha_calculo  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    horizonte_dias INT         NOT NULL DEFAULT 90,
    modelo         VARCHAR(50) NOT NULL,   -- 'SARIMA'|'MONTE_CARLO'|'COMBINADO'
    resultado      JSON        NOT NULL,
    FOREIGN KEY (id_planta) REFERENCES Planta(id),
    INDEX idx_planta_fecha (id_planta, fecha_calculo)
);

-- ------------------------------------------------------------
-- 14. ALERTA
-- ------------------------------------------------------------
CREATE TABLE Alerta (
    id           INT          PRIMARY KEY AUTO_INCREMENT,
    id_planta    INT          NOT NULL,
    tipo         VARCHAR(50)  NOT NULL,   -- 'CRÍTICO'|'ADVERTENCIA'|'INFORMATIVO'
    titulo       VARCHAR(200) NOT NULL,
    descripcion  TEXT,
    nivel_actual FLOAT,
    umbral       FLOAT,
    tendencia    VARCHAR(100),
    fecha        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_planta) REFERENCES Planta(id),
    INDEX idx_planta_fecha (id_planta, fecha)
);

-- ------------------------------------------------------------
-- 15. NOTIFICACION
--     Firebase FCM envía el correo/push.
--     Aquí se registra estado de lectura/archivo para
--     mostrar el contador de no leídas en el dashboard.
-- ------------------------------------------------------------
CREATE TABLE Notificacion (
    id         INT       PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT       NOT NULL,
    id_alerta  INT       NOT NULL,
    leida      BOOL      DEFAULT FALSE,
    archivada  BOOL      DEFAULT FALSE,
    fecha      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id),
    FOREIGN KEY (id_alerta)  REFERENCES Alerta (id),
    INDEX idx_usuario (id_usuario)
);

-- ------------------------------------------------------------
-- 16. MONITOR_API
-- ------------------------------------------------------------
CREATE TABLE Monitor_API (
    id           INT          PRIMARY KEY AUTO_INCREMENT,
    nombre_api   VARCHAR(100) NOT NULL,
    url          VARCHAR(300),
    metodo       VARCHAR(10),
    endpoint     VARCHAR(300),
    estado       VARCHAR(20)  NOT NULL,
    codigo_error INT,
    mensaje      VARCHAR(300),
    ocurrencias  INT          DEFAULT 1,
    resuelto     BOOL         DEFAULT FALSE,
    ultimo_ok    TIMESTAMP,
    fecha        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_api_fecha (nombre_api, fecha),
    INDEX idx_resuelto  (resuelto)
);

-- ------------------------------------------------------------
-- 17. API_KEY
-- ------------------------------------------------------------
CREATE TABLE API_Key (
    id             INT          PRIMARY KEY AUTO_INCREMENT,
    nombre         VARCHAR(100) NOT NULL,
    valor_cifrado  TEXT         NOT NULL,
    expiracion     TIMESTAMP,
    activa         BOOL         DEFAULT TRUE,
    fecha_rotacion TIMESTAMP,
    rotada_por     INT,
    FOREIGN KEY (rotada_por) REFERENCES Usuario(id)
);

-- ------------------------------------------------------------
-- 18. AUDITORIA
-- ------------------------------------------------------------
CREATE TABLE Auditoria (
    id             INT          PRIMARY KEY AUTO_INCREMENT,
    id_usuario     INT,
    accion         VARCHAR(50)  NOT NULL,
    modulo         VARCHAR(100) NOT NULL,
    entidad        VARCHAR(200),
    descripcion    TEXT,
    ip             VARCHAR(40)  NOT NULL,
    severidad      VARCHAR(20),
    valor_anterior JSON,
    valor_nuevo    JSON,
    fecha          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id),
    INDEX idx_fecha     (fecha),
    INDEX idx_usuario   (id_usuario),
    INDEX idx_modulo    (modulo),
    INDEX idx_severidad (severidad)
);

-- ------------------------------------------------------------
-- 19. SESION
-- ------------------------------------------------------------
CREATE TABLE Sesion (
    id           INT         PRIMARY KEY AUTO_INCREMENT,
    id_usuario   INT,
    fecha_inicio TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_fin    TIMESTAMP,
    ip           VARCHAR(40) NOT NULL,
    user_agent   VARCHAR(150),
    exitosa      BOOL,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id),
    INDEX idx_usuario (id_usuario)
);

-- ------------------------------------------------------------
-- 20. BITACORA
-- ------------------------------------------------------------
CREATE TABLE Bitacora (
    id            INT          PRIMARY KEY AUTO_INCREMENT,
    tabla         VARCHAR(100) NOT NULL,
    operacion     VARCHAR(10)  NOT NULL,
    id_registro   INT,
    valor_antes   JSON,
    valor_despues JSON,
    fecha         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tabla_fecha (tabla, fecha),
    INDEX idx_operacion   (operacion)
);

-- ------------------------------------------------------------
-- 21. SCORE_CONFIG
--     ✅ FIX: columna fuente_campo agregada para que Quarkus
--     sepa de qué tabla/campo jalar el valor de cada factor
--     sin tenerlo hardcodeado en el código.
-- ------------------------------------------------------------
CREATE TABLE Score_Config (
    id           INT          PRIMARY KEY AUTO_INCREMENT,
    factor       VARCHAR(100) NOT NULL UNIQUE,
    peso_pct     INT          NOT NULL,
    descripcion  VARCHAR(300),
    fuente_campo VARCHAR(200),   -- ej. 'Fuente_Agua.disponibilidad'
    activo       BOOL         DEFAULT TRUE
);


-- ============================================================
-- DATOS SEMILLA
-- ============================================================

INSERT INTO Rol (nombre, descripcion) VALUES
    ('Administrador',    'Acceso total a todos los módulos y datos'),
    ('Director',         'Acceso a reportes, alertas y simulaciones'),
    ('Gerente de Planta','Gestión de información de su planta asignada'),
    ('Analista',         'Acceso a reportes y análisis de su región'),
    ('Operador',         'Acceso limitado a operación de planta');

INSERT INTO Permiso (clave, modulo, descripcion) VALUES
    ('resumen.ver',         'Resumen',         'Visualizar dashboard ejecutivo'),
    ('plantas.ver',         'Plantas',         'Ver información de plantas'),
    ('fuentes_agua.ver',    'Fuentes de agua', 'Ver fuentes de agua'),
    ('riesgos.ver',         'Riesgos',         'Ver índice de riesgo hídrico'),
    ('simulaciones.ver',    'Simulaciones',    'Ver simulaciones de sequía'),
    ('reportes.ver',        'Reportes',        'Ver y exportar reportes'),
    ('configuracion.ver',   'Configuración',   'Acceder a configuración del sistema'),
    ('usuarios.gestionar',  'Configuración',   'Gestionar usuarios y roles'),
    ('api_keys.gestionar',  'Configuración',   'Gestionar llaves de API');

-- Administrador → todos los permisos
INSERT INTO Rol_Permiso (id_rol, id_permiso) VALUES
    (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9);
-- Director → resumen, plantas, fuentes, riesgos, simulaciones, reportes
INSERT INTO Rol_Permiso (id_rol, id_permiso) VALUES
    (2,1),(2,2),(2,3),(2,4),(2,5),(2,6);
-- Gerente de Planta → resumen, plantas, fuentes, riesgos, simulaciones, reportes
INSERT INTO Rol_Permiso (id_rol, id_permiso) VALUES
    (3,1),(3,2),(3,3),(3,4),(3,5),(3,6);
-- Analista → resumen, plantas, fuentes, riesgos, reportes
INSERT INTO Rol_Permiso (id_rol, id_permiso) VALUES
    (4,1),(4,2),(4,3),(4,4),(4,6);
-- Operador → solo resumen y plantas
INSERT INTO Rol_Permiso (id_rol, id_permiso) VALUES
    (5,1),(5,2);

INSERT INTO Region (nombre) VALUES
    ('Norte'),
    ('Noroeste'),
    ('Centro'),
    ('Bajío'),
    ('Sur');

-- Administrador y Director → todas las regiones
INSERT INTO Rol_Region (id_rol, id_region) VALUES
    (1,1),(1,2),(1,3),(1,4),(1,5),
    (2,1),(2,2),(2,3),(2,4),(2,5);
-- Analista → solo Norte
INSERT INTO Rol_Region (id_rol, id_region) VALUES
    (4,1);
-- Gerente de Planta y Operador → Rol_Planta define su planta específica

-- ✅ FIX: seed de Score_Config con fuente_campo incluida
INSERT INTO Score_Config (factor, peso_pct, descripcion, fuente_campo) VALUES
    ('disponibilidad_agua',   30,
     'Disponibilidad hídrica según CONAGUA',
     'Fuente_Agua.disponibilidad'),
    ('tendencia_sequia',      25,
     'Tendencia histórica 5 años',
     'Estado_Planta.indice_hidrico WHERE tipo_dato=historico'),
    ('costo_total',           20,
     'Costo único cierre + apertura',
     'Planta.costo_cierre_mxn + Planta.costo_apertura_mxn'),
    ('tiempo_reapertura',     15,
     'Días estimados de reapertura',
     'Planta.dias_reapertura_min / Planta.dias_reapertura_max'),
    ('riesgo_hidrico_actual', 10,
     'Índice hídrico más reciente',
     'Estado_Planta.indice_hidrico ORDER BY fecha_registro DESC LIMIT 1');


-- ============================================================
-- TRIGGERS
-- ============================================================

DELIMITER $$

CREATE TRIGGER trg_usuario_insert AFTER INSERT ON Usuario
FOR EACH ROW
BEGIN
    INSERT INTO Bitacora (tabla, operacion, id_registro, valor_despues)
    VALUES ('Usuario', 'INSERT', NEW.id, JSON_OBJECT(
        'uuid', NEW.uuid, 'nombre', NEW.nombre,
        'correo', NEW.correo, 'id_rol', NEW.id_rol, 'activo', NEW.activo
    ));
END$$

CREATE TRIGGER trg_usuario_update AFTER UPDATE ON Usuario
FOR EACH ROW
BEGIN
    INSERT INTO Bitacora (tabla, operacion, id_registro, valor_antes, valor_despues)
    VALUES ('Usuario', 'UPDATE', NEW.id,
        JSON_OBJECT('uuid', OLD.uuid, 'nombre', OLD.nombre,
                    'correo', OLD.correo, 'id_rol', OLD.id_rol, 'activo', OLD.activo),
        JSON_OBJECT('uuid', NEW.uuid, 'nombre', NEW.nombre,
                    'correo', NEW.correo, 'id_rol', NEW.id_rol, 'activo', NEW.activo)
    );
END$$

CREATE TRIGGER trg_usuario_delete AFTER DELETE ON Usuario
FOR EACH ROW
BEGIN
    INSERT INTO Bitacora (tabla, operacion, id_registro, valor_antes)
    VALUES ('Usuario', 'DELETE', OLD.id, JSON_OBJECT(
        'uuid', OLD.uuid, 'nombre', OLD.nombre,
        'correo', OLD.correo, 'id_rol', OLD.id_rol
    ));
END$$

CREATE TRIGGER trg_planta_insert AFTER INSERT ON Planta
FOR EACH ROW
BEGIN
    INSERT INTO Bitacora (tabla, operacion, id_registro, valor_despues)
    VALUES ('Planta', 'INSERT', NEW.id, JSON_OBJECT(
        'nombre', NEW.nombre, 'id_empresa', NEW.id_empresa,
        'id_region', NEW.id_region, 'activa', NEW.activa
    ));
END$$

CREATE TRIGGER trg_planta_update AFTER UPDATE ON Planta
FOR EACH ROW
BEGIN
    INSERT INTO Bitacora (tabla, operacion, id_registro, valor_antes, valor_despues)
    VALUES ('Planta', 'UPDATE', NEW.id,
        JSON_OBJECT('nombre', OLD.nombre, 'id_empresa', OLD.id_empresa,
                    'id_region', OLD.id_region, 'activa', OLD.activa),
        JSON_OBJECT('nombre', NEW.nombre, 'id_empresa', NEW.id_empresa,
                    'id_region', NEW.id_region, 'activa', NEW.activa)
    );
END$$

-- ✅ FIX: activa incluido en el trigger de delete
CREATE TRIGGER trg_planta_delete AFTER DELETE ON Planta
FOR EACH ROW
BEGIN
    INSERT INTO Bitacora (tabla, operacion, id_registro, valor_antes)
    VALUES ('Planta', 'DELETE', OLD.id, JSON_OBJECT(
        'nombre', OLD.nombre, 'id_empresa', OLD.id_empresa,
        'id_region', OLD.id_region, 'activa', OLD.activa
    ));
END$$

CREATE TRIGGER trg_estado_planta_insert AFTER INSERT ON Estado_Planta
FOR EACH ROW
BEGIN
    INSERT INTO Bitacora (tabla, operacion, id_registro, valor_despues)
    VALUES ('Estado_Planta', 'INSERT', NEW.id, JSON_OBJECT(
        'id_planta', NEW.id_planta, 'nivel_agua', NEW.nivel_agua,
        'indice_hidrico', NEW.indice_hidrico,
        'evento_extremo', NEW.evento_extremo,
        'tipo_dato', NEW.tipo_dato
    ));
END$$

CREATE TRIGGER trg_alerta_insert AFTER INSERT ON Alerta
FOR EACH ROW
BEGIN
    INSERT INTO Bitacora (tabla, operacion, id_registro, valor_despues)
    VALUES ('Alerta', 'INSERT', NEW.id, JSON_OBJECT(
        'id_planta', NEW.id_planta, 'tipo', NEW.tipo,
        'titulo', NEW.titulo, 'nivel_actual', NEW.nivel_actual
    ));
END$$

CREATE TRIGGER trg_rol_insert AFTER INSERT ON Rol
FOR EACH ROW
BEGIN
    INSERT INTO Bitacora (tabla, operacion, id_registro, valor_despues)
    VALUES ('Rol', 'INSERT', NEW.id,
        JSON_OBJECT('nombre', NEW.nombre));
END$$

CREATE TRIGGER trg_rol_update AFTER UPDATE ON Rol
FOR EACH ROW
BEGIN
    INSERT INTO Bitacora (tabla, operacion, id_registro, valor_antes, valor_despues)
    VALUES ('Rol', 'UPDATE', NEW.id,
        JSON_OBJECT('nombre', OLD.nombre),
        JSON_OBJECT('nombre', NEW.nombre)
    );
END$$

CREATE TRIGGER trg_rol_delete AFTER DELETE ON Rol
FOR EACH ROW
BEGIN
    INSERT INTO Bitacora (tabla, operacion, id_registro, valor_antes)
    VALUES ('Rol', 'DELETE', OLD.id,
        JSON_OBJECT('nombre', OLD.nombre));
END$$

CREATE TRIGGER trg_apikey_update AFTER UPDATE ON API_Key
FOR EACH ROW
BEGIN
    INSERT INTO Bitacora (tabla, operacion, id_registro, valor_antes, valor_despues)
    VALUES ('API_Key', 'UPDATE', NEW.id,
        JSON_OBJECT('nombre', OLD.nombre, 'activa', OLD.activa,
                    'expiracion', OLD.expiracion),
        JSON_OBJECT('nombre', NEW.nombre, 'activa', NEW.activa,
                    'expiracion', NEW.expiracion,
                    'rotada_por', NEW.rotada_por)
    );
END$$

DELIMITER ;

-- ============================================================
-- FIN DEL SCRIPT
-- ============================================================