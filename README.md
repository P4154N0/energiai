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

- [Descripción](#-descripción)
- [Estado del proyecto](#-estado-del-proyecto)
- [Problema y Necesidad](#-problema-y-necesidad)
- [Características Clave](#-características-clave)
- [Interfaz de Usuario y Observabilidad](#-Interfaz-de-Usuario-y-Observabilidad)
- [Arquitectura del Sistema](#️-arquitectura-del-sistema)
- [Arquitectura de Infraestructura (OCI)](#️-arquitectura-de-infraestructura-oci)
- [Componentes del Proyecto](#-componentes-del-proyecto)
- [Tecnologías](#️-tecnologías)
- [Dataset](#-dataset)
- [Atribución de Roles y Equipo](#-atribución-de-roles-y-equipo)
- [Cómo ejecutar el proyecto](#-cómo-ejecutar-el-proyecto)
- [Documentación Técnica](#-documentación-técnica)
- [Créditos](#-créditos)

---

## 📋 Descripción

**EnergIAi** es una plataforma integral que analiza el consumo eléctrico residencial mediante Inteligencia Artificial y reglas de negocio adaptativas. A partir de parámetros como el consumo mensual, cantidad de electrodomésticos, rutina en horario pico y tarifa contratada, la solución:

- Clasifica el perfil energético de una vivienda (**Eficiente**, **Moderado** o **Ineficiente**).
- Genera recomendaciones concretas y personalizadas para reducir el desperdicio energético.
- Estima el impacto financiero mensual en tiempo real mediante una **tarifa eléctrica configurable ($/kWh)**.
- Implementa una arquitectura resiliente con **patrón Fallback** y observabilidad en la interfaz en tiempo real.

Proyecto ideado originalmente para el **Hackathon ONE — Proyectos G9 | Alura + Oracle**, dentro del track *Sostenibilidad, Energía y Casas Inteligentes*.

---

## 🚦 Estado del Proyecto

| Componente | Estado |
|---|---|
| **Infraestructura OCI** (VCN, Subred, Security Lists, 2 VMs Compute) | ✅ Desplegada y operativa |
| **API de Machine Learning** (Python 3.12 / FastAPI) | ✅ Desplegada en OCI como servicio `systemd` |
| **Modelo de Clasificación** (`.pkl`) | ✅ Entrenado, evaluado y servido en producción |
| **Backend Principal** (Java 21 LTS / Spring Boot 3.3.x) | ✅ 100% Funcional — Arquitectura por capas, Bean Validation y Fallback |
| **Frontend & Interfaz de Usuario** (HTML5 / CSS3 / Vanilla JS) | ✅ 100% Funcional — Velocímetro dinámico, Tarifa configurable e indicador visual de estado |

---

## 🧩 Problema y Necesidad

Muchos usuarios residenciales reciben facturas eléctricas elevadas sin entender qué hábitos o equipos generan dicho impacto. **EnergIAi** transforma datos crudos de consumo en diagnóstico claro e interactivo, permitiendo:

1. Visibilidad directa del costo mensual estimado en función de la tarifa contratada.
2. Identificación inmediata de ineficiencias de consumo.
3. Recomendaciones automatizadas orientadas al ahorro y consumo consciente.

---

## ✨ Características Clave

* **Tarifa Configurable ($/kWh):** Permite ingresar la tarifa específica de la distribuidora eléctrica (por defecto $0.75).
* **Defensa en Profundidad y Fallback en Backend:** Si el microservicio de IA en Python no responde o sufre un timeout, el backend Java ataja la excepción y responde mediante un cliente Mock desacoplado, garantizando disponibilidad 100%.
* **Observabilidad Visual en Tiempo Real:** El frontend detecta la fuente de los datos (`IA_PYTHON_REAL` vs `MOCK_FALLBACK`) y conmuta de forma reactiva el pulsador del header:
  * 🟢 `API CONECTADA · AGRARIO` (Respuesta real del modelo IA).
  * 🔴 `API DESCONECTADA · MODO FALLBACK` (Pulsador rojo titilante en caso de contingencia de red).

---

## 📸 Interfaz de Usuario y Observabilidad

### 1. Clasificación del Perfil Energético

| 🟢 Perfil Eficiente | 🟡 Perfil Moderado | 🔴 Perfil Ineficiente |
|:---:|:---:|:---:|
| ![Perfil Eficiente](docs/images/eficiente.jpeg) | ![Perfil Moderado](docs/images/moderado.jpeg) | ![Perfil Ineficiente](docs/images/ineficiente.jpeg) |
| *Consumo optimizado con bajo impacto financiero.* | *Consumo dentro del promedio con margen de mejora.* | *Consumo elevado con alertas y recomendaciones de ahorro.* |

---

### 2. Observabilidad y Resiliencia en Tiempo Real

| 🟢 API Conectada (`IA_PYTHON_REAL`) | 🔴 Modo Fallback (`MOCK_FALLBACK`) |
|:---:|:---:|
| ![API Conectada](docs/images/eficiente.jpeg) | ![Modo Fallback](docs/images/mock.jpeg) |
| *Inferencia en tiempo real servida por el microservicio de ML en Python.* | *Respuesta contingente servida por Java ante caídas de red o timeouts.* |

---

## 🏗️ Arquitectura del Sistema

```text
               Usuario / Navegador
                        │
                        ▼
             Interfaz Web (HTML5/JS)
                        │
                    HTTP POST
                        ▼
          API Principal Java (Spring Boot)
            Oracle Cloud (VM Pública)
                        │
          ┌─────────────┴─────────────┐
   (Conexión OK)               (Error / Red caída)
          │                           │
          ▼                           ▼
  API ML (FastAPI)            MlModelClientMock
    Oracle Cloud                 (Resiliencia)
          │                           │
          ▼                           │
   Modelo (.pkl)                      │
          └─────────────┬─────────────┘
                        │
                        ▼
           AnalisisEnergeticoResponse
        (Metadatos de Observabilidad)
```
## ☁️ Arquitectura de Infraestructura (OCI)

El proyecto se encuentra alojado en **Oracle Cloud Infrastructure (Free Tier)** en la región Brazil East (São Paulo):

| Máquina | Rol | IP pública | IP privada | Puerto | Estado |
|---|---|---|---|---|---|
| **VM Java** | Backend Principal (Spring Boot 3.3.x) | `163.176.43.143` | `10.0.0.213` | 8080 | ✅ Operativa |
| **VM Python** | Inferencia ML (FastAPI + `.pkl`) | `147.15.16.156` | `10.0.0.164` | 8000 | ✅ Operativa (systemd) |

*Nota de Seguridad:* La comunicación hacia la VM de Python está restringida mediante **Security Lists de OCI** e `iptables`, permitiendo tráfico al puerto 8000 únicamente desde la subred interna (`10.0.0.0/24`).

---

## 📦 Componentes del Proyecto

| Carpeta | Responsabilidad | Documentación |
|---|---|---|
| [`backend-java/`](./backend-java) | API principal, orquestador, reglas de negocio y fallback | [README](./backend-java/README.md) |
| [`backend-python/`](./backend-python) | Servicio de Machine Learning (Inferencia FastAPI) | [README](./backend-python/README.md) |
| [`data-science/`](./data-science) | Análisis exploratorio, entrenamiento y serialización | [README](./data-science/README.md) |
| [`oci/`](./oci) | Infraestructura, scripts y reglas de firewall | [README](./oci/README.md) |
| `postman/` | Colección de Postman para pruebas de integración | — |

---

## 🛠️ Tecnologías

* **Backend Core:** Java 21 LTS + Spring Boot 3.3.x + Jakarta Validation.
* **Microservicio ML:** Python 3.12 + FastAPI + Scikit-Learn + Uvicorn.
* **Frontend:** HTML5 + CSS3 (Custom Properties & Keyframes) + JavaScript Vanilla.
* **Infraestructura Cloud:** Oracle Cloud Infrastructure (OCI Compute, VCN, Security Rules).
* **Metodología y Control:** Git, Conventional Commits, OpenAPI/Swagger.

---

## 📊 Dataset

El modelo predictivo fue entrenado utilizando el dataset público **[Household Energy Consumption](https://www.kaggle.com/datasets/samxsam/household-energy-consumption)** de Kaggle, procesado y optimizado para clasificación supervisada.

---

## 👥 Atribución de Roles y Equipo

* **[Jonathan Marino](https://www.linkedin.com/in/jonathan-marino/):** Exploración, curado y limpieza del dataset (EDA).
* **[Hernán Pérez Melgar](https://www.linkedin.com/in/hernan-perez-melgar-320088184/):** Entrenamiento y evaluación del modelo de clasificación (`.pkl`).
* **[Héctor Pablo Graff (P4154N0)](https://www.linkedin.com/in/hector-pablo-graff/):** **Ingeniería de Software & Desarrollo Integral End-to-End**
  * Arquitectura del Backend Core en Java 21 / Spring Boot 3.3.x (DTOs, Validaciones, Estrategia de Fallback).
  * Microservicio de Inferencia de ML en Python / FastAPI.
  * Arquitectura, despliegue y hardening de infraestructura en Oracle Cloud Infrastructure (OCI).
  * Diseño y desarrollo de la Interfaz Frontend, velocímetro dinámico, tarifa configurable y observabilidad visual del estado de API en tiempo real.
* **Soporte Colaborativo / Integrantes:** Agustina Lerda, Annie Lehmann, Frank Mijhael Bendezu Hinostroza.

---

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

🙌 Créditos
Proyecto ideado en el marco del Hackathon ONE — Proyectos G9 | Alura + Oracle.

---

Developed 💻 with ❤️ by P4154N0 from 🇦🇷 who takes 🧉 and ❤️ country music 🤠 🇨🇦
