# ODA Config Service
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/OpenDonationAssistant/oda-config-service)
![Sonar Tech Debt](https://img.shields.io/sonar/tech_debt/OpenDonationAssistant_oda-config-service?server=https%3A%2F%2Fsonarcloud.io)
![Sonar Violations](https://img.shields.io/sonar/violations/OpenDonationAssistant_oda-config-service?server=https%3A%2F%2Fsonarcloud.io)
![Sonar Tests](https://img.shields.io/sonar/tests/OpenDonationAssistant_oda-config-service?server=https%3A%2F%2Fsonarcloud.io)
![Sonar Coverage](https://img.shields.io/sonar/coverage/OpenDonationAssistant_oda-config-service?server=https%3A%2F%2Fsonarcloud.io)

## Running with Docker

### Pull the Docker Image

```bash
docker pull ghcr.io/opendonationassistant/oda-config-service:latest
```

### Required Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `RABBITMQ_HOST` | RabbitMQ host address | `localhost` |
| `JDBC_URL` | PostgreSQL JDBC connection URL | `jdbc:postgresql://localhost/postgres?currentSchema=config` |
| `JDBC_USER` | Database username | `postgres` |
| `JDBC_PASSWORD` | Database password | `postgres` |

### Running the Container

```bash
docker run -d \
  --name oda-config-service \
  -e RABBITMQ_HOST=your-rabbitmq-host \
  -e JDBC_URL=jdbc:postgresql://your-postgres-host:5432/postgres?currentSchema=config \
  -e JDBC_USER=your-db-user \
  -e JDBC_PASSWORD=your-db-password \
  -p 8080:8080 \
  ghcr.io/opendonationassistant/oda-config-service:latest
```

### Using Docker Compose

Create a `docker-compose.yml` file:

```yaml
version: '3.8'

services:
  oda-config-service:
    image: ghcr.io/opendonationassistant/oda-config-service:latest
    environment:
      - RABBITMQ_HOST=rabbitmq
      - JDBC_URL=jdbc:postgresql://postgres:5432/postgres?currentSchema=config
      - JDBC_USER=postgres
      - JDBC_PASSWORD=postgres
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - rabbitmq
```
