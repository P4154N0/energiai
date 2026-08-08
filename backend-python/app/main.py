import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI, HTTPException, status
from fastapi.responses import JSONResponse

from app.model import ModeloNoDisponibleError, modelo
from app.schemas import HealthResponse, PrediccionRequest, PrediccionResponse

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("energiai.api")


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Cargamos el modelo una sola vez al arrancar el servicio, no en cada request.
    try:
        modelo.cargar()
    except ModeloNoDisponibleError as e:
        # No tiramos abajo el proceso: dejamos que /health lo reporte como no disponible,
        # así Java puede activar el mock-fallback en vez de que la VM Python ni siquiera levante.
        logger.error("No se pudo cargar el modelo al iniciar: %s", e)
    yield


app = FastAPI(
    title="EnergIAi - ML Service",
    description="Servicio interno de clasificación de perfil energético. "
                 "Solo debe ser accesible desde la VM Java, dentro de la misma VCN de OCI.",
    version="1.0.0",
    lifespan=lifespan,
)


@app.get("/health", response_model=HealthResponse, tags=["health"])
def health():
    return HealthResponse(status="ok", modelo_cargado=modelo.esta_cargado)


@app.post("/predict", response_model=PrediccionResponse, tags=["prediccion"])
def predict(request: PrediccionRequest):
    if not modelo.esta_cargado:
        # 503 para que el RestClient/MlModelClientImpl de Java dispare el mock-fallback.
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="El modelo de ML no está disponible en este momento",
        )

    try:
        categoria, probabilidad, probabilidades = modelo.predecir(request)
    except ModeloNoDisponibleError as e:
        raise HTTPException(status_code=status.HTTP_503_SERVICE_UNAVAILABLE, detail=str(e))
    except Exception as e:
        logger.exception("Error inesperado al predecir")
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Error interno al predecir")

    return PrediccionResponse(
        categoria=categoria,
        probabilidad=round(probabilidad, 4),
        probabilidades={k: round(v, 4) for k, v in probabilidades.items()},
    )


@app.exception_handler(Exception)
async def unhandled_exception_handler(request, exc):
    logger.exception("Error no manejado")
    return JSONResponse(status_code=500, content={"detail": "Error interno del servidor"})
