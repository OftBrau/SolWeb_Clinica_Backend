SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS logs_actividad;
DROP TABLE IF EXISTS evaluaciones_practicante;
DROP TABLE IF EXISTS actividades_practicante;
DROP TABLE IF EXISTS examenes;
DROP TABLE IF EXISTS modulos_sistema;
DROP TABLE IF EXISTS disponibilidad_doctor;
DROP TABLE IF EXISTS consultas;
DROP TABLE IF EXISTS consultorios;
DROP TABLE IF EXISTS doctor_consultorio;
DROP TABLE IF EXISTS horarios_atencion;
DROP TABLE IF EXISTS teleconsultas;
DROP TABLE IF EXISTS especialidades;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario       INT             AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(255)    NOT NULL,
    apellido         VARCHAR(255)    NOT NULL,
    email            VARCHAR(255)    NOT NULL UNIQUE,
    password_hash    VARCHAR(255)    NOT NULL,
    telefono         VARCHAR(50),
    rol              VARCHAR(50)     NOT NULL,
    estado           VARCHAR(20)     NOT NULL DEFAULT 'ACTIVO',
    password_default TINYINT(1)      NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS pacientes (
    id_paciente       INT             AUTO_INCREMENT PRIMARY KEY,
    id_usuario        INT             NOT NULL UNIQUE,
    codigo_estudiante VARCHAR(50),
    fecha_nacimiento  DATE,
    genero            VARCHAR(20),
    tipo_sangre       VARCHAR(10),
    alergias          TEXT,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS doctores (
    id_doctor     INT             AUTO_INCREMENT PRIMARY KEY,
    id_usuario    INT             NOT NULL UNIQUE,
    especialidad  VARCHAR(100)    NOT NULL,
    descripcion   VARCHAR(300)    NULL,
    bibliografia  TEXT            NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS citas (
    id_cita       INT             AUTO_INCREMENT PRIMARY KEY,
    id_paciente   INT             NOT NULL,
    id_doctor     INT             NOT NULL,
    id_consultorio INT            NULL,
    fecha         DATE            NOT NULL,
    hora          TIME            NOT NULL,
    estado        VARCHAR(30)     NOT NULL,
    tipo          VARCHAR(30)     NOT NULL,
    motivo        VARCHAR(500),
    FOREIGN KEY (id_paciente) REFERENCES pacientes(id_paciente),
    FOREIGN KEY (id_doctor)   REFERENCES doctores(id_doctor)
);

CREATE TABLE IF NOT EXISTS historias_clinicas (
    id_historia   INT   AUTO_INCREMENT PRIMARY KEY,
    id_paciente   INT   NOT NULL UNIQUE,
    FOREIGN KEY (id_paciente) REFERENCES pacientes(id_paciente)
);

CREATE TABLE IF NOT EXISTS consultas (
    id_consulta INT AUTO_INCREMENT PRIMARY KEY,
    id_cita INT NOT NULL,
    id_paciente INT NOT NULL,
    id_doctor INT NOT NULL,
    id_practicante INT,
    estado_revision VARCHAR(20) DEFAULT NULL COMMENT 'PENDIENTE_REVISION | APROBADO | RECHAZADO',
    diagnostico_cie10 VARCHAR(20),
    descripcion_diagnostico TEXT,
    tratamiento TEXT,
    prescripcion TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS consultorios (
    id_consultorio INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(200),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS especialidades (
    id_especialidad INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS horarios_atencion (
    id_horario INT AUTO_INCREMENT PRIMARY KEY,
    id_especialidad INT NOT NULL,
    dia_semana VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    FOREIGN KEY (id_especialidad) REFERENCES especialidades(id_especialidad)
);

CREATE TABLE IF NOT EXISTS doctor_consultorio (
    id_asignacion INT AUTO_INCREMENT PRIMARY KEY,
    id_doctor INT NOT NULL,
    id_consultorio INT NOT NULL,
    dia_semana VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    FOREIGN KEY (id_consultorio) REFERENCES consultorios(id_consultorio)
);

-- Disponibilidad horaria del doctor (CUS_17)
CREATE TABLE IF NOT EXISTS disponibilidad_doctor (
    id_disponibilidad INT AUTO_INCREMENT PRIMARY KEY,
    id_doctor INT NOT NULL,
    dia_semana VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    activo TINYINT(1) DEFAULT 1
);

-- Exámenes solicitados (CUS_21/22)
CREATE TABLE IF NOT EXISTS examenes (
    id_examen INT AUTO_INCREMENT PRIMARY KEY,
    id_consulta INT NOT NULL,
    id_paciente INT NOT NULL,
    id_doctor INT NOT NULL,
    tipo VARCHAR(20) NOT NULL COMMENT 'LABORATORIO | IMAGEN',
    nombre_examen VARCHAR(200) NOT NULL,
    descripcion TEXT,
    resultado TEXT,
    estado VARCHAR(20) DEFAULT 'PENDIENTE' COMMENT 'PENDIENTE | REALIZADO | RECIBIDO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_consulta) REFERENCES consultas(id_consulta)
);

-- Supervisión de practicantes (CUS_25/26)
-- id_practicante = doctores.id_doctor del usuario con rol PRACTICANTE
-- id_supervisor  = doctores.id_doctor del doctor supervisor
CREATE TABLE IF NOT EXISTS supervision_practicantes (
    id_asignacion   INT AUTO_INCREMENT PRIMARY KEY,
    id_practicante  INT NOT NULL UNIQUE,
    id_supervisor   INT NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_practicante) REFERENCES doctores(id_doctor),
    FOREIGN KEY (id_supervisor)  REFERENCES doctores(id_doctor)
);

-- Actividades del practicante (CUS_25)
CREATE TABLE IF NOT EXISTS actividades_practicante (
    id_actividad INT AUTO_INCREMENT PRIMARY KEY,
    id_practicante INT NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    tipo VARCHAR(50) NOT NULL,
    fecha DATE NOT NULL,
    hora TIME,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    id_paciente INT,
    id_supervisor INT,
    FOREIGN KEY (id_paciente) REFERENCES pacientes(id_paciente)
);

-- Evaluaciones de practicantes (CUS_23)
CREATE TABLE IF NOT EXISTS evaluaciones_practicante (
    id_evaluacion INT AUTO_INCREMENT PRIMARY KEY,
    id_practicante INT NOT NULL,
    id_supervisor INT NOT NULL,
    fecha DATE NOT NULL,
    puntuacion DECIMAL(3,1) NOT NULL,
    comentario TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Teleconsultas
CREATE TABLE IF NOT EXISTS teleconsultas (
    id_teleconsulta INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente INT NOT NULL,
    id_doctor INT,
    especialidad VARCHAR(100) NOT NULL,
    url_sesion VARCHAR(500),
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    motivo TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_paciente) REFERENCES pacientes(id_paciente),
    FOREIGN KEY (id_doctor) REFERENCES doctores(id_doctor)
);

-- Chat de teleconsultas
CREATE TABLE IF NOT EXISTS chat_mensajes (
    id_mensaje BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_teleconsulta INT NOT NULL,
    usuario VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    texto TEXT NOT NULL,
    rol VARCHAR(50) NOT NULL,
    hora VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_teleconsulta) REFERENCES teleconsultas(id_teleconsulta)
);

-- Notas de teleconsulta (CUS_19)
CREATE TABLE IF NOT EXISTS notas_teleconsulta (
    id_nota INT AUTO_INCREMENT PRIMARY KEY,
    id_teleconsulta INT NOT NULL,
    id_doctor INT NOT NULL,
    contenido TEXT NOT NULL,
    tipo VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_teleconsulta) REFERENCES teleconsultas(id_teleconsulta),
    FOREIGN KEY (id_doctor) REFERENCES doctores(id_doctor)
);

-- Logs de actividad (CUS_49)
CREATE TABLE IF NOT EXISTS logs_actividad (
    id_log INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT,
    email VARCHAR(100),
    accion VARCHAR(100) NOT NULL,
    detalle TEXT,
    ip VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Módulos del sistema (CUS_47/48)
CREATE TABLE IF NOT EXISTS modulos_sistema (
    id_modulo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    activo TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Migración: agregar id_cita a teleconsultas (solo si no existe)
SET @db = (SELECT DATABASE());
SET @exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'teleconsultas' AND COLUMN_NAME = 'id_cita');
SET @query = IF(@exists = 0, 'ALTER TABLE teleconsultas ADD COLUMN id_cita INT NULL AFTER id_teleconsulta', 'SELECT 1');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Migración: agregar descripcion y bibliografia a doctores (solo si no existen)
SET @exists_desc = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'doctores' AND COLUMN_NAME = 'descripcion');
SET @query_desc = IF(@exists_desc = 0, 'ALTER TABLE doctores ADD COLUMN descripcion VARCHAR(300) NULL AFTER especialidad', 'SELECT 1');
PREPARE stmt FROM @query_desc;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @exists_bib = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'doctores' AND COLUMN_NAME = 'bibliografia');
SET @query_bib = IF(@exists_bib = 0, 'ALTER TABLE doctores ADD COLUMN bibliografia TEXT NULL AFTER descripcion', 'SELECT 1');
PREPARE stmt FROM @query_bib;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Insertar especialidades por defecto
INSERT IGNORE INTO especialidades (nombre, descripcion, estado) VALUES
('Medicina General', 'Atención médica integral', 'ACTIVO'),
('Obstetricia', 'Atención del embarazo y parto', 'ACTIVO'),
('Nutrición', 'Orientación alimenticia', 'ACTIVO'),
('Psicología', 'Salud mental y emocional', 'ACTIVO'),
('Rehabilitación', 'Terapia física y rehabilitación', 'ACTIVO'),
('Fisioterapia', 'Terapia física', 'ACTIVO');

-- Insertar doctores de prueba (password: clinica123)
INSERT IGNORE INTO usuarios (nombre, apellido, email, password_hash, telefono, rol, estado, password_default) VALUES
('Ricardo', 'Palma', 'rpalma@clinica.com', '$2b$12$kf0cUCIPyhYbiHPuYEVmvuA4hHlGIup1CuOhTQe3Vn0Zz1XhA4Vri', '999888001', 'DOCTOR', 'ACTIVO', 0),
('Carmen', 'Lozano', 'clozano@clinica.com', '$2b$12$kf0cUCIPyhYbiHPuYEVmvuA4hHlGIup1CuOhTQe3Vn0Zz1XhA4Vri', '999888002', 'DOCTOR', 'ACTIVO', 0),
('Andrea', 'Montes', 'amontes@clinica.com', '$2b$12$kf0cUCIPyhYbiHPuYEVmvuA4hHlGIup1CuOhTQe3Vn0Zz1XhA4Vri', '999888003', 'DOCTOR', 'ACTIVO', 0),
('Ana', 'Quispe', 'aquispe@clinica.com', '$2b$12$kf0cUCIPyhYbiHPuYEVmvuA4hHlGIup1CuOhTQe3Vn0Zz1XhA4Vri', '999888004', 'DOCTOR', 'ACTIVO', 0),
('Luis', 'Vega', 'lvega@clinica.com', '$2b$12$kf0cUCIPyhYbiHPuYEVmvuA4hHlGIup1CuOhTQe3Vn0Zz1XhA4Vri', '999888005', 'DOCTOR', 'ACTIVO', 0),
('Carlos', 'Mendoza', 'cmendoza@clinica.com', '$2b$12$kf0cUCIPyhYbiHPuYEVmvuA4hHlGIup1CuOhTQe3Vn0Zz1XhA4Vri', '999888006', 'DOCTOR', 'ACTIVO', 0),
('Pamela', 'Ríos', 'prios@clinica.com', '$2b$12$kf0cUCIPyhYbiHPuYEVmvuA4hHlGIup1CuOhTQe3Vn0Zz1XhA4Vri', '999888007', 'DOCTOR', 'ACTIVO', 0),
('Marco', 'Silva', 'msilva@clinica.com', '$2b$12$kf0cUCIPyhYbiHPuYEVmvuA4hHlGIup1CuOhTQe3Vn0Zz1XhA4Vri', '999888008', 'DOCTOR', 'ACTIVO', 0),
('Pedro', 'Castillo', 'pcastillo@clinica.com', '$2b$12$kf0cUCIPyhYbiHPuYEVmvuA4hHlGIup1CuOhTQe3Vn0Zz1XhA4Vri', '999888009', 'DOCTOR', 'ACTIVO', 0);

INSERT IGNORE INTO doctores (id_usuario, especialidad, descripcion, bibliografia) VALUES
((SELECT id_usuario FROM usuarios WHERE email = 'rpalma@clinica.com'), 'Medicina General', 'Médico general con amplia experiencia en atención primaria.', 'Egresado de la Universidad Nacional Mayor de San Marcos. Con 10 años de experiencia en el sector público y privado.'),
((SELECT id_usuario FROM usuarios WHERE email = 'clozano@clinica.com'), 'Medicina General', 'Especialista en medicina familiar y preventiva.', 'Médica cirujana por la Universidad Peruana Cayetano Heredia. Diplomado en Salud Familiar.'),
((SELECT id_usuario FROM usuarios WHERE email = 'amontes@clinica.com'), 'Obstetricia', 'Obstetra dedicada al cuidado integral de la mujer.', 'Licenciada en Obstetricia por la Universidad Nacional Mayor de San Marcos. Especialista en Alto Riesgo Obstétrico.'),
((SELECT id_usuario FROM usuarios WHERE email = 'aquispe@clinica.com'), 'Nutrición', 'Nutricionista clínica especializada en planes personalizados.', 'Nutricionista por la Universidad Peruana de Ciencias Aplicadas. Maestría en Nutrición Clínica.'),
((SELECT id_usuario FROM usuarios WHERE email = 'lvega@clinica.com'), 'Nutrición', 'Nutricionista deportivo y clínico.', 'Egresado de la Universidad San Ignacio de Loyola. Certificación internacional en Nutrición Deportiva.'),
((SELECT id_usuario FROM usuarios WHERE email = 'cmendoza@clinica.com'), 'Psicología', 'Psicólogo clínico con enfoque cognitivo-conductual.', 'Psicólogo por la Pontificia Universidad Católica del Perú. Maestría en Psicología Clínica.'),
((SELECT id_usuario FROM usuarios WHERE email = 'prios@clinica.com'), 'Psicología', 'Psicóloga especializada en salud mental y bienestar emocional.', 'Psicóloga por la Universidad de Lima. Experiencia en terapia individual y grupal.'),
((SELECT id_usuario FROM usuarios WHERE email = 'msilva@clinica.com'), 'Rehabilitación', 'Especialista en medicina física y rehabilitación.', 'Médico rehabilitador por la Universidad Nacional Mayor de San Marcos. Experiencia en rehabilitación neurológica.'),
((SELECT id_usuario FROM usuarios WHERE email = 'pcastillo@clinica.com'), 'Fisioterapia', 'Fisioterapeuta especializado en terapia manual y deportiva.', 'Fisioterapeuta por la Universidad Peruana de Ciencias Aplicadas. Certificado en Terapia Manual Ortopédica.');

-- Insertar módulos por defecto
INSERT IGNORE INTO modulos_sistema (nombre, descripcion) VALUES
('CITAS', 'Gestión de citas médicas'),
('HCE', 'Historia Clínica Electrónica'),
('PACIENTES', 'Gestión de pacientes'),
('USUARIOS', 'Gestión de usuarios del sistema'),
('CONSULTORIOS', 'Gestión de consultorios'),
('ESPECIALIDADES', 'Configuración de especialidades'),
('HORARIOS', 'Configuración de horarios'),
('REPORTES', 'Generación de reportes'),
('TELECONSULTA', 'Módulo de teleconsultas'),
('EXAMENES', 'Solicitud de exámenes'),
('LOGS', 'Registro de actividad del sistema');
