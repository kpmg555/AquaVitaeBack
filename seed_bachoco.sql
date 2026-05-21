-- ============================================================
-- AquaVitaeDB - Datos iniciales
-- Fuente: Reporte Anual Bachoco 2024 (BMV, Clave: BACHOCO)
-- Ciudades operativas México: pág. 36
-- Datos financieros: pág. 39-43
-- ============================================================

-- Empresa y Region ya existen del scriptSQL.sql:
--   Empresa(1) = Industrias Bachoco S.A. de C.V.
--   Region(1)=Norte, (2)=Noroeste, (3)=Centro, (4)=Bajío, (5)=Sur

-- 3. UBICACIONES
-- location_id = índice (1-based) en el array de coordenadas del API Open-Meteo
-- Fuente coordenadas: application.properties
INSERT INTO Ubicacion (id, location_id, nombre, latitud, longitud, elevation) VALUES
(1,  11, 'Celaya, Guanajuato',          21.00, -101.20,  1750),
(2,  19, 'Monterrey, Nuevo León',       25.50,  -99.80,   538),
(3,  26, 'Hermosillo, Sonora',          29.70, -110.80,   215),
(4,  31, 'Mérida, Yucatán',            20.70,  -88.90,     9),
(5,  25, 'Culiacán, Sinaloa',           25.00, -107.50,    60),
(6,   1, 'Lagos de Moreno, Jalisco',    21.88, -102.29,  1930),
(7,  10, 'Gómez Palacio, Durango',      24.90, -104.90,  1140),
(8,  30, 'Coatzacoalcos, Veracruz',     19.30,  -96.50,    10),
(9,  21, 'Tecamachalco, Puebla',        18.80,  -97.90,  2040);

-- 4. PLANTAS PROCESADORAS
-- Costos estimados basados en:
--   - Ingresos netos México 2024: $82,007.9 M MXN
--   - Margen bruto 2024: 20.6%  →  costo_ventas ≈ 79.4% = ~$65,134 M MXN
--   - 7 plantas procesadoras (tabla de instalaciones, pág. 36)
--   - Costo operación diaria estimado por planta: total / 365 / 7 ≈ $25.5 M MXN
--   - Plantas grandes (Monterrey, Hermosillo, Celaya): ~$25-28 M/día
--   - Plantas medianas (Culiacán, Gómez Palacio, Lagos): ~$18-20 M/día
--   - Instalaciones menores (Mérida, Tecamachalco, Coatzacoalcos): ~$10-13 M/día
--   - Costo apertura/cierre: estimado proporcional al tamaño
INSERT INTO Planta (id, nombre, id_empresa, id_ubicacion, id_region,
                    activa, umbral_alerta,
                    costo_cierre_mxn, costo_apertura_mxn, costo_operacion_diaria_mxn,
                    dias_reapertura_min, dias_reapertura_max,
                    fuente_costos) VALUES
-- Regiones del scriptSQL: 1=Norte, 2=Noroeste, 3=Centro, 4=Bajío, 5=Sur
-- NORTE (id_region=1)
(1, 'Planta Monterrey',        1, 2, 1, 1, 0.75, 195000000.00, 480000000.00, 27500000.00, 45, 75, 'Reporte Anual Bachoco 2024 BMV'),
(4, 'Planta Gómez Palacio',    1, 7, 1, 1, 0.75, 110000000.00, 290000000.00, 16500000.00, 35, 60, 'Reporte Anual Bachoco 2024 BMV'),
-- NOROESTE (id_region=2)
(2, 'Planta Hermosillo',       1, 3, 2, 1, 0.75, 175000000.00, 430000000.00, 25000000.00, 45, 75, 'Reporte Anual Bachoco 2024 BMV'),
(3, 'Planta Culiacán',         1, 5, 2, 1, 0.75, 120000000.00, 310000000.00, 18000000.00, 35, 60, 'Reporte Anual Bachoco 2024 BMV'),
-- BAJÍO (id_region=4)
(5, 'Planta Celaya',           1, 1, 4, 1, 0.75, 185000000.00, 460000000.00, 26000000.00, 45, 75, 'Reporte Anual Bachoco 2024 BMV'),
(6, 'Planta Lagos de Moreno',  1, 6, 4, 1, 0.75, 105000000.00, 270000000.00, 15500000.00, 30, 55, 'Reporte Anual Bachoco 2024 BMV'),
-- SUR (id_region=5)
(7, 'Planta Mérida',           1, 4, 5, 1, 0.75,  80000000.00, 215000000.00, 11500000.00, 30, 50, 'Reporte Anual Bachoco 2024 BMV'),
(8, 'Planta Coatzacoalcos',    1, 8, 5, 1, 0.75,  70000000.00, 195000000.00, 10000000.00, 30, 50, 'Reporte Anual Bachoco 2024 BMV'),
(9, 'Planta Tecamachalco',     1, 9, 5, 1, 0.75,  85000000.00, 230000000.00, 12500000.00, 30, 50, 'Reporte Anual Bachoco 2024 BMV');

-- 5. ESTADO INICIAL DE CADA PLANTA (índice hídrico actual)
-- indice_hidrico: 0.0 = sin estrés hídrico, 1.0 = estrés crítico
-- Clasificación: <0.45 bajo, <0.70 medio, >=0.70 alto
-- Valores acordes a condiciones reales de aridez por región (mayo 2026)
INSERT INTO Estado_Planta (id_planta, nivel_agua, indice_hidrico, umbral, fuente, tipo_dato) VALUES
-- NORTE (zona árida/semiárida - estrés alto)
(1, 14.5,  0.82, 0.75, 'OPEN_METEO', 'historico'),   -- Monterrey: alto
(2, 21.0,  0.76, 0.75, 'OPEN_METEO', 'historico'),   -- Hermosillo: alto (desierto Sonora)
(3, 45.2,  0.62, 0.75, 'OPEN_METEO', 'historico'),   -- Culiacán: medio (estacional)
(4, 28.8,  0.71, 0.75, 'OPEN_METEO', 'historico'),   -- Gómez Palacio: alto (semiárido)
-- CENTRO
(5, 54.0,  0.55, 0.75, 'OPEN_METEO', 'historico'),   -- Celaya: medio (sequía Guanajuato)
(6, 86.5,  0.37, 0.75, 'OPEN_METEO', 'historico'),   -- Lagos de Moreno: bajo
-- SUR (zonas más húmedas)
(7, 79.0,  0.42, 0.75, 'OPEN_METEO', 'historico'),   -- Mérida: bajo (acuífero Yucatán)
(8, 125.0, 0.24, 0.75, 'OPEN_METEO', 'historico'),   -- Coatzacoalcos: bajo (tropical)
(9, 57.5,  0.51, 0.75, 'OPEN_METEO', 'historico');   -- Tecamachalco: medio
