<div align="center">

# 🐍 EnergIAi — Machine Learning Service (FastAPI)

*Microservicio de inferencia que clasifica el perfil de consumo energético de una vivienda.*

[![Python](https://img.shields.io/badge/Python-3.12-blue?logo=python)](#️-tecnologías)
[![FastAPI](https://img.shields.io/badge/FastAPI-ML%20Service-009688?logo=fastapi)](#️-tecnologías)
[![Scikit-Learn](https://img.shields.io/badge/Scikit--Learn-model-F7931E?logo=scikitlearn)](#️-tecnologías)
[![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker)](#-docker)
[![Deploy](https://img.shields.io/badge/OCI-desplegado-brightgreen?logo=oracle)](#️-infraestructura)

</div>

⬅️ Volver al [README principal del proyecto](../README.md)

---

## 📖 Índice

- [Descripción](#-descripción)
- [Arquitectura](#-arquitectura)
- [Infraestructura](#️-infraestructura)
- [Tecnologías](#️-tecnologías)
- [Endpoints](#-endpoints)
- [Contrato de entrada](#-contrato-de-entrada)
- [Respuesta](#-respuesta)
- [Integración con Java](#-integración-con-java)
- [Ejecución local](#-ejecución-local)
- [Docker](#-docker)
- [Estructura](#-estructura)
- [Responsabilidades del servicio](#-responsabilidades-del-servicio)

---

## 📋 Descripción

Este servicio forma parte de la arquitectura distribuida de **EnergIAi** y se ejecuta de forma independiente del backend principal en Java.

Su única responsabilidad es realizar **inferencia** sobre un modelo previamente entrenado por el equipo de Data Science (ver [`data-science/README.md`](../data-science/README.md)). Toda la lógica de negocio, validaciones, cálculo del consumo promedio diario y orquestación permanecen en la API desarrollada en Java.

El backend de Java recibe la información enviada por el usuario, valida los datos, calcula el consumo promedio diario y envía únicamente las variables necesarias a este servicio. FastAPI carga el modelo serializado (`energy_profile_model.pkl`) y devuelve:

- Categoría energética.
- Probabilidad de la categoría predicha.
- Distribución completa de probabilidades para las tres clases.

Este desacoplamiento permite evolucionar el modelo de Machine Learning sin modificar el backend principal.

---

## 🏗 Arquitectura

```
                     Usuario
                        │
                        ▼
          Spring Boot API (Java 21)
                 OCI VM Pública
                        │
                  HTTP REST
                 POST /predict
                        │
                        ▼
          FastAPI ML Service
        OCI VM (acceso restringido
           por firewall interno)
                        │
                        ▼
          energy_profile_model.pkl
      Logistic Regression (F1 = 0.85)
```

La API de FastAPI no queda expuesta al exterior en la práctica: su acceso está restringido únicamente a la VM donde corre el backend Java, mediante reglas de firewall dentro de la VCN de Oracle Cloud Infrastructure.

---

## ☁️ Infraestructura

✅ **Desplegado y corriendo en producción.**

| Máquina | Rol | IP pública | IP privada | Puerto | Acceso |
|---|---|---|---|---|---|
| VM Java | Backend principal | `163.176.43.143` | `10.0.0.213` | 8080 | Público |
| VM Python | Servicio ML | `147.15.16.156` | `10.0.0.164` | 8000 | Restringido por firewall a `10.0.0.0/24` |

> **Nota:** el diseño original preveía una subred privada sin salida a internet para esta VM. Las cuentas *Always Free* de OCI no incluyen NAT Gateway, así que la VM Python quedó con IP pública (necesaria para instalar dependencias vía `apt`/`pip`). El aislamiento se logra en su lugar con **reglas de firewall**: la Security List de OCI y el `iptables` interno solo aceptan tráfico al puerto 8000 desde la subred interna — el resultado de seguridad es equivalente, ninguna IP externa puede alcanzar el servicio directamente.

El proceso corre como servicio `systemd` (`energiai-ml.service`): persistente, se reinicia solo ante fallos o reinicio de la VM. Detalle completo del despliegue (paso a paso, `iptables`, troubleshooting) en [`../oci/README.md`](../oci/README.md).

Verificación (debe ejecutarse desde dentro de la VCN, por ejemplo desde la VM Java):
```bash
curl http://10.0.0.164:8000/health
# {"status":"ok","modelo_cargado":true}
```

---

## 🛠 Tecnologías

| Componente | Tecnología |
|---|---|
| Lenguaje | Python 3.12 |
| Framework | FastAPI |
| Validación | Pydantic |
| Modelo | Scikit-Learn |
| Servidor ASGI | Uvicorn |
| Serialización | Pickle (.pkl) |
| Documentación | Swagger / OpenAPI |
| Infraestructura | Oracle Cloud Infrastructure |
| Gestión de proceso | systemd |
| Containerización | Docker (alternativa a systemd) |

---

## 📄 Endpoints

### GET /health

Permite verificar que el servicio está disponible y que el modelo fue cargado correctamente.

```json
{
    "status": "ok",
    "modelo_cargado": true
}
```

### POST /predict

Ejecuta una predicción utilizando el modelo entrenado.

Swagger interactivo:

```
http://<host>:8000/docs
```

---

## 📥 Contrato de entrada

```json
{
  "household_size": 4,
  "has_ac": 1,
  "home_office": true,
  "housing_type": "CASA",
  "equipment_count": 10,
  "avg_energy_consumption_kwh": 14.0,
  "peak_usage_level": "HIGH"
}
```

### Housing Type

Valores aceptados: `CASA`, `DEPARTAMENTO`, `MONOAMBIENTE`.

Internamente el servicio realiza la traducción hacia las categorías utilizadas durante el entrenamiento del modelo (por ejemplo, `CASA → Casa`).

### Peak Usage Level

Valores admitidos: `LOW`, `MEDIUM`, `HIGH`. También son traducidos internamente antes de invocar el modelo.

### Avg Energy Consumption

El valor **no es calculado por FastAPI**. Debe ser enviado por el backend Java luego de calcular:

```
Consumo total del mes anterior
──────────────────────────────
Cantidad de días del mes
```

Esta decisión mantiene completamente desacoplada la lógica de negocio del servicio de Machine Learning.

---

## 📤 Respuesta

```json
{
    "categoria": "Moderado",
    "probabilidad": 0.5743,
    "probabilidades": {
        "Eficiente": 0.424,
        "Moderado": 0.5743,
        "Ineficiente": 0.0016
    }
}
```

---

## 🔄 Integración con Java

El backend Java consume este servicio mediante `MlModelClientImpl`, apuntando a `http://10.0.0.164:8000/predict` en el entorno desplegado (o `http://localhost:8000/predict` en local).

Flujo de ejecución:

```
Usuario → Spring Boot → POST /predict → FastAPI → Modelo ML → Respuesta → Spring Boot → Usuario
```

Si la comunicación falla por timeout, conexión rechazada o el modelo no se encuentra disponible (`503`), el backend Java activa automáticamente el mecanismo de **Mock-Fallback**, garantizando la continuidad del servicio. Ver detalle en [`backend-java/README.md`](../backend-java/README.md#-mock-fallback-client-layer).

---

## 🚀 Ejecución local

Instalar dependencias:

```bash
pip install -r requirements.txt
```

Ejecutar:

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

La documentación estará disponible en `http://localhost:8000/docs`.

---

## 🐳 Docker

Construcción:

```bash
docker build -t energiai-ml .
```

Ejecución:

```bash
docker run -p 8000:8000 energiai-ml
```

---

## 📁 Estructura

```
backend-python/
├── app/
│   ├── main.py
│   ├── model.py
│   └── schemas.py
├── model/
│   └── energy_profile_model.pkl
├── Dockerfile
├── requirements.txt
└── README.md
```

---

## 📚 Responsabilidades del servicio

Este microservicio tiene una única responsabilidad:

- Cargar el modelo entrenado.
- Validar el contrato de entrada.
- Traducir los enums utilizados por Java.
- Ejecutar la inferencia.
- Devolver las probabilidades del modelo.

**No** implementa lógica de negocio, **no** calcula consumos, **no** realiza persistencia y **no** conoce reglas funcionales del dominio. Todo ese comportamiento permanece centralizado en la API desarrollada en Java, respetando el principio de **Single Responsibility** y favoreciendo el desacoplamiento entre ambas aplicaciones.

---

## 👥 Atribución de Roles y Equipo

* **En la sección de Créditos / Equipo, agregar la sección explícita de Atribución de Roles y Equipo alineada con los créditos reales:**
    * Héctor Pablo Graff (P4154N0): Desarrollo del microservicio FastAPI, contrato de datos con Pydantic, desacoplamiento y despliegue en OCI (systemd + iptables).
    * Hernán Pérez Melgar: Entrenamiento del modelo de Machine Learning (energy_profile_model.pkl).
    * Jonathan Marino: Curado y preparación del dataset original.

---

⬅️ Volver al [README principal del proyecto](../README.md)

---

Developed 💻 with ❤️ by **[P4154N0](https://www.linkedin.com/in/hector-pablo-graff/)** from 🇦🇷 who takes 🧉 and ❤️ country music 🤠 🇨🇦