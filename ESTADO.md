# Clínica UPN — Sistema de Gestión Integral

Sistema de gestión clínica universitaria con roles diferenciados: Paciente, Doctor/Médico, Practicante, Administrativo, Administrador y Director.

---

## Actores del Sistema

| Actor | Grupo de Permisos | Descripción |
|-------|-------------------|-------------|
| **Paciente** | `GP_PACIENTE` | Usuario final que recibe atención médica |
| **Doctor / Médico** | `GP_MEDICO` | Profesional de salud que atiende consultas |
| **Practicante** | `GP_PRACTICANTE` | Estudiante en formación que registra consultas supervisadas |
| **Administrativo** | `GP_ADMIN_OP` | Personal operativo que gestiona citas, pacientes y consultorios |
| **Administrador** | `GP_ADMIN_SYS` | Encargado de configuración del sistema, usuarios y roles |
| **Director** | `GP_DIRECTOR` | Directivo que visualiza dashboards, KPIs y reportes estratégicos |

---

## Casos de Uso de Negocio (CUN) por Actor

### PACIENTE

| CUN | Nombre | CUS Asociados | Estado |
|-----|--------|---------------|--------|
| CUN_01 | Agendar cita médica | CUS_01, CUS_02, CUS_03 | ✅ |
| CUN_02 | Cancelar cita médica | CUS_04 | ✅ |
| CUN_03 | Reprogramar cita médica | CUS_05 | ✅ |
| CUN_04 | Acceder a historia clínica electrónica | CUS_06, CUS_07 | ✅ (lectura) |
| CUN_05 | Solicitar teleconsulta | CUS_08, CUS_09 | ❌ |
| CUN_06 | Actualizar datos personales | CUS_10, CUS_11 | ✅ |
| CUN_07 | Recibir notificaciones y recordatorios | CUS_12, CUS_13 | ⚠️ (solo email confirmación) |

### DOCTOR / MÉDICO

| CUN | Nombre | CUS Asociados | Estado |
|-----|--------|---------------|--------|
| CUN_08 | Registrar diagnóstico y tratamiento | CUS_14, CUS_15 | ✅ |
| CUN_09 | Gestionar agenda y disponibilidad | CUS_16, CUS_17 | ✅ (agenda) / ❌ (disponibilidad) |
| CUN_10 | Conducir teleconsulta | CUS_18, CUS_19 | ❌ |
| CUN_11 | Prescribir medicamentos | CUS_20 | ✅ |
| CUN_12 | Solicitar exámenes médicos | CUS_21, CUS_22 | ❌ |
| CUN_13 | Supervisar practicante | CUS_23, CUS_24 | ❌ |

### PRACTICANTE

| CUN | Nombre | CUS Asociados | Estado |
|-----|--------|---------------|--------|
| CUN_14 | Registrar consulta bajo supervisión | CUS_25, CUS_26 | ❌ (backend vacío) |
| CUN_15 | Consultar HCE del paciente asignado | CUS_27, CUS_28 | ❌ |
| CUN_16 | Ver evaluaciones de desempeño | CUS_29 | ❌ |
| CUN_17 | Acceder a agenda de actividades | CUS_30, CUS_31 | ❌ |

### ADMINISTRATIVO

| CUN | Nombre | CUS Asociados | Estado |
|-----|--------|---------------|--------|
| CUN_18 | Registrar y actualizar datos de paciente | CUS_32, CUS_33 | ✅ |
| CUN_19 | Gestionar citas operativas | CUS_34, CUS_35, CUS_36 | ❌ |
| CUN_20 | Asignar consultorio y doctor | CUS_37, CUS_38 | ❌ |
| CUN_21 | Emitir reporte operativo | CUS_39, CUS_40 | ❌ |

### ADMINISTRADOR

| CUN | Nombre | CUS Asociados | Estado |
|-----|--------|---------------|--------|
| CUN_22 | Gestionar usuarios y roles | CUS_41, CUS_42, CUS_43, CUS_44 | ✅ |
| CUN_23 | Configurar especialidades y horarios | CUS_45, CUS_46 | ❌ |
| CUN_24 | Gestionar módulos del sistema | CUS_47, CUS_48 | ❌ |
| CUN_25 | Monitorizar logs de actividad | CUS_49, CUS_50 | ❌ |

### DIRECTOR

| CUN | Nombre | CUS Asociados | Estado |
|-----|--------|---------------|--------|
| CUN_26 | Visualizar dashboard analítico | CUS_51, CUS_52 | ❌ |
| CUN_27 | Acceder a reportes de tendencias | CUS_53, CUS_54 | ❌ |
| CUN_28 | Revisar indicadores de rendimiento | CUS_55, CUS_56 | ❌ |

---

## Casos de Uso de Sistema (CUS) — Detalle

### PACIENTE

| CUS | Nombre | RF | Endpoint | Frontend | Estado |
|-----|--------|----|----------|----------|--------|
| **CUS_01** | Buscar disponibilidad de citas | RF-01.1, RF-01.6 | `GET /api/cita-publica/doctores/{especialidad}` | Landing page | ✅ |
| **CUS_02** | Seleccionar especialidad y doctor | RF-01.2 | `GET /api/cita-publica/doctores/{especialidad}` | Landing page | ✅ |
| **CUS_03** | Confirmar agendado de cita | RF-01.3, RF-05.1 | `POST /api/cita-publica/agendar` | Landing page | ✅ |
| **CUS_04** | Cancelar cita médica | RF-01.4, RF-05.3 | `PUT /api/citas/{id}/cancelar` | Mis Citas | ✅ |
| **CUS_05** | Reprogramar cita médica | RF-01.5, RF-05.3 | `PUT /api/citas/{id}/reprogramar` | Mis Citas | ✅ |
| **CUS_06** | Consultar historia clínica | RF-02.3 | `GET /api/hce/documentos` | Mi Historia | ✅ |
| **CUS_07** | Descargar documentos HCE | RF-02.4 | — | — | ❌ |
| **CUS_08** | Solicitar teleconsulta | RF-03.1 | — | — | ❌ |
| **CUS_09** | Unirse a videollamada | RF-03.2 | — | — | ❌ |
| **CUS_10** | Actualizar datos personales | RF-06.3 | `PUT /api/pacientes/{id}` | Mi Perfil | ✅ |
| **CUS_11** | Actualizar datos de contacto | RF-06.3 | `PUT /api/pacientes/{id}` | Mi Perfil | ✅ |
| **CUS_12** | Recibir notificación de cita | RF-05.1 | EmailService (async) | — | ✅ |
| **CUS_13** | Recibir recordatorio de cita | RF-05.2 | — | — | ❌ |

### DOCTOR / MÉDICO

| CUS | Nombre | RF | Endpoint | Frontend | Estado |
|-----|--------|----|----------|----------|--------|
| **CUS_14** | Registrar diagnóstico del paciente | RF-02.1, RF-02.6 | `PUT /api/consultas/{id}/diagnostico` | Atención | ✅ **NUEVO** |
| **CUS_15** | Registrar tratamiento del paciente | RF-02.1, RF-02.6 | `PUT /api/consultas/{id}/tratamiento` | Atención | ✅ **NUEVO** |
| **CUS_16** | Ver agenda de consultas del día | RF-07.1 | `GET /api/consultas/agenda` | Agenda | ✅ |
| **CUS_17** | Configurar disponibilidad horaria | RF-07.2, RF-07.3 | — | — | ❌ |
| **CUS_18** | Iniciar teleconsulta con paciente | RF-03.3 | — | — | ❌ |
| **CUS_19** | Registrar notas durante teleconsulta | RF-03.4, RF-03.5 | — | — | ❌ |
| **CUS_20** | Prescribir medicamentos | RF-04.1, RF-04.4 | `PUT /api/consultas/{id}/prescripcion` | Atención | ✅ **NUEVO** |
| **CUS_21** | Solicitar exámenes de laboratorio | RF-04.2, RF-04.4 | — | — | ❌ |
| **CUS_22** | Solicitar exámenes de imagen | RF-04.3, RF-04.4 | — | — | ❌ |
| **CUS_23** | Registrar evaluación de practicante | RF-08.3 | — | — | ❌ |
| **CUS_24** | Ver historial evaluaciones practicante | RF-08.4 | — | — | ❌ |

---

## Endpoints del Backend

### Públicos (sin autenticación)

| Método | Endpoint | CUS |
|--------|----------|-----|
| `GET` | `/api/cita-publica/doctores/{especialidad}` | CUS_01, CUS_02 |
| `GET` | `/api/cita-publica/buscar-paciente?email=&codigo=` | CUS_03 |
| `POST` | `/api/cita-publica/agendar` | CUS_03 |
| `POST` | `/api/auth/login` | — |

### PACIENTE (requiere token con rol PACIENTE)

| Método | Endpoint | CUS |
|--------|----------|-----|
| `GET` | `/api/citas/mis-citas` | CUS_06 |
| `PUT` | `/api/citas/{id}/cancelar` | CUS_04 |
| `PUT` | `/api/citas/{id}/reprogramar` | CUS_05 |
| `GET` | `/api/hce/documentos` | CUS_06 |
| `PUT` | `/api/pacientes/{id}` | CUS_10, CUS_11 |

### DOCTOR / PRACTICANTE (requiere token con rol DOCTOR, PRACTICANTE, ADMINISTRADOR o DIRECTOR)

| Método | Endpoint | CUS |
|--------|----------|-----|
| `GET` | `/api/consultas/agenda?fecha=&idDoctor=` | CUS_16 |
| `GET` | `/api/consultas/doctores` | CUS_16 (filtro) |
| `POST` | `/api/consultas` | CUS_14 |
| `GET` | `/api/consultas/{id}` | CUS_14-20 |
| `PUT` | `/api/consultas/{id}/diagnostico` | CUS_14 |
| `PUT` | `/api/consultas/{id}/tratamiento` | CUS_15 |
| `PUT` | `/api/consultas/{id}/prescripcion` | CUS_20 |
| `GET` | `/api/consultas/paciente/{idPaciente}` | — |

### ADMINISTRADOR (requiere token con rol ADMINISTRADOR)

| Método | Endpoint | CUS |
|--------|----------|-----|
| `GET` | `/api/admin/usuarios?page=&size=` | CUS_41, CUS_44 |
| `PATCH` | `/api/admin/usuarios/{id}/rol` | CUS_44 |

---

## Stack Tecnológico

- **Backend**: Java 21, Spring Boot 3.3.5, Spring Security, JDBC puro, MySQL
- **Frontend**: Angular 17+, Signals, Standalone Components
- **Base de datos**: MySQL con `db_clinica_upn`
- **Autenticación**: JWT con BCrypt
- **Documentación API**: Swagger UI (OpenAPI 3) en `/swagger-ui.html`

---

## Base de Datos — Tablas

| Tabla | Propósito | Estado |
|-------|-----------|--------|
| `usuarios` | Usuarios del sistema (todos los roles) | ✅ |
| `pacientes` | Datos específicos de pacientes (FK a usuarios) | ✅ |
| `doctores` | Datos específicos de doctores (FK a usuarios) | ✅ |
| `citas` | Citas médicas agendadas | ✅ |
| `historias_clinicas` | Enlace paciente → HCE | ✅ |
| `consultas` | Registro de diagnósticos, tratamientos y prescripciones | ✅ **NUEVA** |
| `vista_historial_paciente` | Vista SQL que consolida HCE del paciente | ✅ (externa) |

---

## Flujo de Atención Médica (Doctor)

```
Agenda (CUS_16) 
    ↓  Click "Atender"
Iniciar Consulta (POST /api/consultas) → Cita pasa a ATENDIDA
    ↓
Registrar Diagnóstico (CUS_14) → código CIE-10 + descripción
    ↓
Registrar Tratamiento (CUS_15) → indicaciones
    ↓
Prescribir Medicamentos (CUS_20) → receta digital
    ↓
Atención Completada → Datos en tabla `consultas`
```

---

## Estado de Implementación — Próximos Pasos

### Prioridad 1 — MUST HAVE (pendientes)
| CUS | Nombre | Actor |
|-----|--------|-------|
| CUS_34 | Confirmar cita médica (admin) | Administrativo |
| CUS_37 | Asignar consultorio a doctor | Administrativo |
| CUS_45 | Configurar especialidades médicas | Administrador |
| CUS_46 | Configurar horarios de atención | Administrador |

### Prioridad 2 — PRACTICANTE (backend vacío)
| CUS | Nombre |
|-----|--------|
| CUS_25 | Registrar consulta bajo supervisión |
| CUS_26 | Enviar consulta a revisión del doctor |
| CUS_27 | Buscar HCE de paciente asignado |
| CUS_28 | Consultar detalle de HCE |
| CUS_29 | Ver evaluaciones recibidas |
| CUS_30 | Ver agenda de actividades clínicas |
| CUS_31 | Ver detalle de actividad clínica |

### Prioridad 3 — TELECONSULTA
| CUS | Nombre |
|-----|--------|
| CUS_08 | Solicitar teleconsulta |
| CUS_09 | Unirse a videollamada |
| CUS_18 | Iniciar teleconsulta |
| CUS_19 | Registrar notas durante teleconsulta |

### Prioridad 4 — REPORTES Y DASHBOARDS
| CUS | Nombre |
|-----|--------|
| CUS_39-40 | Reportes operativos |
| CUS_51-56 | Dashboards y KPIs del Director |

---

## Archivos del Proyecto (Backend)

```
backend/src/main/java/edu/upn/clinica/backend/
├── auth/           → Login, JWT, Usuario, UsuarioRepository, UsuarioController
├── cita/           → Citas, Agenda (controller/service/repository/model/dto)
├── consulta/       → Consultas médicas, Diagnóstico, Tratamiento, Prescripción
├── config/         → Seguridad, CORS, Swagger, Constantes
├── doctor/         → Doctores, disponibilidad
├── hce/            → Historia Clínica Electrónica
├── paciente/       → Pacientes CRUD
├── security/       → JwtFilter, JwtUtil, SecurityConfig
└── shared/         → BaseRepository, ApiResponse, AppException, PageResult, EmailService
```