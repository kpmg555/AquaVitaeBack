"""
Batch ARIMA AquaVitae — genera predicciones + KPIs reales.
Por planta: histórico real -> índice -> ARIMA ->
  * Prediccion_Hidrica : pronóstico 90 días + bandas
  * Prediccion_Kpi     : días hasta umbral, prob. evento crítico (Monte Carlo), pérdida
"""
import os, warnings, itertools, datetime as dt
import requests, numpy as np, pandas as pd, pymysql
from statsmodels.tsa.arima.model import ARIMA
from scipy.stats import norm

warnings.filterwarnings("ignore")

# DB configurable por env vars (default = local). Para Cloud SQL: DB_HOST/DB_PORT/DB_USER/DB_PASSWORD.
DB = dict(
    host=os.getenv("DB_HOST", "127.0.0.1"),
    port=int(os.getenv("DB_PORT", "3306")),
    user=os.getenv("DB_USER", "root"),
    password=os.getenv("DB_PASSWORD", "Karen12345!"),
    database=os.getenv("DB_NAME", "AquaVitaeDB"),
    autocommit=True,
)
print(f"Conectando a {DB['host']}:{DB['port']} / {DB['database']} (user {DB['user']})")
START, END = "2025-11-25", "2026-05-25"
HORIZONTE = 90
UMBRAL = 0.15        # evento crítico = ESCASEZ: el índice cae a <= 0.15
DIA_PROB = 30        # probabilidad puntual de escasez a este horizonte (días)
COSTO_BASE = 8_000_000.0   # MXN: pérdida esperada = prob * COSTO_BASE (placeholder)

DDL_PRED = """
CREATE TABLE IF NOT EXISTS Prediccion_Hidrica (
  id INT AUTO_INCREMENT PRIMARY KEY, id_planta INT NOT NULL, fecha DATE NOT NULL,
  horizonte_dia INT NOT NULL, valor_predicho FLOAT NOT NULL,
  banda_inferior FLOAT, banda_superior FLOAT, modelo VARCHAR(40),
  fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP, INDEX idx_pf (id_planta, fecha))"""
DDL_KPI = """
CREATE TABLE IF NOT EXISTS Prediccion_Kpi (
  id INT AUTO_INCREMENT PRIMARY KEY, id_planta INT NOT NULL,
  indice_actual FLOAT, dias_hasta_umbral INT, probabilidad_evento FLOAT,
  perdida_estimada DOUBLE, umbral FLOAT, modelo VARCHAR(40),
  fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP, INDEX idx_k (id_planta))"""


def indice_diario(lat, lon):
    h = requests.get("https://archive-api.open-meteo.com/v1/archive", params={
        "latitude": lat, "longitude": lon, "start_date": START, "end_date": END,
        "hourly": "precipitation,soil_moisture_0_to_7cm,et0_fao_evapotranspiration,temperature_2m",
        "timezone": "auto"}, timeout=60).json()["hourly"]
    df = pd.DataFrame(h, index=pd.to_datetime(h["time"])).drop(columns="time").astype(float)
    d = pd.DataFrame({"precip": df["precipitation"].resample("D").sum(),
                      "hum": df["soil_moisture_0_to_7cm"].resample("D").mean(),
                      "evap": df["et0_fao_evapotranspiration"].resample("D").sum(),
                      "temp": df["temperature_2m"].resample("D").mean()}).interpolate()
    return (np.minimum(1, d.precip / 50) * 0.3 + d.hum * 0.4 +
            (1 - np.minimum(1, d.evap / 5)) * 0.2 +
            (1 - np.minimum(1, d.temp / 40)) * 0.1).asfreq("D")


def mejor_orden(s):
    best = (np.inf, (1, 1, 1))
    for o in itertools.product(range(4), range(2), range(4)):
        try: best = min(best, (ARIMA(s, order=o).fit().aic, o))
        except Exception: pass
    return best[1]


def kpis_desde_forecast(serie, media, se):
    indice_actual = float(serie.iloc[-1])
    # días hasta umbral: 0 si ya está en escasez; si no, primer día que CAE a <= UMBRAL; -1 = nunca
    if indice_actual <= UMBRAL:
        dias = 0
    else:
        cruces = np.where(media <= UMBRAL)[0]
        dias = int(cruces[0] + 1) if len(cruces) else -1
    # probabilidad puntual de escasez al día DIA_PROB: P(valor_t <= UMBRAL), normal(media, se)
    i = min(DIA_PROB, len(media)) - 1
    prob = float(norm.cdf((UMBRAL - media[i]) / max(se[i], 1e-6)))
    # pérdida proyectada = valor esperado: probabilidad * costo
    perdida = float(prob * COSTO_BASE)
    return indice_actual, dias, prob, perdida


conn = pymysql.connect(**DB); cur = conn.cursor()
cur.execute(DDL_PRED); cur.execute(DDL_KPI)
cur.execute("SELECT p.id, p.nombre, u.latitud, u.longitud "
            "FROM Planta p JOIN Ubicacion u ON p.id_ubicacion = u.id ORDER BY p.id")
plantas = cur.fetchall()

# timestamp único para toda la corrida: las gráficas filtran por MAX(fecha_calculo)
RUN_TS = dt.datetime.now().replace(microsecond=0)

print(f"{'Planta':<22}{'modelo':>11}{'díasUmbral':>11}{'probEvento':>11}{'pérdida':>12}")
print("-" * 67)
for pid, nombre, lat, lon in plantas:
    serie = indice_diario(float(lat), float(lon))
    orden = mejor_orden(serie)
    fit = ARIMA(serie, order=orden).fit()
    fc = fit.get_forecast(HORIZONTE)
    media = np.clip(fc.predicted_mean.values, 0, 1)
    ci = fc.conf_int().values
    inf, sup = np.clip(ci[:, 0], 0, 1), np.clip(ci[:, 1], 0, 1)
    base = serie.index[-1].date(); modelo = f"ARIMA{orden}"

    # --- predicciones ---
    cur.execute("DELETE FROM Prediccion_Hidrica WHERE id_planta=%s", (pid,))
    cur.executemany(
        "INSERT INTO Prediccion_Hidrica (id_planta,fecha,horizonte_dia,valor_predicho,banda_inferior,banda_superior,modelo,fecha_calculo) "
        "VALUES (%s,%s,%s,%s,%s,%s,%s,%s)",
        [(pid, base + dt.timedelta(days=i+1), i+1, float(media[i]), float(inf[i]), float(sup[i]), modelo, RUN_TS)
         for i in range(HORIZONTE)])

    # --- KPIs ---
    se = fc.se_mean.values
    ind, dias, prob, perdida = kpis_desde_forecast(serie, media, se)
    cur.execute("DELETE FROM Prediccion_Kpi WHERE id_planta=%s", (pid,))
    cur.execute("INSERT INTO Prediccion_Kpi (id_planta,indice_actual,dias_hasta_umbral,probabilidad_evento,perdida_estimada,umbral,modelo,fecha_calculo) "
                "VALUES (%s,%s,%s,%s,%s,%s,%s,%s)", (pid, ind, dias, prob, perdida, UMBRAL, modelo, RUN_TS))

    print(f"{nombre:<22}{str(orden):>11}{dias:>11}{prob*100:>10.1f}%{perdida/1e6:>10.1f}M")

print("-" * 67)
print("Listo: Prediccion_Hidrica + Prediccion_Kpi actualizadas.")
cur.close(); conn.close()
