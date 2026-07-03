# Busca Talento — Prácticas Profesionales

## Objetivo
Que los doctores busquen y contacten practicantes de forma activa, tipo LinkedIn Recruiter, sin que el practicante tenga que postular.

---

## Base de datos

### Nueva tabla `invitaciones_practicante`

| Columna | Tipo |
|---------|------|
| id_invitacion | INT PK AUTO_INCREMENT |
| id_doctor | INT FK → doctores |
| id_practicante | INT FK → practicantes |
| mensaje | TEXT (opcional) |
| estado | ENUM('PENDIENTE','ACEPTADA','RECHAZADA') |
| fecha_creacion | TIMESTAMP |
| fecha_respuesta | TIMESTAMP NULL |

---

## Backend — 6 endpoints nuevos

| Método | Endpoint | Rol | Descripción |
|--------|----------|-----|-------------|
| `GET` | `/api/perfil/practicantes?habilidad=X&universidad=Y` | DOCTOR | Buscar con filtros (ya existe, se mejora) |
| `POST` | `/api/practicante/invitaciones` | DOCTOR | Enviar invitación |
| `GET` | `/api/practicante/invitaciones/doctor` | DOCTOR | Mis invitaciones enviadas |
| `GET` | `/api/practicante/mis-invitaciones` | PRACTICANTE | Invitaciones recibidas |
| `PUT` | `/api/practicante/invitaciones/{id}/aceptar` | PRACTICANTE | Aceptar → auto-asigna en `supervision_practicantes` |
| `PUT` | `/api/practicante/invitaciones/{id}/rechazar` | PRACTICANTE | Rechazar |

### Notificaciones WebSocket
- Al invitar → notifica al practicante
- Al aceptar → notifica al doctor

---

## Frontend — 3 páginas

### 1. Ver Practicantes (rediseñado) — doctores
- Filtros: habilidades, universidad, certificaciones
- Cards con foto, nombre, skills, universidad
- Botón **"Invitar"** → modal con mensaje opcional
- Si ya invitado → badge "Pendiente" / "Aceptado"

### 2. Mis Invitaciones — practicantes (NUEVO)
- Lista de invitaciones recibidas (doctor, fecha, mensaje)
- Botones **Aceptar** / **Rechazar**
- Badge de pendientes en el sidebar

### 3. Invitaciones Enviadas — doctores (NUEVO)
- Lista con estado de cada invitación
- Filtro: pendientes, aceptadas, rechazadas

---

## Sidebar

| Rol | Nuevo ítem | Badge |
|-----|-----------|-------|
| PRACTICANTE | "Mis Invitaciones" | contador de pendientes |
| DOCTOR | "Ver Practicantes" (ya existe) | — |
| DOCTOR | "Invitaciones Enviadas" | — |

---

## Flujo completo

```
1. Doctor va al sidebar → "Ver Practicantes"
2. Busca/filtra por habilidades, universidad, certificaciones
3. Ve card con info del practicante → click "Invitar"
4. Modal: escribe mensaje y envía
5. Practicante recibe notificación WebSocket (campanita + badge en sidebar)
6. Va a "Mis Invitaciones" → ve la propuesta
7. Acepta → se crea automáticamente el vínculo en supervision_practicantes
8. Doctor recibe notificación: "Dominid Muñoz aceptó tu invitación"
```
