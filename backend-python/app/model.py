import logging
from pathlib import Path

import joblib
import pandas as pd

from app.schemas import HousingType, PeakUsageLevel, PrediccionRequest

logger = logging.getLogger("energiai.model")

MODEL_PATH = Path(__file__).resolve().parent.parent / "model" / "energy_profile_model.pkl"

# El modelo fue entrenado con estos valores exactos de string (no coinciden
# en formato con los enums de Java, que están en mayúsculas). Acá se traduce.
HOUSING_TYPE_MAP: dict[HousingType, str] = {
    HousingType.CASA: "Casa",
    HousingType.DEPARTAMENTO: "Departamento",
    HousingType.MONOAMBIENTE: "Monoambiente",
}

PEAK_USAGE_LEVEL_MAP: dict[PeakUsageLevel, str] = {
    PeakUsageLevel.LOW: "Low",
    PeakUsageLevel.MEDIUM: "Medium",
    PeakUsageLevel.HIGH: "High",
}

# Traducción de las clases que devuelve el modelo (inglés) a las categorías
# que expone el contrato de la API (español), ver README / consigna del hackathon.
CATEGORIA_MAP: dict[str, str] = {
    "Efficient": "Eficiente",
    "Moderate": "Moderado",
    "Inefficient": "Ineficiente",
}

FEATURE_COLUMNS = [
    "Household_Size",
    "Has_AC",
    "Home_Office",
    "Housing_Type",
    "Equipment_Count",
    "Avg_Energy_Consumption_kWh",
    "Peak_Usage_Level",
]


class ModeloNoDisponibleError(Exception):
    """Se lanza cuando el modelo no pudo cargarse al iniciar la aplicación."""


class EnergyProfileModel:
    def __init__(self, model_path: Path = MODEL_PATH):
        self._model_path = model_path
        self._pipeline = None

    def cargar(self) -> None:
        if not self._model_path.exists():
            raise ModeloNoDisponibleError(f"No se encontró el modelo en {self._model_path}")
        self._pipeline = joblib.load(self._model_path)
        logger.info("Modelo cargado desde %s", self._model_path)

    @property
    def esta_cargado(self) -> bool:
        return self._pipeline is not None

    def _a_dataframe(self, request: PrediccionRequest) -> pd.DataFrame:
        fila = {
            "Household_Size": request.household_size,
            "Has_AC": request.has_ac,
            "Home_Office": int(request.home_office),
            "Housing_Type": HOUSING_TYPE_MAP[request.housing_type],
            "Equipment_Count": request.equipment_count,
            "Avg_Energy_Consumption_kWh": request.avg_energy_consumption_kwh,
            "Peak_Usage_Level": PEAK_USAGE_LEVEL_MAP[request.peak_usage_level],
        }
        return pd.DataFrame([fila], columns=FEATURE_COLUMNS)

    def predecir(self, request: PrediccionRequest) -> tuple[str, float, dict[str, float]]:
        if not self.esta_cargado:
            raise ModeloNoDisponibleError("El modelo no está cargado")

        X = self._a_dataframe(request)

        clase_predicha = self._pipeline.predict(X)[0]
        probas = self._pipeline.predict_proba(X)[0]
        clases = self._pipeline.classes_

        probabilidades = {
            CATEGORIA_MAP.get(clase, clase): float(prob)
            for clase, prob in zip(clases, probas)
        }
        categoria = CATEGORIA_MAP.get(clase_predicha, clase_predicha)
        probabilidad = probabilidades[categoria]

        return categoria, probabilidad, probabilidades


# Instancia única (singleton) que se carga al iniciar la app (ver main.py)
modelo = EnergyProfileModel()
