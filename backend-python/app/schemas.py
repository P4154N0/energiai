from enum import Enum

from pydantic import BaseModel, Field


class HousingType(str, Enum):
    """Debe coincidir exactamente con el enum HousingType.java del backend."""
    CASA = "CASA"
    DEPARTAMENTO = "DEPARTAMENTO"
    MONOAMBIENTE = "MONOAMBIENTE"


class PeakUsageLevel(str, Enum):
    """Debe coincidir exactamente con el enum PeakUsageLevel.java del backend."""
    LOW = "LOW"
    MEDIUM = "MEDIUM"
    HIGH = "HIGH"


class PrediccionRequest(BaseModel):
    """
    Contrato de entrada que envía la API Java (MlModelClientImpl) a este
    servicio. avg_energy_consumption_kwh ya viene calculado por Java
    (consumoTotalMesAnterior / díasDelMesAnterior), no es responsabilidad
    de este servicio calcularlo.
    """
    household_size: int = Field(..., ge=1, le=20, description="Cantidad de personas en el hogar")
    has_ac: int = Field(..., ge=0, le=1, description="1 si tiene aire acondicionado, 0 si no")
    home_office: bool = Field(..., description="Si se realiza home office en la vivienda")
    housing_type: HousingType
    equipment_count: int = Field(..., ge=0, description="Cantidad de equipos eléctricos")
    avg_energy_consumption_kwh: float = Field(..., ge=0, description="Consumo promedio diario en kWh, ya calculado")
    peak_usage_level: PeakUsageLevel

    model_config = {
        "json_schema_extra": {
            "example": {
                "household_size": 4,
                "has_ac": 1,
                "home_office": True,
                "housing_type": "CASA",
                "equipment_count": 10,
                "avg_energy_consumption_kwh": 14.0,
                "peak_usage_level": "HIGH"
            }
        }
    }


class PrediccionResponse(BaseModel):
    categoria: str = Field(..., description="Eficiente | Moderado | Ineficiente")
    probabilidad: float = Field(..., description="Probabilidad de la clase predicha (0.0 a 1.0)")
    probabilidades: dict[str, float] = Field(..., description="Probabilidad de cada categoría posible")


class HealthResponse(BaseModel):
    status: str
    modelo_cargado: bool
