# SISTEMA DE GESTIÓN INTEGRAL — CLÍNICA UPN

## PROPÓSITO

Sistema de gestión clínica universitaria que permite la administración de citas médicas, historias clínicas electrónicas, consultas, teleconsultas, reportes operativos y gestión académica de practicantes, con roles y permisos diferenciados para cada actor del sistema.

---

## ACTORES DEL SISTEMA

| Actor | Grupo de Permisos | Responsabilidad |
|-------|-------------------|-----------------|
| **Paciente** | `GP_PACIENTE` | Agendar, cancelar, reprogramar citas; consultar su HCE; actualizar datos personales; solicitar teleconsultas |
| **Doctor / Médico** | `GP_MEDICO` | Atender consultas; registrar diagnóstico, tratamiento y prescripciones; gestionar agenda; supervisar practicantes; conducir teleconsultas |
| **Practicante** | `GP_PRACTICANTE` | Registrar consultas supervisadas; consultar HCE de pacientes asignados; ver evaluaciones y agenda de actividades |
| **Administrativo** | `GP_ADMIN_OP` | Registrar/actualizar pacientes; gestionar citas (confirmar, cancelar, reprogramar); asignar consultorios; emitir reportes operativos |
| **Administrador** | `GP_ADMIN_SYS` | Gestionar usuarios y roles; configurar especialidades, horarios y módulos del sistema; monitorear logs |
| **Director** | `GP_DIRECTOR` | Visualizar dashboards con KPIs; acceder a reportes de tendencias; revisar indicadores de rendimiento de practicantes |

---

## CASOS DE USO DE NEGOCIO (CUN)

### PACIENTE

| ID | Nombre | Descripción |
|----|--------|-------------|
| CUN_01 | Agendar cita médica | El paciente busca disponibilidad, selecciona especialidad y doctor, y confirma el agendado de la cita |
| CUN_02 | Cancelar cita médica | El paciente cancela una cita previamente agendada |
| CUN_03 | Reprogramar cita médica | El paciente reprograma una cita conservando el historial del cambio |
| CUN_04 | Acceder a historia clínica electrónica | El paciente consulta y descarga documentos de su HCE |
| CUN_05 | Solicitar teleconsulta | El paciente solicita y se conecta a una teleconsulta |
| CUN_06 | Actualizar datos personales | El paciente actualiza su información personal y de contacto |
| CUN_07 | Recibir notificaciones y recordatorios | El paciente recibe notificaciones al confirmar cita y recordatorios antes de la fecha |

### DOCTOR / MÉDICO

| ID | Nombre | Descripción |
|----|--------|-------------|
| CUN_08 | Registrar diagnóstico y tratamiento | El médico registra el diagnóstico clínico (CIE-10) y el tratamiento indicado en la HCE del paciente |
| CUN_09 | Gestionar agenda y disponibilidad | El médico visualiza su agenda del día y configura su disponibilidad horaria |
| CUN_10 | Conducir teleconsulta | El médico inicia y registra notas durante una teleconsulta |
| CUN_11 | Prescribir medicamentos | El médico emite recetas médicas digitales asociadas a la consulta |
| CUN_12 | Solicitar exámenes médicos | El médico genera solicitudes de exámenes de laboratorio e imagen |
| CUN_13 | Supervisar practicante | El médico registra evaluaciones y consulta el historial de evaluaciones del practicante |

### PRACTICANTE

| ID | Nombre | Descripción |
|----|--------|-------------|
| CUN_14 | Registrar consulta bajo supervisión | El practicante registra consultas que son enviadas al médico para revisión y validación |
| CUN_15 | Consultar HCE del paciente asignado | El practicante busca y consulta la historia clínica de pacientes que tiene asignados |
| CUN_16 | Ver evaluaciones de desempeño | El practicante visualiza las evaluaciones recibidas de su médico supervisor |
| CUN_17 | Acceder a agenda de actividades | El practicante accede a su agenda de actividades clínicas programadas y al detalle de cada una |

### ADMINISTRATIVO

| ID | Nombre | Descripción |
|----|--------|-------------|
| CUN_18 | Registrar y actualizar datos de paciente | El administrativo registra nuevos pacientes y actualiza datos de pacientes existentes |
| CUN_19 | Gestionar citas operativas | El administrativo confirma, cancela y reprograma citas médicas |
| CUN_20 | Asignar consultorio y doctor | El administrativo asigna y reasigna consultorios a médicos por especialidad y horario |
| CUN_21 | Emitir reporte operativo | El administrativo genera y exporta reportes operativos diarios de atención médica |

### ADMINISTRADOR

| ID | Nombre | Descripción |
|----|--------|-------------|
| CUN_22 | Gestionar usuarios y roles | El administrador crea, edita, desactiva usuarios y asigna roles |
| CUN_23 | Configurar especialidades y horarios | El administrador configura especialidades médicas y horarios de atención |
| CUN_24 | Gestionar módulos del sistema | El administrador activa y desactiva módulos funcionales del sistema |
| CUN_25 | Monitorizar logs de actividad | El administrador consulta logs de actividad y eventos de seguridad de todos los usuarios |

### DIRECTOR

| ID | Nombre | Descripción |
|----|--------|-------------|
| CUN_26 | Visualizar dashboard analítico | El director visualiza KPIs de atención médica en tiempo real con filtros por período y especialidad |
| CUN_27 | Acceder a reportes de tendencias | El director accede a reportes de enfermedades frecuentes y tendencias de atención |
| CUN_28 | Revisar indicadores de rendimiento | El director revisa y compara indicadores de rendimiento académico y clínico de practicantes |

---

## CASOS DE USO DE SISTEMA (CUS) — ESPECIFICACIONES

### Módulo PACIENTE

#### CUS_01 — Buscar disponibilidad de citas
- **Origen:** CUN_01
- **Actor:** Paciente (`GP_PACIENTE`)
- **Objetivo:** Buscar y visualizar disponibilidad de citas por especialidad, médico y fecha
- **RF-01.1:** Buscar disponibilidad por especialidad, doctor y fecha
- **RF-01.6:** Evitar cruces de horarios al agendar

#### CUS_02 — Seleccionar especialidad y doctor
- **Origen:** CUN_01
- **Actor:** Paciente (`GP_PACIENTE`)
- **Objetivo:** Seleccionar especialidad y médico antes de confirmar una cita
- **RF-01.2:** Seleccionar especialidad y doctor antes de confirmar

#### CUS_03 — Confirmar agendado de cita
- **Origen:** CUN_01
- **Actor:** Paciente (`GP_PACIENTE`)
- **Objetivo:** Registrar y confirmar el agendado notificando al paciente
- **RF-01.3:** Confirmar agendado y notificar al paciente
- **RF-05.1:** Enviar notificación automática al confirmar cita

#### CUS_04 — Cancelar cita médica
- **Origen:** CUN_02
- **Actor:** Paciente (`GP_PACIENTE`)
- **Objetivo:** Cancelar una cita previamente agendada
- **RF-01.4:** Cancelar cita por paciente o administrativo
- **RF-05.3:** Notificar al paciente ante cancelaciones

#### CUS_05 — Reprogramar cita médica
- **Origen:** CUN_03
- **Actor:** Paciente (`GP_PACIENTE`)
- **Objetivo:** Reprogramar una cita conservando el historial del cambio
- **RF-01.5:** Reprogramar conservando historial del cambio
- **RF-05.3:** Notificar al paciente ante reprogramaciones

#### CUS_06 — Consultar historia clínica electrónica
- **Origen:** CUN_04
- **Actor:** Paciente (`GP_PACIENTE`)
- **Objetivo:** Consultar la HCE del propio paciente
- **RF-02.3:** Permitir al paciente consultar su propia HCE

#### CUS_07 — Descargar documentos de la HCE
- **Origen:** CUN_04
- **Actor:** Paciente (`GP_PACIENTE`)
- **Objetivo:** Descargar documentos y reportes de la HCE
- **RF-02.4:** Permitir al paciente descargar documentos de su HCE

#### CUS_08 — Solicitar teleconsulta
- **Origen:** CUN_05
- **Actor:** Paciente (`GP_PACIENTE`)
- **Objetivo:** Solicitar una teleconsulta desde su perfil
- **RF-03.1:** Solicitar teleconsulta desde el perfil del paciente

#### CUS_09 — Unirse a videollamada de teleconsulta
- **Origen:** CUN_05
- **Actor:** Paciente (`GP_PACIENTE`)
- **Objetivo:** Conectarse a la videollamada en la fecha programada
- **RF-03.2:** Unirse a videollamada en fecha programada

#### CUS_10 — Actualizar datos personales
- **Origen:** CUN_06
- **Actor:** Paciente (`GP_PACIENTE`)
- **Objetivo:** Actualizar información personal desde su perfil
- **RF-06.3:** Actualizar datos personales desde el perfil

#### CUS_11 — Actualizar datos de contacto
- **Origen:** CUN_06
- **Actor:** Paciente (`GP_PACIENTE`)
- **Objetivo:** Actualizar teléfono, correo, dirección
- **RF-06.3:** Actualizar datos de contacto desde el perfil

#### CUS_12 — Recibir notificación de cita
- **Origen:** CUN_07
- **Actor:** Paciente (`GP_PACIENTE`)
- **Objetivo:** Recibir notificación automática al confirmar cita
- **RF-05.1:** Notificación automática al confirmar cita

#### CUS_13 — Recibir recordatorio de cita
- **Origen:** CUN_07
- **Actor:** Paciente (`GP_PACIENTE`)
- **Objetivo:** Recibir recordatorio automático antes de la fecha de cita
- **RF-05.2:** Recordatorio automático antes de la fecha

---

### Módulo DOCTOR / MÉDICO

#### CUS_14 — Registrar diagnóstico del paciente
- **Origen:** CUN_08
- **Actor:** Doctor/Médico (`GP_MEDICO`)
- **Objetivo:** Registrar el diagnóstico de una consulta en la HCE del paciente
- **Precondición:** Consulta médica en curso o registrada, HCE disponible
- **RF-02.1:** Registrar diagnósticos por consulta médica
- **RF-02.6:** Trazabilidad con fecha, hora y usuario responsable
- **Datos:** Código CIE-10, descripción del diagnóstico
- **Flujo:**
  1. Accede a la consulta del paciente → carga HCE
  2. Selecciona "Registrar diagnóstico"
  3. Ingresa código CIE-10 y descripción
  4. Confirma y guarda → almacena con timestamp y médico responsable
  5. Muestra confirmación

#### CUS_15 — Registrar tratamiento del paciente
- **Origen:** CUN_08
- **Actor:** Doctor/Médico (`GP_MEDICO`)
- **Objetivo:** Registrar el tratamiento indicado en la HCE, asociado al diagnóstico
- **Precondición:** Diagnóstico registrado previamente (CUS_14)
- **RF-02.1:** Registrar tratamientos por consulta médica
- **RF-02.6:** Trazabilidad con fecha, hora y usuario responsable
- **Flujo:**
  1. Accede a sección de tratamiento
  2. Ingresa tratamiento indicado, duración y observaciones
  3. Confirma y guarda → asociado al diagnóstico con trazabilidad
  4. Muestra confirmación

#### CUS_16 — Ver agenda de consultas del día
- **Origen:** CUN_09
- **Actor:** Doctor/Médico (`GP_MEDICO`)
- **Objetivo:** Visualizar agenda de consultas programadas para el día actual
- **Precondición:** Médico autenticado y activo, consultas programadas
- **RF-07.1:** Mostrar agenda de consultas del día
- **Datos mostrados:** Hora, paciente, tipo de consulta, estado
- **Flujo:**
  1. Accede al módulo de agenda → valida permisos, obtiene fecha actual
  2. Consulta citas programadas para el médico
  3. Muestra agenda ordenada por hora
  4. Permite filtrar por estado y ver detalle de cada cita

#### CUS_17 — Configurar disponibilidad horaria
- **Origen:** CUN_09
- **Actor:** Doctor/Médico (`GP_MEDICO`)
- **Objetivo:** Definir disponibilidad horaria por día y turno
- **RF-07.2:** Configurar disponibilidad por día y turno
- **RF-07.3:** Bloquear horarios no disponibles

#### CUS_18 — Iniciar teleconsulta con paciente
- **Origen:** CUN_10
- **Actor:** Doctor/Médico (`GP_MEDICO`)
- **Objetivo:** Iniciar sesión de teleconsulta con el paciente
- **RF-03.3:** Iniciar teleconsulta desde el módulo del médico

#### CUS_19 — Registrar notas durante teleconsulta
- **Origen:** CUN_10
- **Actor:** Doctor/Médico (`GP_MEDICO`)
- **Objetivo:** Registrar notas clínicas durante la teleconsulta
- **RF-03.4:** Registrar notas clínicas durante teleconsulta
- **RF-03.5:** Registrar teleconsulta como parte de la HCE

#### CUS_20 — Prescribir medicamentos
- **Origen:** CUN_11
- **Actor:** Doctor/Médico (`GP_MEDICO`)
- **Objetivo:** Emitir recetas médicas digitales asociadas a una consulta
- **Precondición:** Consulta médica registrada o en curso, HCE disponible
- **RF-04.1:** Emitir recetas médicas digitales
- **RF-04.4:** Registrar prescripciones en la HCE
- **Datos:** Nombre del medicamento, dosis, frecuencia, duración
- **Flujo:**
  1. Accede a la consulta → carga HCE
  2. Selecciona "Prescribir medicamentos"
  3. Ingresa medicamento: nombre, dosis, frecuencia, duración
  4. Agrega más medicamentos si es necesario
  5. Confirma → genera receta digital con firma del médico
  6. Registra en HCE, muestra confirmación, habilita descarga/envío

#### CUS_21 — Solicitar exámenes de laboratorio
- **Origen:** CUN_12
- **Actor:** Doctor/Médico (`GP_MEDICO`)
- **Objetivo:** Generar solicitudes de exámenes de laboratorio
- **RF-04.2:** Solicitar exámenes de laboratorio desde la consulta
- **RF-04.4:** Registrar en la HCE del paciente

#### CUS_22 — Solicitar exámenes de imagen
- **Origen:** CUN_12
- **Actor:** Doctor/Médico (`GP_MEDICO`)
- **Objetivo:** Generar solicitudes de exámenes de imagen
- **RF-04.3:** Solicitar exámenes de imagen desde la consulta
- **RF-04.4:** Registrar en la HCE del paciente

#### CUS_23 — Registrar evaluación de practicante
- **Origen:** CUN_13
- **Actor:** Doctor/Médico (`GP_MEDICO`)
- **Objetivo:** Registrar evaluación de desempeño del practicante supervisado
- **RF-08.3:** Registrar evaluaciones de desempeño del practicante

#### CUS_24 — Ver historial de evaluaciones del practicante
- **Origen:** CUN_13
- **Actor:** Doctor/Médico (`GP_MEDICO`)
- **Objetivo:** Consultar el historial de evaluaciones de cada practicante
- **RF-08.4:** Consultar historial de evaluaciones del practicante

---

### Módulo PRACTICANTE

#### CUS_25 — Registrar consulta bajo supervisión
- **Origen:** CUN_14
- **Actor:** Practicante (`GP_PRACTICANTE`)
- **Objetivo:** Registrar consultas bajo supervisión del médico
- **RF-08.1:** Permitir al practicante registrar consultas supervisadas

#### CUS_26 — Enviar consulta a revisión del doctor
- **Origen:** CUN_14
- **Actor:** Practicante (`GP_PRACTICANTE`)
- **Objetivo:** Enviar el registro de consulta al médico para revisión y validación
- **RF-08.2:** Enviar consultas registradas a revisión del médico

#### CUS_27 — Buscar historia clínica de paciente asignado
- **Origen:** CUN_15
- **Actor:** Practicante (`GP_PRACTICANTE`)
- **Objetivo:** Buscar la HCE de pacientes asignados
- **RF-02.5:** Consultar HCE de pacientes asignados

#### CUS_28 — Consultar detalle de historia clínica
- **Origen:** CUN_15
- **Actor:** Practicante (`GP_PRACTICANTE`)
- **Objetivo:** Visualizar el detalle completo de la HCE de un paciente
- **RF-02.5:** Consultar detalle de HCE de pacientes asignados

#### CUS_29 — Ver evaluaciones recibidas
- **Origen:** CUN_16
- **Actor:** Practicante (`GP_PRACTICANTE`)
- **Objetivo:** Visualizar las evaluaciones de desempeño recibidas del médico supervisor
- **RF-08.5:** Ver evaluaciones recibidas

#### CUS_30 — Ver agenda de actividades clínicas
- **Origen:** CUN_17
- **Actor:** Practicante (`GP_PRACTICANTE`)
- **Objetivo:** Visualizar la agenda de actividades clínicas programadas
- **RF-08.6:** Acceder a agenda de actividades clínicas

#### CUS_31 — Ver detalle de actividad clínica
- **Origen:** CUN_17
- **Actor:** Practicante (`GP_PRACTICANTE`)
- **Objetivo:** Consultar el detalle de cada actividad clínica de su agenda
- **RF-08.6:** Acceder al detalle de actividades clínicas

---

### Módulo ADMINISTRATIVO

#### CUS_32 — Registrar nuevo paciente
- **Origen:** CUN_18
- **Actor:** Administrativo (`GP_ADMIN_OP`)
- **Objetivo:** Registrar un nuevo paciente en el sistema
- **RF-06.1:** Registrar nuevos pacientes

#### CUS_33 — Actualizar datos de paciente
- **Origen:** CUN_18
- **Actor:** Administrativo (`GP_ADMIN_OP`)
- **Objetivo:** Actualizar datos de un paciente ya registrado
- **RF-06.2:** Actualizar datos de pacientes registrados

#### CUS_34 — Confirmar cita médica
- **Origen:** CUN_19
- **Actor:** Administrativo (`GP_ADMIN_OP`)
- **Objetivo:** Confirmar citas médicas desde el módulo operativo
- **RF-01.7:** Gestionar confirmación, cancelación y reprogramación desde el módulo administrativo

#### CUS_35 — Cancelar cita por gestión administrativa
- **Origen:** CUN_19
- **Actor:** Administrativo (`GP_ADMIN_OP`)
- **Objetivo:** Cancelar citas ante imprevistos operativos
- **RF-01.4:** Cancelar por paciente o administrativo
- **RF-01.7:** Gestión administrativa de cancelaciones

#### CUS_36 — Reprogramar cita por gestión administrativa
- **Origen:** CUN_19
- **Actor:** Administrativo (`GP_ADMIN_OP`)
- **Objetivo:** Reprogramar citas ante cambios operativos
- **RF-01.5:** Reprogramar conservando historial
- **RF-01.7:** Gestión administrativa de reprogramaciones

#### CUS_37 — Asignar consultorio a doctor
- **Origen:** CUN_20
- **Actor:** Administrativo (`GP_ADMIN_OP`)
- **Objetivo:** Asignar un consultorio a un médico por especialidad y horario
- **RF-09.1:** Asignar consultorio por especialidad
- **RF-09.3:** Controlar que un consultorio no sea asignado a dos médicos en el mismo horario

#### CUS_38 — Reasignar consultorio
- **Origen:** CUN_20
- **Actor:** Administrativo (`GP_ADMIN_OP`)
- **Objetivo:** Reasignar consultorio ante cambios en la programación
- **RF-09.2:** Reasignar consultorio ante cambios operativos

#### CUS_39 — Generar reporte operativo diario
- **Origen:** CUN_21
- **Actor:** Administrativo (`GP_ADMIN_OP`)
- **Objetivo:** Generar automáticamente el reporte operativo diario
- **RF-10.1:** Generar reportes operativos diarios
- **RF-10.3:** Registrar atenciones automáticamente para alimentar reportes

#### CUS_40 — Exportar reporte operativo
- **Origen:** CUN_21
- **Actor:** Administrativo (`GP_ADMIN_OP`)
- **Objetivo:** Exportar reportes en formato descargable
- **RF-10.2:** Exportar reportes operativos descargables

---

### Módulo ADMINISTRADOR

#### CUS_41 — Crear usuario en el sistema
- **Origen:** CUN_22
- **Actor:** Administrador (`GP_ADMIN_SYS`)
- **Objetivo:** Crear nuevos usuarios en el sistema
- **RF-11.1:** Crear nuevos usuarios

#### CUS_42 — Editar usuario del sistema
- **Origen:** CUN_22
- **Actor:** Administrador (`GP_ADMIN_SYS`)
- **Objetivo:** Editar datos de un usuario existente
- **RF-11.2:** Editar datos de usuario existente

#### CUS_43 — Desactivar usuario del sistema
- **Origen:** CUN_22
- **Actor:** Administrador (`GP_ADMIN_SYS`)
- **Objetivo:** Desactivar usuarios sin eliminar su historial
- **RF-11.3:** Desactivar usuarios sin eliminar historial

#### CUS_44 — Asignar rol a usuario
- **Origen:** CUN_22
- **Actor:** Administrador (`GP_ADMIN_SYS`)
- **Objetivo:** Asignar y modificar roles a los usuarios
- **RF-11.4:** Asignar y modificar roles
- **RF-11.5:** Restringir acceso según rol del usuario autenticado

#### CUS_45 — Configurar especialidades médicas
- **Origen:** CUN_23
- **Actor:** Administrador (`GP_ADMIN_SYS`)
- **Objetivo:** Configurar especialidades médicas disponibles
- **RF-12.1:** Configurar especialidades médicas disponibles

#### CUS_46 — Configurar horarios de atención
- **Origen:** CUN_23
- **Actor:** Administrador (`GP_ADMIN_SYS`)
- **Objetivo:** Configurar horarios de atención por especialidad
- **RF-12.2:** Configurar horarios de atención por especialidad

#### CUS_47 — Activar módulo del sistema
- **Origen:** CUN_24
- **Actor:** Administrador (`GP_ADMIN_SYS`)
- **Objetivo:** Activar módulos funcionales del sistema
- **RF-12.3:** Activar y desactivar módulos del sistema

#### CUS_48 — Desactivar módulo del sistema
- **Origen:** CUN_24
- **Actor:** Administrador (`GP_ADMIN_SYS`)
- **Objetivo:** Desactivar módulos funcionales del sistema
- **RF-12.3:** Activar y desactivar módulos del sistema

#### CUS_49 — Consultar logs de actividad
- **Origen:** CUN_25
- **Actor:** Administrador (`GP_ADMIN_SYS`)
- **Objetivo:** Consultar y filtrar logs de actividad de usuarios
- **RF-13.1:** Logs de todas las actividades
- **RF-13.3:** Filtrar logs por usuario, fecha y tipo

#### CUS_50 — Consultar logs de seguridad
- **Origen:** CUN_25
- **Actor:** Administrador (`GP_ADMIN_SYS`)
- **Objetivo:** Consultar eventos de seguridad (accesos fallidos, sesiones)
- **RF-13.2:** Logs de eventos de seguridad
- **RF-13.3:** Filtrar logs de seguridad

---

### Módulo DIRECTOR

#### CUS_51 — Ver dashboard de KPIs de atención
- **Origen:** CUN_26
- **Actor:** Director (`GP_DIRECTOR`)
- **Objetivo:** Dashboard con indicadores clave de atención médica en tiempo real
- **RF-14.1:** Dashboard con KPIs en tiempo real

#### CUS_52 — Filtrar KPIs por período y especialidad
- **Origen:** CUN_26
- **Actor:** Director (`GP_DIRECTOR`)
- **Objetivo:** Filtrar indicadores del dashboard por período y especialidad
- **RF-14.2:** Filtrar dashboard por período y especialidad

#### CUS_53 — Ver reporte de enfermedades frecuentes
- **Origen:** CUN_27
- **Actor:** Director (`GP_DIRECTOR`)
- **Objetivo:** Reporte de enfermedades más frecuentes atendidas
- **RF-14.3:** Reportes de enfermedades frecuentes

#### CUS_54 — Ver tendencias de atención
- **Origen:** CUN_27
- **Actor:** Director (`GP_DIRECTOR`)
- **Objetivo:** Tendencias de atención para decisiones estratégicas
- **RF-14.3:** Tendencias de atención

#### CUS_55 — Ver indicadores de rendimiento de practicantes
- **Origen:** CUN_28
- **Actor:** Director (`GP_DIRECTOR`)
- **Objetivo:** Indicadores de rendimiento académico y clínico de practicantes
- **RF-14.4:** Indicadores de rendimiento de practicantes

#### CUS_56 — Comparar rendimiento entre practicantes
- **Origen:** CUN_28
- **Actor:** Director (`GP_DIRECTOR`)
- **Objetivo:** Comparar rendimiento entre diferentes practicantes
- **RF-14.5:** Comparar rendimiento entre practicantes

---

## REQUERIMIENTOS FUNCIONALES TRANSVERSALES

| RF | Descripción | Aplica a |
|----|-------------|----------|
| RF-02.1 | Registrar diagnósticos y tratamientos por consulta | CUS_14, CUS_15 |
| RF-02.3 | Paciente consulta su propia HCE | CUS_06 |
| RF-02.4 | Paciente descarga documentos de su HCE | CUS_07 |
| RF-02.5 | Practicante consulta HCE de pacientes asignados | CUS_27, CUS_28 |
| RF-02.6 | Trazabilidad con fecha, hora y usuario responsable | CUS_14, CUS_15 |
| RF-04.4 | Registrar prescripciones en la HCE | CUS_20, CUS_21, CUS_22 |
| RF-05.1 | Notificación automática al confirmar cita | CUS_03, CUS_12 |
| RF-05.2 | Recordatorio automático antes de la cita | CUS_13 |
| RF-05.3 | Notificar al paciente ante cambios en su cita | CUS_04, CUS_05 |
| RF-06.3 | Paciente actualiza sus datos desde su perfil | CUS_10, CUS_11 |
| RF-08.1 | Practicante registra consultas bajo supervisión | CUS_25 |
| RF-08.2 | Practicante envía consultas a revisión del médico | CUS_26 |
| RF-08.3 | Médico registra evaluaciones del practicante | CUS_23 |
| RF-08.4 | Médico consulta historial de evaluaciones | CUS_24 |
| RF-08.5 | Practicante ve sus evaluaciones recibidas | CUS_29 |
| RF-08.6 | Practicante accede a agenda de actividades | CUS_30, CUS_31 |
| RF-12.3 | Activar/desactivar módulos del sistema | CUS_47, CUS_48 |
| RF-13.1 | Logs de todas las actividades de usuarios | CUS_49 |
| RF-13.2 | Logs de eventos de seguridad | CUS_50 |
| RF-13.3 | Filtrar logs por usuario, fecha y tipo | CUS_49, CUS_50 |
| RF-14.1 | Dashboard con KPIs en tiempo real | CUS_51 |
| RF-14.2 | Filtrar dashboard por período y especialidad | CUS_52 |
| RF-14.3 | Reportes de enfermedades y tendencias | CUS_53, CUS_54 |
| RF-14.4 | Indicadores de rendimiento de practicantes | CUS_55 |
| RF-14.5 | Comparar rendimiento entre practicantes | CUS_56 |

---

## ESTRUCTURA DE GRUPOS DE PERMISOS

```
GP_PACIENTE   → CUS_01 al CUS_13
GP_MEDICO     → CUS_14 al CUS_24
GP_PRACTICANTE → CUS_25 al CUS_31
GP_ADMIN_OP   → CUS_32 al CUS_40
GP_ADMIN_SYS  → CUS_41 al CUS_50
GP_DIRECTOR   → CUS_51 al CUS_56
```

---

## MATRIZ DE PERMISOS POR ROL vs MÓDULO

| Módulo | PACIENTE | DOCTOR | PRACTICANTE | ADMINISTRATIVO | ADMINISTRADOR | DIRECTOR |
|--------|----------|--------|-------------|----------------|---------------|----------|
| Citas / Agenda | ✅ | ✅ | ✅ | ✅ | ✅ | — |
| HCE | ✅ (propia) | ✅ | ✅ (asignados) | — | — | — |
| Consultas | — | ✅ | ✅ (supervisado) | — | — | — |
| Teleconsulta | ✅ | ✅ | — | — | — | — |
| Pacientes | — | — | — | ✅ | — | — |
| Usuarios/Roles | — | — | — | — | ✅ | — |
| Configuración | — | ✅ (horarios) | — | — | ✅ | — |
| Consultorios | — | — | — | ✅ | — | — |
| Reportes | — | — | — | ✅ | — | ✅ |
| Dashboard/KPIs | — | — | — | — | — | ✅ |
| Logs | — | — | — | — | ✅ | — |
| Evaluaciones | — | ✅ | ✅ | — | — | ✅ |

---

## FLUJOS PRINCIPALES

### Flujo de Atención Presencial (Doctor)
```
1. Médico ingresa a /app/citas
2. Visualiza su agenda del día (CUS_16)
3. Selecciona paciente → click "Atender"
4. Sistema inicia consulta médica (POST /api/consultas)
5. Médico registra diagnóstico CIE-10 (CUS_14)
6. Médico registra tratamiento (CUS_15)
7. Médico prescribe medicamentos (CUS_20) → receta digital
8. Consulta completada → cita marcada como ATENDIDA
```

### Flujo de Cita (Paciente)
```
1. Paciente busca disponibilidad por especialidad (CUS_01)
2. Selecciona doctor y horario (CUS_02)
3. Confirma agendado → recibe email (CUS_03, CUS_12)
4. (Opcional) Cancela o reprograma (CUS_04, CUS_05)
5. Asiste a consulta → médico registra en HCE
6. Consulta su HCE cuando lo requiere (CUS_06)
```

### Flujo de Atención (Practicante)
```
1. Practicante accede a su agenda de actividades (CUS_30)
2. Registra consulta bajo supervisión (CUS_25)
3. Envía consulta a revisión del médico (CUS_26)
4. Médico supervisor revisa y evalúa (CUS_23)
5. Practicante recibe notificación de evaluación (CUS_29)
```

### Flujo Administrativo
```
1. Administrativo registra paciente (CUS_32) o actualiza datos (CUS_33)
2. Gestiona citas: confirma (CUS_34), cancela (CUS_35), reprograma (CUS_36)
3. Asigna consultorio a doctor (CUS_37) o reasigna (CUS_38)
4. Genera y exporta reporte operativo diario (CUS_39, CUS_40)
```

### Flujo de Configuración (Administrador)
```
1. Crea usuarios y asigna roles (CUS_41, CUS_44)
2. Configura especialidades médicas (CUS_45)
3. Configura horarios de atención (CUS_46)
4. Activa/desactiva módulos del sistema (CUS_47, CUS_48)
5. Monitorea logs de actividad y seguridad (CUS_49, CUS_50)
```

### Flujo Directivo
```
1. Director ingresa al dashboard (CUS_51)
2. Filtra KPIs por período y especialidad (CUS_52)
3. Revisa reportes de enfermedades frecuentes (CUS_53)
4. Analiza tendencias de atención (CUS_54)
5. Revisa rendimiento de practicantes (CUS_55, CUS_56)
```

---

## REGLAS DE NEGOCIO

1. **Un médico solo puede atender citas que le pertenecen** — validación por ID del doctor autenticado
2. **El diagnóstico debe registrarse antes del tratamiento** — validación en CUS_15
3. **No se puede agendar si hay conflicto de horario** — validación en CUS_01 (RF-01.6)
4. **Un consultorio no puede asignarse a dos médicos en el mismo horario** — RF-09.3
5. **Los usuarios se desactivan, no se eliminan** — soft delete (CUS_43)
6. **La HCE tiene trazabilidad obligatoria** — fecha, hora y usuario responsable (RF-02.6)
7. **El practicante requiere supervisión del médico** — flujo de revisión en CUS_25 → CUS_26
8. **Los reportes operativos se alimentan automáticamente** — RF-10.3
9. **Las teleconsultas quedan registradas en la HCE** — RF-03.5
10. **El acceso a funcionalidades se restringe según el rol** — RF-11.5
