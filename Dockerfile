FROM python:3.10-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app.py .

# Give the container access to the Docker socket
VOLUME /var/run/docker.sock

CMD ["python", "app.py"]