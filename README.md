<div align="center">

# ⚡ EnergIAi

### Análisis Inteligente de Consumo Energético Residencial

*Transformamos datos crudos de consumo eléctrico en decisiones más sostenibles.*

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange?logo=openjdk)](#-tecnologías)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.x-brightgreen?logo=springboot)](#-tecnologías)
[![Python](https://img.shields.io/badge/Python-3.12-blue?logo=python)](#-tecnologías)
[![FastAPI](https://img.shields.io/badge/FastAPI-ML%20Service-009688?logo=fastapi)](#-tecnologías)
[![OCI](https://img.shields.io/badge/Oracle%20Cloud-Infrastructure-F80000?logo=oracle)](#️-arquitectura-de-infraestructura-oci)
[![Hackathon](https://img.shields.io/badge/Hackathon-ONE%20G9%20LATAM-purple)](https://alura-es-cursos.github.io/proyectos-hackathon-g9-latam/)
[![Status](https://img.shields.io/badge/status-MVP%20Production%20Ready-brightgreen)](#-estado-del-proyecto)

</div>

---

## 📖 Índice

- [Descripción](#descripción)
- [Estado del proyecto](#estado-del-proyecto)
- [Problema y Necesidad](#problema-y-necesidad)
- [Características Clave](#características-clave)
- [Interfaz de Usuario y Observabilidad](#interfaz)
- [Arquitectura del Sistema](#arquitectura-sistema)
- [Arquitectura de Infraestructura (OCI)](#arquitectura-oci)
- [Pruebas de Carga y Rendimiento (Benchmarking)](#benchmarking)
- [Componentes del Proyecto](#componentes)
- [Tecnologías](#tecnologías)
- [Dataset](#dataset)
- [Atribución de Roles y Equipo](#equipo)
- [Cómo ejecutar el proyecto](#ejecucion)
- [Documentación Técnica](#documentacion-tecnica)
- [Créditos](#créditos)

---

<a id="descripción"></a>
## 📋 Descripción

**EnergIAi** es una plataforma integral que analiza el consumo eléctrico residencial mediante Inteligencia Artificial y reglas de negocio adaptativas. A partir de parámetros como el consumo mensual, cantidad de electrodomésticos, rutina en horario pico y tarifa contratada, la solución:

- Clasifica el perfil energético de una vivienda (**Eficiente**, **Moderado** o **Ineficiente**).
- Genera recomendaciones concretas y personalizadas para reducir el desperdicio energético.
- Estima el impacto financiero mensual en tiempo real mediante una **tarifa eléctrica configurable ($/kWh)**.
- Implementa una arquitectura resiliente con **patrón Fallback** y observabilidad en la interfaz en tiempo real.

Proyecto ideado originalmente para el **Hackathon ONE — Proyectos G9 | Alura + Oracle**, dentro del track *Sostenibilidad, Energía y Casas Inteligentes*.

---

<a id="estado-del-proyecto"></a>
## 🚦 Estado del Proyecto

| Componente | Estado |
|---|---|
| **Infraestructura OCI** (VCN, Subred, Security Lists, 2 VMs Compute) | ✅ Desplegada y operativa |
| **API de Machine Learning** (Python 3.12 / FastAPI) | ✅ Desplegada en OCI como servicio `systemd` |
| **Modelo de Clasificación** (`.pkl`) | ✅ Entrenado, evaluado y servido en producción |
| **Backend Principal** (Java 21 LTS / Spring Boot 3.3.x) | ✅ 100% Funcional — Arquitectura por capas, Bean Validation y Fallback |
| **Frontend & Interfaz de Usuario** (HTML5 / CSS3 / Vanilla JS) | ✅ 100% Funcional — Velocímetro dinámico, Tarifa configurable e indicador visual de estado |

---

<a id="problema-y-necesidad"></a>
## 🧩 Problema y Necesidad

Muchos usuarios residenciales reciben facturas eléctricas elevadas sin entender qué hábitos o equipos generan dicho impacto. **EnergIAi** transforma datos crudos de consumo en diagnóstico claro e interactivo, permitiendo:

1. Visibilidad directa del costo mensual estimado en función de la tarifa contratada.
2. Identificación inmediata de ineficiencias de consumo.
3. Recomendaciones automatizadas orientadas al ahorro y consumo consciente.

---

<a id="características-clave"></a>
## ✨ Características Clave

* **Tarifa Configurable ($/kWh):** Cálculo financiero adaptativo según el valor por unidad ingresado por el usuario o empresa.
* **Internacionalización Nativa (i18n):** Interfaz bilingüe (**Español / Inglés**) conmutable en tiempo real sin recargar la página.
* **Inyección de Fallas & Resiliencia (Kill Switch):** Incluye un interruptor en cabecera (*Simular Caída*) que intercepta la solicitud y fuerza el flujo hacia el cliente `MlModelClientMock` para probar la tolerancia a fallos en vivo.
* **Observabilidad Visual Reactiva:** El frontend detecta el origen de los datos (`IA_PYTHON_REAL` vs `MOCK_FALLBACK`) y conmuta automáticamente el indicador de la cabecera:
  * 🟢 `API CONECTADA · AGRARIO` (Respuesta real servida por el modelo en Python).
  * 🔴 `API DESCONECTADA · MODO FALLBACK` (Respuesta de contingencia por simulación o caída de red).

---

<a id="interfaz"></a>
## 📸 Interfaz de Usuario y Observabilidad

### 1. Clasificación del Perfil Energético y Soporte Bilingüe (ES / EN)

| 🟢 Perfil Eficiente | 🟡 Perfil Moderado | 🔴 Perfil Ineficiente |
|:---:|:---:|:---:|
| ![Perfil Eficiente](docs/images/eficiente.png) | ![Perfil Moderado](docs/images/moderado.png) | ![Perfil Ineficiente](docs/images/ineficiente.png) |
| *Consumo optimizado con bajo impacto financiero.* | *Consumo dentro del promedio con margen de mejora.* | *Consumo elevado con alertas y recomendaciones de ahorro.* |

> 🌐 **Internacionalización:** Toda la interfaz y el diagnósticos son conmutables en tiempo real entre **Español (ES)** e **Inglés (EN)** en un solo clic.

---

### 2. Observabilidad y Resiliencia en Tiempo Real (Kill Switch & Fallback)

| 🟢 Operación Normal (`IA_PYTHON_REAL`) | 🔴 Simulación de Caída Activa (`MOCK_FALLBACK`) |
|:---:|:---:|
| ![API Conectada](docs/images/eficiente.png) | ![Modo Fallback](docs/images/mock.png) |
| *Inferencia servida en tiempo real por el microservicio en Python.* | *Respuesta de contingencia forzada desde el Toggle de Simulación.* |

---

### 🎬 Demostración en Vivo (Live Demo)

<div align="center">
  <a href="https://www.youtube.com/watch?v=ID_DE_TU_VIDEO" target="_blank">
    <img src="https://img.youtube.com/vi/ID_DE_TU_VIDEO/maxresdefault.jpg" alt="Ver Demo en YouTube" width="85%" style="border-radius: 10px; box-shadow: 0 4px 20px rgba(0,0,0,0.3);">
  </a>
  <p><em>▶️ Haz clic en la imagen para ver la demostración interactiva en YouTube (Prueba de Kill Switch y conmutación ES/EN).</em></p>
</div>

---

<a id="arquitectura-sistema"></a>
## 🏗️ Arquitectura del Sistema

```mermaid
graph TD
    A["Usuario / Navegador"] -->|"HTTP POST"| B["API Principal Java (Spring Boot)<br/>Oracle Cloud (VM Pública)"]
    
    B --> C{"¿Estado de Conexión con VM Python?"}
    
    C -->|"Conexión OK"| D["API ML (FastAPI)<br/>Oracle Cloud"]
    C -->|"Error / Red Caída"| E["MlModelClientMock<br/>(Modo Resiliencia Offline-First)"]
    
    D --> F["Modelo Predictivo (.pkl)"]
    
    F --> G["AnalisisEnergeticoResponse<br/>(Metadatos de Observabilidad)"]
    E --> G

    style B fill:#f97316,stroke:#333,stroke-width:2px,color:#fff
    style C fill:#3b82f6,stroke:#333,stroke-width:2px,color:#fff
    style D fill:#10b981,stroke:#333,stroke-width:2px,color:#fff
    style E fill:#64748b,stroke:#333,stroke-width:2px,color:#fff
    style G fill:#8b5cf6,stroke:#333,stroke-width:2px,color:#fff
```

---

<a id="arquitectura-oci"></a>
## ☁️ Arquitectura de Infraestructura (OCI)

El proyecto se encuentra alojado en **Oracle Cloud Infrastructure (Free Tier)** en la región Brazil East (São Paulo):

| Máquina | Rol | IP pública | IP privada | Puerto | Estado |
|---|---|---|---|---|---|
| **VM Java** | Backend Principal (Spring Boot 3.3.x) | `163.176.43.143` | `10.0.0.213` | 8080 | ✅ Operativa |
| **VM Python** | Inferencia ML (FastAPI + `.pkl`) | `147.15.16.156` | `10.0.0.164` | 8000 | ✅ Operativa (systemd) |

*Nota de Seguridad:* La comunicación hacia la VM de Python está restringida mediante **Security Lists de OCI** e `iptables`, permitiendo tráfico al puerto 8000 únicamente desde la subred interna (`10.0.0.0/24`).

---

<a id="benchmarking"></a>
## ⚡ Pruebas de Carga, Rendimiento y Elasticidad (Benchmarking)

Para garantizar un estándar de producción real, la API desplegada en **Oracle Cloud Infrastructure (OCI)** fue sometida a pruebas de carga destructiva y concurrencia utilizando **Grafana k6**, monitoreando en tiempo real la salud del hardware (`htop`) en la Virtual Machine Ubuntu.

---

### 🏥 1. Diagnóstico de Infraestructura (`GET /health`)
* **Concurrencia Probada:** Ráfagas de hasta **30 usuarios virtuales (VUs)** simultáneos.
* **Elasticidad de Procesador:** La JVM despertó los núcleos bajo demanda pasando de **0.7% a 27.2% de CPU**, retornando al estado basal inmediatamente al finalizar.
* **Métrica SLA:** Latencia **$p(95) = 57.37\text{ ms}$** y **0.00% tasa de fallos** sobre 1,687 peticiones.

| 🟢 1. Estado Inicial (Basal) | 🟡 2. Pico de Carga (30 VUs) | 🟢 3. Reporte Final (`k6`) |
|:---:|:---:|:---:|
| ![Health Baseline](./backend-java/k6/images/health/health-htop-baseline.png) | ![Health CPU Peak](./backend-java/k6/images/health/health-htop-cpu-peak.png) | ![Health Metrics](./backend-java/k6/images/health/health-k6-metrics-verde.png) |
| *Servidor en reposo (0.7% CPU, ~420 MB RAM).* | *Escalado elástico de CPU sin degradar la memoria.* | *1,00% de éxito, 0% errores y $p(95) < 58\text{ ms}$.* |

---

### 🧪 2. Servicio de Ingesta y Cálculo Energético (`POST /analisis-energetico`)
* **Carga de Negocio:** Procesamiento e interpretación de DTOs JSON con evaluación del perfil energético.
* **Estabilidad de Memoria:** Memoria RAM congelada en **418 MB / 954 MB** demostrando la ausencia de fugas de memoria (*memory leaks*).
* **Métrica SLA:** Latencia **$p(95) = 74.36\text{ ms}$** y **0.00% tasa de fallos** sobre 133 peticiones reales.

| 🟢 1. Estado Inicial (Basal) | 🟡 2. Pico de Carga (5 VUs) | 🟢 3. Reporte Final (`k6`) |
|:---:|:---:|:---:|
| ![Energiai Baseline](./backend-java/k6/images/energiai/analisis-energetico-htop-baseline.png) | ![Energiai CPU Peak](./backend-java/k6/images/energiai/analisis-energetico-htop-cpu-peak.png) | ![Energiai Metrics](./backend-java/k6/images/energiai/analisis-energetico-k6-metrics-verde.png) |
| *Servidor listo para recibir payloads de ingesta.* | *Absorción de carga JSON manteniendo consumo en ~418 MB.* | *133 peticiones POST procesadas en $< 75\text{ ms}$.* |

---

<a id="componentes"></a>
## 📦 Componentes del Proyecto

| Carpeta | Responsabilidad | Documentación |
|---|---|---|
| [`backend-java/`](./backend-java) | API principal, orquestador, reglas de negocio y fallback | [README](./backend-java/README.md) |
| [`backend-python/`](./backend-python) | Servicio de Machine Learning (Inferencia FastAPI) | [README](./backend-python/README.md) |
| [`data-science/`](./data-science) | Análisis exploratorio, entrenamiento y serialización | [README](./data-science/README.md) |
| [`oci/`](./oci) | Infraestructura, scripts y reglas de firewall | [README](./oci/README.md) |
| `postman/` | Colección de Postman para pruebas de integración | — |

---

<a id="tecnologías"></a>
## 🛠️ Tecnologías

* **Backend Core:** Java 21 LTS + Spring Boot 3.3.x + Jakarta Validation.
* **Microservicio ML:** Python 3.12 + FastAPI + Scikit-Learn + Uvicorn.
* **Frontend:** HTML5 + CSS3 (Custom Properties & Keyframes) + JavaScript Vanilla.
* **Infraestructura Cloud:** Oracle Cloud Infrastructure (OCI Compute, VCN, Security Rules).
* **Metodología y Control:** Git, Conventional Commits, OpenAPI/Swagger.

---

<a id="dataset"></a>
## 📊 Dataset

El modelo predictivo fue entrenado utilizando el dataset público **[Household Energy Consumption](https://www.kaggle.com/datasets/samxsam/household-energy-consumption)** de Kaggle, procesado y optimizado para clasificación supervisada.

---

<a id="equipo"></a>
## 👥 Atribución de Roles y Equipo

* **[Jonathan Marino](https://www.linkedin.com/in/jonathan-marino/):** Exploración, curado y limpieza del dataset (EDA).
* **[Hernán Pérez Melgar](https://www.linkedin.com/in/hernan-perez-melgar-320088184/):** Entrenamiento y evaluación del modelo de clasificación (`.pkl`).
* **[Héctor Pablo Graff (P4154N0)](https://www.linkedin.com/in/hector-pablo-graff/):** **Ingeniería de Software & Desarrollo Integral End-to-End**
  * Arquitectura del Backend Core en Java 21 / Spring Boot 3.3.x (DTOs, Validaciones, Estrategia de Fallback).
  * Microservicio de Inferencia de ML en Python / FastAPI.
  * Arquitectura, despliegue y hardening de infraestructura en Oracle Cloud Infrastructure (OCI).
  * **Pruebas de Carga, Estrés y Benchmarking (Grafana k6):** Diseño de la suite de stress testing, auditoría de métricas de SLA ($p(95) < 75\text{ ms}$) y observabilidad de hardware (`htop`) en la nube.
  * Diseño y desarrollo de la Interfaz Frontend, velocímetro dinámico, tarifa configurable y observabilidad visual del estado de API en tiempo real.
* **Soporte Colaborativo / Integrantes:** Agustina Lerda, Annie Lehmann, Frank Mijhael Bendezu Hinostroza.

---

<a id="ejecucion"></a>
## ⚙️ Cómo Ejecutar el Proyecto

### 1. Clonar el repositorio

```bash
git clone [https://github.com/P4154N0/energiai.git](https://github.com/P4154N0/energiai.git)
cd energiai
```
---

### 2. Iniciar el Microservicio de IA (Python)

```bash
cd backend-python
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```
---

### 3. Iniciar el Backend Principal (Java)

```bash
cd backend-java
./mvnw spring-boot:run
```
---

La aplicación estará accesible desde el navegador en http://localhost:8080.

---

<a id="documentacion-tecnica"></a>
## 📄 Documentación Técnica

http://localhost:8080/swagger-ui.html

---

<a id="créditos"></a>
## 🙌 Créditos & Agradecimientos

Proyecto desarrollado en el marco de la simulación laboral de **No Country** junto al programa **ONE (Oracle Next Education)** — Proyectos G9 | **Alura Latam** + **Oracle**.

---

Developed 💻 with ❤️ by P4154N0 from 🇦🇷 who takes 🧉 and ❤️ country music 🤠 🇨🇦
