# Mini Job Queue

A distributed job processing system built with Spring Boot, RabbitMQ and PostgreSQL.

The project allows users to create, schedule, prioritize, process, retry, monitor and track jobs using a message-queue-based architecture.

---

## Tech Stack

- Java 21
- Spring Boot
- RabbitMQ
- PostgreSQL
- JPA / Hibernate
- Docker & Docker Compose
- Prometheus
- Grafana
- Maven

---

## Features

- **Job Management** — Create, view, and cancel jobs through REST APIs with complete job status tracking.
- **Job Scheduling** — Schedule jobs for future execution instead of processing them immediately.
- **Priority-based Scheduling** — Supports `HIGH`, `MEDIUM`, and `LOW` priority jobs using RabbitMQ message priorities.
- **Multiple Consumers** — Processes jobs concurrently using multiple RabbitMQ consumers for better throughput.
- **Retry & Exponential Backoff** — Automatically retries failed jobs using delayed retry queues with increasing delays of 1s, 2s, and 4s.
- **Dead Letter Queue** — Moves jobs to the DLQ after exceeding the maximum retry limit.
- **Job Timeout Handling** — Detects jobs that take longer than the configured processing limit and handles timed-out jobs.
- **Job Locking** — Prevents the same job from being processed concurrently by multiple consumers.
- **Job History** — Records important job status transitions throughout the job lifecycle.
- **Monitoring & Observability** — Uses Prometheus and Grafana to monitor jobs, retries, failures, queue activity, processing duration, and system metrics.
  
---

## Architecture

<img width="1312" height="1199" alt="Codex Image Sep 11, 2026, 04_35_56 PM" src="https://github.com/user-attachments/assets/1e2478ef-5a7b-4222-a708-12bb7f5d1baf" />

---

## Monitoring with Grafana

Grafana dashboards are used to visualize job-processing metrics collected by Prometheus.

<img width="959" height="359" alt="image" src="https://github.com/user-attachments/assets/c13fec79-b3bc-44dd-91f2-1f190f918b5a" />

<img width="959" height="457" alt="image" src="https://github.com/user-attachments/assets/242f777c-5f20-4a21-9484-fb6b4edba93b" />

<img width="926" height="161" alt="image" src="https://github.com/user-attachments/assets/7b8dcec4-c091-4c7b-bed5-869fc26bad6a" />

<img width="463" height="502" alt="image" src="https://github.com/user-attachments/assets/09d3358f-ade1-4f81-ac02-c1b4a643a4f0" />

---

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/RuchaSinkar/Mini-Job-Queue.git
cd Mini-Job-Queue
```

### 2. Build the application

```bash
mvn clean package -DskipTests
```

### 3. Build the Docker image

```bash
docker build -t mini-job-queue .
```

### 4. Start the services

```bash
docker compose up -d
```

This starts the required services such as:

- Mini Job Queue
- PostgreSQL
- RabbitMQ
- Prometheus
- Grafana

### 5. Check running containers

```bash
docker ps
```

### 6. Access the application

Application:

```text
http://localhost:8080
```

Prometheus:

```text
http://localhost:9090
```

Grafana:

```text
http://localhost:3000
```

---

## Future Improvements

- Authentication and authorization
- Better job scheduling using a dedicated scheduler
- More advanced retry policies
- Kubernetes deployment
- Distributed tracing
- More detailed monitoring and alerting

---

## License

This project is licensed under the MIT License.
