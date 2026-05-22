# AquaVitaeBack

Backend del sistema de gestión hídrica AquaVitae, desarrollado con Quarkus 3 y arquitectura hexagonal. Expone una API REST para el monitoreo de plantas industriales, simulación hídrica y análisis de alternativas de ubicación.

---

## Tecnologías

- Java 17
- Quarkus 3.33
- MySQL 8.0
- Maven 3.9
- Firebase Admin SDK (notificaciones)
- Docker (para despliegue en Cloud Run)

---

## Requisitos previos

- [Java 17](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/)
- [MySQL 8.0](https://dev.mysql.com/downloads/)
- [gcloud CLI](https://cloud.google.com/sdk/docs/install) (solo para despliegue)

---

## Configuración local

### 1. Clonar el repositorio

```bash
git clone https://github.com/FeryRi/AquaVitaeBack.git
cd AquaVitaeBack
git checkout develop
git pull origin develop
```

### 2. Crear la base de datos

Inicia MySQL y ejecuta el script oficial:

```bash
mysql -u root -p < scriptSQL.sql
mysql -u root -p < seed_bachoco.sql
```

Esto crea la base de datos `AquaVitaeDB` con las 21 tablas y los datos iniciales de las 9 plantas Bachoco.

### 3. Variables de entorno (opcional para local)

El proyecto usa valores por defecto para desarrollo local. Si quieres sobreescribirlos, crea un archivo `.env` o exporta las variables:

| Variable | Valor local por defecto | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/AquaVitaeDB` | URL de la base de datos |
| `DB_USER` | `root` | Usuario de MySQL |
| `DB_PASSWORD` | `Karen12345!` | Contraseña de MySQL |
| `FIREBASE_CREDENTIALS` | `src/main/resources/aquavitae-firebase-adminsdk.json` | Ruta al JSON de Firebase |
| `CORS_ORIGINS` | `http://localhost:3000,http://localhost:5173` | Orígenes permitidos |

### 4. Correr en modo desarrollo

```bash
./mvnw quarkus:dev
```

El backend queda disponible en `http://localhost:8080` con hot reload activado.

---

## Endpoints disponibles

### Dashboard
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/dashboard` | Plantas con índice hídrico y resumen por nivel de riesgo |
| GET | `/api/alertas?limit=10` | Lista de alertas activas |
| GET | `/api/evolucion?dias=30` | Evolución hídrica en N días |

### Simulación Hídrica
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/simulacion/kpis?plantaId={id}` | 4 KPIs: índice actual, días al umbral, probabilidad, pérdida económica |
| GET | `/api/simulacion/proyeccion?plantaId={id}&dias=90` | Curva de proyección con banda de confianza |
| GET | `/api/simulacion/recuperacion?plantaId={id}&dias=90` | Escenario con y sin intervención |

### Alternativas de Ubicación
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/alternativas/alerta?plantaId={id}` | Banner de alerta con días de cierre y costos estimados |
| GET | `/api/alternativas/ubicaciones?plantaId={id}` | Alternativas ordenadas por riesgo |
| GET | `/api/alternativas/factores?plantaId={id}` | 5 factores evaluados para la planta |

---

## Arquitectura

El proyecto sigue **arquitectura hexagonal**:

```
src/main/java/com/aquavitae/
├── domain/
│   ├── models/          # Entidades de dominio
│   ├── ports/           # Interfaces (FuenteClimaPort, etc.)
│   ├── repository/      # Contratos de repositorios
│   └── service/         # Lógica de negocio pura
├── application/
│   ├── dto/             # Data Transfer Objects
│   └── usecase/         # Casos de uso
├── infrastructure/
│   ├── api/             # Adaptadores de APIs externas (OpenMeteo, NASA, SMN)
│   ├── entities/        # Entidades JPA
│   ├── mapper/          # Conversores dominio ↔ DTO
│   └── repository/      # Implementaciones de repositorios
└── interfaces/
    └── rest/            # Resources REST (controllers)
```

---

## Despliegue en GCP (Cloud Run)

### Requisitos
- Tener `gcloud` instalado y autenticado: `gcloud auth login`
- Tener acceso al proyecto `aquavitaeback`

### Compilar y desplegar

```bash
gcloud builds submit . --config=cloudbuild.yaml --project=aquavitaeback
```

Esto ejecuta automáticamente:
1. Compilación con Maven
2. Construcción de imagen Docker
3. Push a Artifact Registry
4. Deploy a Cloud Run

El backend queda disponible en:
`https://aquavitae-backend-1005047638592.us-central1.run.app`

### Variables de entorno en producción

Las variables están configuradas directamente en Cloud Run (no en el código):
- `DB_URL` → IP pública de Cloud SQL
- `DB_USER` / `DB_PASSWORD` → credenciales de producción
- `CORS_ORIGINS` → dominios del frontend en producción
- `FIREBASE_CREDENTIALS` → ruta al JSON de Firebase Admin

---

## APIs externas integradas

- **Open-Meteo** — datos climáticos en tiempo real (32 estados de México)
- **NASA POWER** — temperatura, precipitación, humedad
- **SMN/CONAGUA** — servicio meteorológico nacional
