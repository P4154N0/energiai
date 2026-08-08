# ☁️ EnergIAi — Configuración de Infraestructura en OCI

[![OCI](https://img.shields.io/badge/Oracle%20Cloud-Infrastructure-F80000?logo=oracle)](#-índice)
[![Ubuntu](https://img.shields.io/badge/Ubuntu-24.04-E95420?logo=ubuntu)](#4-instancias-vms)
[![Always Free](https://img.shields.io/badge/OCI-Always%20Free-brightgreen?logo=oracle)](#1-decisión-de-arquitectura-sin-nat-gateway)
[![systemd](https://img.shields.io/badge/systemd-servicio%20persistente-orange?logo=linux)](#54-dejarlo-corriendo-como-servicio-persistente-systemd)

Guía paso a paso de la arquitectura e infraestructura desplegada en **Oracle Cloud Infrastructure (Free Tier)** para la plataforma **EnergIAi**. Incluye decisiones de diseño, mitigación de restricciones del Free Tier y hardening de seguridad a nivel de red y sistema operativo.

Región: **Brazil East (São Paulo)**.

---

## 📖 Índice

1. [Decisión de Arquitectura: Sin NAT Gateway](#1-decisión-de-arquitectura-sin-nat-gateway)
2. [Configuración de Red (VCN)](#2-configuración-de-red-vcn)
3. [Security Lists (Firewall Cloud de OCI)](#3-security-lists-firewall-cloud-de-oci)
4. [Instancias Compute (VMs)](#4-instancias-compute-vms)
5. [VM Python — Despliegue de FastAPI (ML Service)](#5-vm-python--despliegue-de-fastapi-ml-service)
6. [VM Java — Despliegue de Spring Boot (Backend Core)](#6-vm-java--despliegue-de-spring-boot-backend-core)
7. [Alineación y Verificación End-to-End](#7-alineación-y-verificación-end-to-end)
8. [Atribución de Roles y Equipo](#-atribución-de-roles-y-equipo)

---

## 1. Decisión de Arquitectura: Sin NAT Gateway

El diseño inicial contemplaba alojar el microservicio de inferencia en Python dentro de una **subred privada sin salida a Internet**, conectada mediante un NAT Gateway.

**Restricción del Free Tier:** Las cuentas *Always Free* de OCI no incluyen cuota disponible para NAT Gateways (límite en `0`), arrojando el error `NAT gateway limit per VCN reached`.

**Solución Implementada (Defensa en Profundidad):**
Se configuró una única subred pública regional. El aislamiento y la seguridad del microservicio de IA se lograron combinando dos capas de firewall:
1. **IP Pública Habilitada:** Necesaria para la descarga de paquetes (`apt`, `pip`) y actualización del sistema.
2. **Restricción de Tráfico por Firewall:** La **Security List de OCI** y las reglas de `iptables` en la VM de Python bloquean cualquier petición entrante al puerto `8000` que provenga fuera de la subred interna (`10.0.0.0/24`).

---

## 2. Configuración de Red (VCN)

| Recurso | Nombre | Detalle / CIDR |
|---|---|---|
| **VCN** | `vcn-energiai` | CIDR `10.0.0.0/16` |
| **Subred** | `subnet-energiai-public` | CIDR `10.0.0.0/24` (Public Regional) |
| **Internet Gateway** | `ig-energiai` | Conexión pública a la VCN |
| **Tabla de Rutas** | Default Route Table | Regla: `0.0.0.0/0` → `ig-energiai` |

### Pasos de Configuración

1. **Creación de VCN:** `vcn-energiai` con CIDR `10.0.0.0/16` y Nombres de Host DNS habilitados.
2. **Creación de Internet Gateway:** `ig-energiai` asociado a la VCN.
3. **Creación de Subred:** `subnet-energiai-public` (`10.0.0.0/24`).
4. **Enrutamiento:** Adición de la regla `0.0.0.0/0` apuntando a `ig-energiai`.

---

## 3. Security Lists (Firewall Cloud de OCI)

Reglas de **Ingress** configuradas en la `Default Security List`:

| Ingress Source | Protocolo | Puerto Destino | Propósito / Alcance |
|---|---|---|---|
| `0.0.0.0/0` | TCP | `22` | Gestión remota SSH |
| `0.0.0.0/0` | TCP | `8080` | Backend Java Spring Boot & Web Frontend (Acceso Público) |
| `10.0.0.0/24` | TCP | `8000` | Microservicio ML FastAPI (**Exclusivo Tráfico Interno**) |

*Egress:* Permite todo el tráfico saliente (`0.0.0.0/0`).

---

## 4. Instancias Compute (VMs)

Ambas máquinas fueron desplegadas con el shape **`VM.Standard.E2.1.Micro`** (Always Free: 1 OCPU, 1 GB RAM) corriendo **Canonical Ubuntu 24.04 LTS**.

| Instancia | IP Pública | IP Privada | Puerto | Rol |
|---|---|---|---|---|
| `vm-energiai-java` | `163.176.43.143` | `10.0.0.213` | 8080 | Backend Core Java 21 + Web App |
| `vm-energiai-python` | `147.15.16.156` | `10.0.0.164` | 8000 | Inferencia ML FastAPI |

---

### Acceso mediante Llaves SSH

```bash
# Conexión a la VM del Backend Java
ssh -i ssh-key-vm-energiai-java.key ubuntu@163.176.43.143

# Conexión a la VM de Inferencia Python
ssh -i ssh-key-vm-energiai-python.key ubuntu@147.15.16.156
```

---

## 5. VM Python — Despliegue de FastAPI

### 5.1 Instalación de Entorno
```bash
sudo apt update
sudo apt install -y python3 python3-venv python3-pip git
```

### 5.2 Despliegue y Virtualenv
```bash
cd ~/backend-python
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

### 5.3 Persistencia con systemd (`energiai-ml.service`)

Servicio configurado en `/etc/systemd/system/energiai-ml.service`:

```ini
[Unit]
Description=EnergIAi ML Service (FastAPI)
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/backend-python
ExecStart=/home/ubuntu/backend-python/venv/bin/uvicorn app.main:app --host 0.0.0.0 --port 8000
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
```

Comandos de gestión:

```Bash
sudo systemctl daemon-reload
sudo systemctl enable energiai-ml
sudo systemctl start energiai-ml
sudo systemctl status energiai-ml
```

### 5.4 Hardening Local con iptables

Para asegurar que iptables no bloquee el tráfico interno de la VCN pero sí descarte llamadas externas al puerto 8000:

```bash
sudo iptables -I INPUT 5 -p tcp -s 10.0.0.0/24 --dport 8000 -j ACCEPT
sudo apt install -y iptables-persistent
```

## 6. VM Java — Despliegue de Spring Boot (Backend Core)

### 6.1 Instalación del Runtime

```Bash
sudo apt update
sudo apt install -y openjdk-21-jdk
```

### 6.2 Regla de iptables para Tráfico Público (Puerto 8080)

```Bash
sudo iptables -I INPUT 5 -p tcp --dport 8080 -j ACCEPT
sudo netfilter-persistent save
```

### 6.3 Persistencia con systemd (energiai-backend.service)
Servicio configurado en /etc/systemd/system/energiai-backend.service:

```Ini, TOML
[Unit]
Description=EnergIAi Core Backend Service (Java Spring Boot)
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/backend-java
ExecStart=/usr/bin/java -jar target/energiai-api-0.0.1-SNAPSHOT.jar
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
```

## 7. Alineación y Verificación End-to-End
Chequeo Interno desde VM Java hacia Microservicio Python:

```Bash
curl [http://10.0.0.164:8000/health](http://10.0.0.164:8000/health)
# Respuesta esperada: {"status":"ok","modelo_cargado":true}
```

Chequeo Externo hacia Microservicio Python (Prueba de Seguridad):

```Bash
curl [http://147.15.16.156:8000/health](http://147.15.16.156:8000/health)
# Respuesta esperada: Connection refused / Timeout (Bloqueado por firewall)
```

Prueba End-to-End desde Navegador / Postman:

```Bash
curl [http://163.176.43.143:8080/api/v1/health](http://163.176.43.143:8080/api/v1/health)
# Respuesta esperada: {"status":"UP"}
```

---

## 👥 Atribución de Roles y Equipo

* **[Héctor Pablo Graff (P4154N0)](https://www.linkedin.com/in/hector-pablo-graff/):** **Arquitecto de Infraestructura Cloud & DevOps**
  * Diseño de topología de red en Oracle Cloud Infrastructure (OCI).
  * Configuración de VCN, Subredes, Internet Gateways y Security Lists.
  * Hardening de seguridad a nivel SO con `iptables` y aislamiento de microservicios.
  * Despliegue de servicios persistentes `systemd` para Java 21 y Python/FastAPI.

---

⬅️ Volver al [README principal del proyecto](../README.md)

---

Developed 💻 with ❤️ by **[P4154N0](https://www.linkedin.com/in/hector-pablo-graff/)** from 🇦🇷 who takes 🧉 and ❤️ country music 🤠 🇨🇦