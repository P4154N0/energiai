FROM python:3.12-slim

WORKDIR /srv

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app ./app
COPY model ./model

EXPOSE 8000

# En OCI esta VM es privada; solo la VM Java le habla, así que 0.0.0.0
# es seguro dentro de la VCN (las Security Lists filtran el acceso externo).
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
