<div align="center">

# ☕ EnergIAi API — Backend Service

*API REST desacoplada y de alta performance para el procesamiento, análisis e integración del consumo energético en tiempo real.*

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange?logo=openjdk)](#️-tecnologías-utilizadas)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.x-brightgreen?logo=springboot)](#️-tecnologías-utilizadas)
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven)](#️-tecnologías-utilizadas)
[![OCI](https://img.shields.io/badge/Oracle%20Cloud-Infrastructure-F80000?logo=oracle)](#️-arquitectura-de-infraestructura-oci)
[![Status](https://img.shields.io/badge/status-Production%20Ready-brightgreen)](#-estado)

</div>

⬅️ Volver al [README principal del proyecto](../README.md)

---

## 📖 Índice

- [Estado](#-estado)
- [Descripción del Problema](#-descripción-del-problema)
- [Arquitectura de la Aplicación](#️-arquitectura-de-la-aplicación)
- [Arquitectura de Infraestructura (OCI)](#️-arquitectura-de-infraestructura-oci)
- [Benchmarking & Performance Testing (k6)](#-benchmarking--performance-testing-k6)
- [Tecnologías Utilizadas](#️-tecnologías-utilizadas)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Instalación y Ejecución Local](#️-instalación-y-ejecución-local)
- [Contrato de Datos con el Modelo](#-contrato-de-datos-con-el-modelo-data-science)
- [Cálculo del Consumo Promedio Diario](#-cálculo-del-consumo-energético-promedio-diario)
- [Mock-Fallback (Client Layer & Resiliencia)](#-mock-fallback-client-layer--resiliencia)
- [Endpoints del MVP](#-endpoints-del-mvp)
- [Postman](#-postman)
- [Despliegue en OCI](#-despliegue-en-oci)
- [Atribución de Roles y Equipo](#-atribución-de-roles-y-equipo)

---

## 🚦 Estado

✅ **Production Ready / MVP Completado.** La API REST está 100% desarrollada en Java 21 LTS con Spring Boot 3.3.x, conectada con el microservicio de inferencia en Python (FastAPI), equipada con estrategia de resiliencia Fallback y lista para desplegarse como servicio persistente en OCI.

---

## 📋 Descripción del problema

El monitoreo de consumo energético requiere un backend robusto capaz de gestionar peticiones, validar entradas y comunicarse de forma segura con modelos predictivos externos.

Este servicio actúa como el **orquestador central**: recibe las solicitudes de consumo, procesa la lógica de negocio en Java y se conecta de forma agnóstica con servicios externos (como el modelo de Machine Learning desarrollado en Python).

---

## 🏗️ Arquitectura de la aplicación

```
┌──────────────┐     HTTP REST     ┌────────────────────────┐
│   Postman /  │ ────────────────▶ │  EnergIAi API (Java)   │
│   Frontend   │ ◀──────────────── │  Spring Boot 3.3 +     │
└──────────────┘                    │  Virtual Threads       │
                                    └───────────┬────────────┘
                                                │
                                 ┌──────────────┴──────────────┐
                                 ▼                             ▼
                       ┌──────────────────┐          ┌──────────────────┐
                       │ Client Layer     │          │ Persistencia BDD │
                       │ (Python ML Model │          │ (PostgreSQL /    │
                       │ / Fallback Mock) │          │ TimescaleDB)*    │
                       └──────────────────┘          └──────────────────┘
```

\* Nota: La capa de persistencia en base de datos PostgreSQL + TimescaleDB con migraciones Flyway está proyectada como un hito incremental futuro.

📌 El recorrido completo de una petición, capa por capa, con datos reales de ejemplo:

![Flujo real de una petición en EnergIAi](docs/images/flujo_real_energiai_con_datos.png)

---

## ☁️ Arquitectura de Infraestructura (OCI)

El proyecto se encuentra desplegado sobre **dos máquinas virtuales de Oracle Cloud Infrastructure (Free Tier)**, dentro de la VCN `vcn-energiai` (`10.0.0.0/16`), región Brazil East (São Paulo):

| Máquina | Rol | IP pública | IP privada | Puerto | Estado |
|---|---|---|---|---|---|
| **VM Java** | Backend Principal (Spring Boot) | `163.176.43.143` | `10.0.0.213` | 8080 | ✅ Operativa |
| **VM Python** | Servicio de Machine Learning (FastAPI + `.pkl`) | `147.15.16.156` | `10.0.0.164` | 8000 | ✅ Operativa (`systemd`) |

**Sobre el acceso a la VM Python:** La restricción de acceso se logra por **firewall**: la Security List de OCI y el `iptables` interno solo aceptan tráfico al puerto 8000 desde la subred interna (`10.0.0.0/24`) — nunca desde internet. El resultado de seguridad es un aislamiento de red total hacia el microservicio de IA.

![Arquitectura de red con las dos máquinas en OCI](docs/images/arquitectura_oci_dos_maquinas.png)

> 🔧 Proceso completo de configuración de infraestructura (paso a paso, decisiones y problemas resueltos) en [`oci/README.md`](../oci/README.md).

---

---

## ⚡ Benchmarking & Performance Testing (k6)

Para validar la resiliencia y capacidad de respuesta en entornos de alta concurrencia, el backend Java 21 / Spring Boot 3.3.x desplegado en **Oracle Cloud Infrastructure (OCI)** fue auditado rigurosamente utilizando la herramienta de Pruebas de Carga **Grafana k6**, monitoreando el consumo de recursos de hardware en vivo vía `htop`.

Los scripts de prueba y sus configuraciones se encuentran versionados dentro de la carpeta local `./k6/script/`.

---

### 🏥 1. Diagnóstico de Salud de Infraestructura (`GET /api/v1/health`)

Prueba de carga destructiva sobre el servlet container (Tomcat) para evaluar la gestión de sockets e hilos bajo una demanda máxima de **30 Usuarios Virtuales (VUs)** durante un periodo de 2 minutos.

#### ⚙️ Comando de Ejecución Local:
```bash
k6 run k6/script/test-health.js
```

---

#### 📊 Evidencias de Ejecución (Ciclo de Vida de Carga)

| 🟢 1. Estado Inicial (Basal) | 🟡 2. Pico de Uso CPU (`htop`) | 🟢 3. Reporte Final (`k6`) |
|:---:|:---:|:---:|
| ![Health Baseline](./k6/images/health/health-htop-baseline.png) | ![Health CPU Peak](./k6/images/health/health-htop-cpu-peak.png) | ![Health Metrics](./k6/images/health/health-k6-metrics-verde.png) |
| *Estado inactivo (0.7% CPU, 420 MB RAM).* | *Escalado elástico de CPU (27.2%) bajo demanda.* | *SLA cumplido: p(95) = 57.37 ms y 0.00% errores.* |

* **Resultados Métrica SLA:** 1,687 peticiones totales | **p(95) = 57.37 ms** | **0.00%** tasa de fallos.

---

### ⚡ 2. Ingesta y Cálculo de Dominio Real (`POST /analisis-energetico`)

Prueba de carga funcional sobre el servicio de negocio principal. Evalúa el parseo y deserialización de payloads JSON, cálculo nativo de consumo mensual/diario con `java.time.YearMonth`, invocación al cliente de inferencia de ML y formateo de respuestas de observabilidad.

#### ⚙️ Comando de Ejecución Local:
```bash
k6 run k6/script/test-api-energiai.js
```

---

#### 📊 Evidencias de Ejecución (Ciclo de Vida de Carga)

| 🟢 1. Estado Inicial (Basal) | 🟡 2. Pico de Uso CPU (`htop`) | 🟢 3. Reporte Final (`k6`) |
|:---:|:---:|:---:|
| ![Energiai Baseline](./k6/images/energiai/analisis-energetico-htop-baseline.png) | ![Energiai CPU Peak](./k6/images/energiai/analisis-energetico-htop-cpu-peak.png) | ![Energiai Metrics](./k6/images/energiai/analisis-energetico-k6-metrics-verde.png) |
| *Servidor listo para la ingesta de DTOs.* | *Inferencia y parseo JSON con consumo estable (418 MB RAM).* | *133 DTOs procesados con p(95) = 74.36 ms y 0% errores.* |

* **Resultados Métrica SLA:** 133 DTOs procesados | **p(95) = 74.36 ms** | **0.00%** tasa de fallos.
* **Gestión de Memoria JVM:** La memoria residente (RES) se congeló en **418 MB / 954 MB**, confirmando la ausencia total de fugas de memoria (*memory leaks*).

---

### ⚡ 2. Ingesta y Cálculo de Dominio Real (`POST /analisis-energetico`)

Prueba de carga funcional sobre el servicio de negocio principal. Evalúa el parseo y deserialización de payloads JSON, cálculo nativo de consumo mensual/diario con `java.time.YearMonth`, invocación al cliente de inferencia de ML y formateo de respuestas de observabilidad.

#### ⚙️ Comando de Ejecución Local:
```bash
k6 run k6/script/test-api-energiai.js
```

---

### 🏥 1. Diagnóstico de Salud de Infraestructura (`GET /api/v1/health`)

Prueba de carga destructiva sobre el servlet container (Tomcat) para evaluar la gestión de sockets e hilos bajo una demanda máxima de **30 Usuarios Virtuales (VUs)** durante un periodo de 2 minutos.

#### ⚙️ Comando de Ejecución Local:
```bash
k6 run k6/script/test-health.js
```

---

## 🛠️ Tecnologías Utilizadas

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 21 LTS (Virtual Threads) |
| Framework | Spring Boot 3.3.x |
| Validación | Jakarta Bean Validation |
| Integración Externa | RestClient / Spring Web Client |
| Build Tool | Apache Maven |
| Documentación API | Swagger / OpenAPI 3 |
| Control de Versiones | Git + GitHub (`P4154N0/energiai`) |
| Infraestructura | Oracle Cloud Infrastructure (OCI Compute, Free Tier) |
| Persistencia (Fase Futura) | PostgreSQL + TimescaleDB / Flyway |

---

## 📁 Estructura del proyecto

```
energiai-api/
├── .gitignore
├── pom.xml
├── README.md
├── docs/
│   └── images/                              # Diagramas de arquitectura y flujo, referenciados en este README
├── postman/                                 # Colección de Postman para probar los endpoints (ver sección Postman)
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── energiai/
    │   │           └── api/
    │   │               ├── client/          # Clientes para consumir la API de Python (con Mock-Fallback)
    │   │               ├── config/          # Configuraciones globales (CORS, Beans, RestClient)
    │   │               ├── controller/      # Endpoints REST anémicos
    │   │               ├── exception/       # Manejador global de excepciones
    │   │               ├── model/
    │   │               │   ├── dto/
    │   │               │   │   ├── request/  # DTOs de entrada
    │   │               │   │   └── response/ # DTOs de salida
    │   │               │   └── entity/       # Entidades de dominio (fase futura)
    │   │               ├── repository/      # Capa de acceso a datos (fase futura)
    │   │               ├── service/         # Interfaces de lógica de negocio
    │   │               │   └── impl/        # Implementación del negocio
    │   │               └── EnergiaiApiApplication.java
    │   └── resources/
    │       ├── application.properties
    │       └── db/
    │           └── migration/               # Scripts de Flyway (Fase Futura)
    └── test/                                # Tests unitarios con JUnit 5 y Mockito
```

---

## ⚙️ Instalación y ejecución local

### 1. Cloná el repositorio y posicionate en tu rama

```bash
git clone [https://github.com/P4154N0/energiai.git](https://github.com/P4154N0/energiai.git)
cd energiai/backend-java

```

### 2. Compilá e iniciá la aplicación

En Windows (PowerShell / CMD):
```bash
mvnw.cmd spring-boot:run
```

En Linux / macOS / Git Bash:
```bash
./mvnw spring-boot:run
```

La aplicación estará escuchando en: `http://localhost:8080`

---

## 📊 Contrato de datos con el modelo (Data Science)

El equipo de Ciencia de Datos entrena el modelo con un dataset propio. Estas son las variables **ya confirmadas y cerradas** por el equipo de Data Science, y cómo se traducen al backend en Java.

| Columna (Python) | Dtype | Tipo en Java | Descripción |
|---|---|---|---|
| `Household_Size` | int64 | `Integer` | Cantidad de personas en el hogar |
| `Has_AC` | int64 | `Integer` | Si el hogar cuenta con aire acondicionado |
| `Home_Office` | bool | `Boolean` | Si se realiza home office en la vivienda |
| `Housing_Type` | object | `HousingType` (enum) | Tipo de vivienda |
| `Equipment_Count` | int64 | `Integer` | Cantidad de equipos eléctricos |
| `Avg_Energy_Consumption_kWh` | float64 | `Double` | Consumo energético promedio diario, calculado por Java (ver sección siguiente) |
| `Peak_Usage_Level` | object | `PeakUsageLevel` (enum) | Nivel de uso en horario pico |

Detalle completo del dataset y la metodología de entrenamiento en [`data-science/README.md`](../data-science/README.md).

---

### Enums

Estos dos campos tienen valores fijos y cerrados, confirmados por Data Science, por lo que se modelan como `enum` en vez de `String` libre — así el backend rechaza automáticamente cualquier valor inválido, sin depender de validación manual.

```java
package com.energiai.api.model.dto.request;

public enum HousingType {
    CASA,
    DEPARTAMENTO,
    MONOAMBIENTE
}
```

```java
package com.energiai.api.model.dto.request;

public enum PeakUsageLevel {
    LOW,
    MEDIUM,
    HIGH
}
```

---

### DTO de entrada (`ConsumoEnergeticoRequest`)

El usuario **no envía** `Avg_Energy_Consumption_kWh` directamente — envía el **consumo total del mes anterior**, y Java calcula el promedio diario (ver sección siguiente).

```Java
package com.energiai.api.model.dto.request;

public class ConsumoEnergeticoRequest {

    private Integer householdSize;
    private Integer hasAc;
    private Boolean homeOffice;
    private HousingType housingType;
    private Integer equipmentCount;
    private Double consumoTotalMesAnterior;
    private PeakUsageLevel peakUsageLevel;
    private Double tarifaKwh; // Tarifa configurable ($/kWh)

    // Getters, setters y validaciones Jakarta Bean Validation
}
```
---

## 🧮 Cálculo del Consumo Promedio Diario

Java calcula el consumo diario de forma precisa utilizando las librerías de fecha nativas:

* Obtiene la fecha actual del sistema.
* Identifica el mes anterior completo.
* Determina la cantidad exacta de días del mes anterior usando `java.time.YearMonth` (gestionando automáticamente bisiestos y meses de 28, 29, 30 o 31 días).
* Divide el consumo mensual por dicha cantidad de días:

$$\text{Avg\_Energy\_Consumption\_kWh} = \frac{\text{consumoTotalMesAnterior}}{\text{díasDelMesAnterior}}$$

Esta lógica se ejecuta en `AnalisisEnergeticoServiceImpl` antes de invocar la llamada remota.

---

## 🔄 Mock-Fallback (Client Layer & Resiliencia)

La capa `client/` implementa el patrón **Fallback** para asegurar disponibilidad continua del servicio:

* **Estrategia Normal:** Invoca la API de Python en FastAPI mediante `RestClient`. Si responde correctamente, el backend retorna la predicción del modelo e indica fuente `IA_PYTHON_REAL`.
* **Estrategia Contingencia:** Si la API de Python no responde, expira el timeout o arroja un error de red, la excepción es capturada y sustituida en tiempo real por `MlModelClientMock`. Este genera un diagnóstico basado en reglas de negocio y marca los metadatos como `MOCK_FALLBACK`.

Gracias a esto, el frontend puede mostrar visualmente el estado de la conexión mediante el pulsador de observabilidad en el header.

---

## 📄 Endpoints del MVP

| Método | Endpoint               | Descripción                                                                             |
|--------|------------------------|-----------------------------------------------------------------------------------------|
| `GET`  | `/api/v1/health`       | Chequeo de estado del servicio backend                                                  |
| `POST` | `/analisis-energetico` | Procesa el consumo, calcula costos y devuelve categoría, probabilidad y recomendaciones |

---

### Ejemplo de Request (`POST /analisis-energetico`)

```json
{
  "householdSize": 4,
  "hasAc": 1,
  "homeOffice": true,
  "housingType": "CASA",
  "equipmentCount": 10,
  "consumoTotalMesAnterior": 420.0,
  "peakUsageLevel": "HIGH",
  "tarifaKwh": 0.75
}
```
---

### Ejemplo de response

```JSON
{
  "categoria": "Ineficiente",
  "probabilidad": 0.81,
  "recomendaciones": [
    "Reducir el uso de equipos durante los horarios pico",
    "Evaluar equipos con alto consumo energético",
    "Distribuir las actividades de mayor consumo a lo largo del día"
  ],
  "costoEstimadoMensual": 315.00,
  "fuenteRespuesta": "IA_PYTHON_REAL"
}
```
---

## 📮 Postman

En la carpeta [`/postman`](../postman) se encuentra la colección lista para importar con los endpoints documentados y ejemplos de prueba.

---

## 🚀 Despliegue en OCI

* **VM Java (IP Pública):** `163.176.43.143:8080`
* **VM Python (IP Privada interna):** `10.0.0.164:8000`
* **Servicio:** Ejecutado en segundo plano y administrado vía `systemd`.

---

## 👥 Atribución de Roles y Equipo

* **[Héctor Pablo Graff (P4154N0)](https://www.linkedin.com/in/hector-pablo-graff/):** **Arquitecto de Software & Principal Developer Backend Java**
    * Diseño de Arquitectura por capas, Controllers Anémicos y Lógica Agnóstica.
    * Implementación de DTOs, Bean Validation, RestClient e Integración con FastAPI.
    * Estrategia de Resiliencia, Patrón Fallback y Metadatos de Observabilidad.
    * Despliegue e infraestructura en Oracle Cloud (OCI).
* **Colaboradores Backend:** Agustina Lerda, Annie Lehmann, Frank Mijhael Bendezu Hinostroza.

---

⬅️ Volver al [README principal del proyecto](../README.md)

---

Developed 💻 with ❤️ by **[P4154N0](https://www.linkedin.com/in/hector-pablo-graff/)** from 🇦🇷 who takes 🧉 and ❤️ country music 🤠 🇨🇦