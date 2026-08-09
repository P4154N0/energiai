import http from 'k6/http';
import { check, sleep } from 'k6';

// 1. Configuracion de etapas de carga y umbrales de calidad
export const options = {
  stages: [
    { duration: '30s', target: 10 }, // Fase 1: Calentamiento (0 a 10 usuarios en 30 seg)
    { duration: '1m',  target: 30 }, // Fase 2: Carga sostenida (sube a 30 usuarios durante 1 min)
    { duration: '30s', target: 0  }, // Fase 3: Enfriamiento (baja de 30 a 0 usuarios en 30 seg)
  ],
  thresholds: {
    // El 95% de las peticiones deben responder en menos de 300 ms
    http_req_duration: ['p(95)<300'],
    // La tasa de errores de conexion o estado debe ser menor al 1%
    http_req_failed: ['rate<0.01'],
  },
};

// 2. Funcion principal que ejecuta cada Usuario Virtual (VU)
export default function () {
  // Endpoint de salud de la API Java en la VM de OCI
  const url = 'http://163.176.43.143:8080/api/v1/health';

  // Ejecucion de la peticion GET
  const response = http.get(url);

  // Validacion de que la API responde 200 OK
  check(response, {
    'status es 200 OK': (r) => r.status === 200,
  });

  // Pausa de 1 segundo entre iteraciones
  sleep(1);
}