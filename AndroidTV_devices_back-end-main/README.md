# AndroidTV Devices Backend (Go + Fiber + SQLite)

This is a minimal backend service written in Go.
It stores devices in a SQLite database and exposes a small REST API.

The project is intentionally kept simple and contained in a single `main.go` file for educational purposes.

---

## Features

* List all devices
* Create a new device (always starts as inactive)
* Toggle device active / inactive
* If a device is set to active, all other devices of the same type automatically become inactive

### Supported Device Types

* CAMERA
* MICROPHONE
* MOUSE
* KEYBOARD

---

## Requirements

* Go 1.21+ (tested with 1.23.1)

Download Go from:
[https://go.dev/dl/](https://go.dev/dl/)

Verify installation:

```bash
go version
```

---

## Project Setup

Open a terminal inside the project folder (where `main.go` is located).

### 1. Install Dependencies

```bash

go mod tidy
```

### 2. Run the Server

```bash
go run .
```

---

## Default Behavior

Server runs at:

```
http://localhost:8080
```

SQLite database file will be created automatically in the same folder:

```
./devices.db
```

---

## Optional Configuration

You can override default settings using environment variables.

* `ADDR` — server address (default `:8080`)
* `DB_PATH` — SQLite database file path (default `./devices.db`)

Example:

```bash
ADDR=:3000 DB_PATH=./data.db go run .
```

---

## API Endpoints

### Health Check

```
GET /health
```

Returns:

```json
{
  "ok": true
}
```

---

### List All Devices

```
GET /devices
```

Response example:

```json
[
  {
    "id": 1,
    "name": "Camera 1",
    "type": "CAMERA",
    "active": false,
    "createdAt": "2026-01-01T10:00:00Z",
    "updatedAt": "2026-01-01T10:00:00Z"
  }
]
```

---

### Create Device

```
POST /devices
```

Request body:

```json
{
  "name": "Camera 1",
  "type": "CAMERA"
}
```

Notes:

* `type` must be one of: CAMERA, MICROPHONE, MOUSE, KEYBOARD
* New devices are always created with `active = false`

---

### Set Device Active / Inactive

```
PATCH /devices/{id}/active
```

Request body:

```json
{
  "active": true
}
```

Behavior:

* If `active = true`, all other devices of the same type become inactive automatically.
* If `active = false`, only that device becomes inactive.

---

## Curl Examples

Create a device:

```bash
curl -X POST http://localhost:8080/devices \
  -H "Content-Type: application/json" \
  -d '{"name":"Camera 1","type":"CAMERA"}'
```

List devices:

```bash
curl http://localhost:8080/devices
```

Activate device with ID 1:

```bash
curl -X PATCH http://localhost:8080/devices/1/active \
  -H "Content-Type: application/json" \
  -d '{"active":true}'
```

---

## Technical Notes

* SQLite is used for simplicity and zero external dependencies.
* WAL mode is enabled for better concurrent performance.
* Transactions use `BEGIN IMMEDIATE` to ensure only one device per type can be active at a time.
* Device type is validated at both API level and database level (CHECK constraint).
* The project is intentionally kept in a single file for learning purposes.

---

## Folder Structure

```
project-folder/
 ├── main.go
 ├── go.mod
 ├── go.sum
 ├── devices.db (auto-created)
 └── README.md
```

---

## License

Educational project. Use freely for learning purposes.
