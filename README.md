# Livekit Demo App

This repository contains a simple demo setup to run and test LiveKit locally.

### Repository Structure

| Folder | Description |
|--------|-------------|
| `livekit-local` | Docker compose setup to run LiveKit server locally |
| `java` | Spring Boot backend (generates tokens + API) |
| `openvidu-vue` | Vue front-end client (UI + connect to LiveKit) |

---

## How to Run

### 1) Start LiveKit Server (Docker)

Go into the `livekit-local` folder and run:

```bash
docker compose up -d
```
###### This will start LiveKit server locally using Docker.

---

### 2) Run Backend (Java)

Go into the `java` folder and run:
```bash
mvn spring-boot:run
```
###### This starts the Spring Boot server and exposes APIs to generate LiveKit tokens.

---

### 3) Run Frontend (Vue)

Go into the `openvidu-vue` folder and run:
```bash
npm install
npm run start
```
###### This starts the front-end UI (Vite local dev server).

---

## Order to run:

 1 - `docker compose up -d`

2 - `mvn spring-boot:run`

3 - `npm run start`

---

## Reference

[LiveKit tutorials and documentation](https://livekit-tutorials.openvidu.io/)
