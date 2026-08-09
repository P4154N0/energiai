import http from 'k6/http';
import { sleep, check } from 'k6';

// 1. Configuración de la carga (Escenario de prueba inicial)
export const options = {
  stages: [
    { duration: '10s', target: 5 },  // Subimos gradualmente a 5 usuarios virtuales en 10s
    { duration: '20s', target: 5 },  // Mantenemos 5 usuarios por 20s
    { duration: '5s',  target: 0 },  // Bajamos a 0 usuarios
  ],
};

// 2. Ejecución de la prueba
export default function () {
  // Ruta exacta a tu API Java en OCI
  const url = 'http://163.176.43.143:8080/analisis-energetico'; 

  // JSON con la estructura real de tu dominio
  const payload = JSON.stringify({
    householdSize: 1,
    hasAc: 1,
    homeOffice: false,
    housingType: "CASA",
    equipmentCount: 2,
    consumoTotalMesAnterior: 920,
    peakUsageLevel: "HIGH"
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    },
  };

  // Petición POST a la API
  const res = http.post(url, payload, params);

  // Validación de respuesta exitosa (200 OK / 201 Created)
  check(res, {
    'Status 200/201 OK': (r) => r.status === 200 || r.status === 201,
  });

  sleep(1);
}