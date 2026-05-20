DROP TABLE IF EXISTS consultas;
CREATE TABLE consultas (
    id_consulta INT AUTO_INCREMENT PRIMARY KEY,
    id_cita INT NOT NULL,
    id_paciente INT NOT NULL,
    id_doctor INT NOT NULL,
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
