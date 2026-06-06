# ARIMA batch — AquaVitae

Batch en Python que genera las predicciones hídricas y los KPIs que consume el
backend (endpoints `/api/predicciones/*`). Por cada planta descarga el histórico
climático de Open-Meteo, ajusta un modelo ARIMA y escribe en:

- `Prediccion_Hidrica` — pronóstico a 90 días + bandas de confianza
- `Prediccion_Kpi` — días hasta umbral, probabilidad de evento crítico y pérdida estimada

## Uso

```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt

# Conexión a la BD por variables de entorno (default = local)
DB_HOST=<host> DB_PORT=3306 DB_USER=<user> DB_PASSWORD=<pass> DB_NAME=AquaVitaeDB \
  python generar_arima.py
```

> Las filas se insertan con `fecha_calculo` (un único timestamp por corrida), que
> es la columna por la que el backend selecciona el último cálculo
> (`MAX(fecha_calculo)`).
