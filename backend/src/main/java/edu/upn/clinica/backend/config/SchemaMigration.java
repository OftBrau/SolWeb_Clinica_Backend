package edu.upn.clinica.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
@Order(5)
public class SchemaMigration implements CommandLineRunner {

    private final DataSource dataSource;

    public SchemaMigration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS asignaciones_enfermero (
                    id_asignacion    INT AUTO_INCREMENT PRIMARY KEY,
                    id_enfermero     INT NOT NULL UNIQUE,
                    id_doctor        INT NOT NULL,
                    activo           TINYINT(1) DEFAULT 1,
                    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (id_enfermero) REFERENCES doctores(id_doctor),
                    FOREIGN KEY (id_doctor)    REFERENCES doctores(id_doctor)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS triajes (
                    id_triaje           INT AUTO_INCREMENT PRIMARY KEY,
                    id_cita             INT NOT NULL,
                    id_enfermero        INT NOT NULL,
                    presion_arterial    VARCHAR(10),
                    temperatura         DECIMAL(4,1),
                    frecuencia_cardiaca INT,
                    saturacion          DECIMAL(4,1),
                    peso                DECIMAL(5,2),
                    talla               DECIMAL(5,2),
                    motivo_consulta     TEXT,
                    notas               TEXT,
                    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (id_cita)      REFERENCES citas(id_cita),
                    FOREIGN KEY (id_enfermero) REFERENCES doctores(id_doctor)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS pagos_citas (
                    id_pago       INT AUTO_INCREMENT PRIMARY KEY,
                    id_cita       INT NOT NULL,
                    monto         DECIMAL(10,2) NOT NULL,
                    metodo_pago   VARCHAR(30) DEFAULT 'MERCADOPAGO',
                    estado_pago   VARCHAR(20) DEFAULT 'PENDIENTE',
                    referencia_mp VARCHAR(255),
                    fecha_pago    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (id_cita) REFERENCES citas(id_cita)
            )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS invitaciones_practicante (
                    id_invitacion   INT AUTO_INCREMENT PRIMARY KEY,
                    id_doctor       INT NOT NULL,
                    id_practicante  INT NOT NULL,
                    mensaje         TEXT,
                    estado          VARCHAR(20) DEFAULT 'PENDIENTE',
                    fecha_creacion  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    fecha_respuesta TIMESTAMP NULL,
                    FOREIGN KEY (id_doctor)      REFERENCES doctores(id_doctor),
                    FOREIGN KEY (id_practicante) REFERENCES doctores(id_doctor)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS tareas_practicante (
                    id_tarea        INT AUTO_INCREMENT PRIMARY KEY,
                    id_doctor       INT NOT NULL,
                    id_practicante  INT NOT NULL,
                    titulo          VARCHAR(200) NOT NULL,
                    descripcion     TEXT,
                    tipo            VARCHAR(50) DEFAULT 'OTRO',
                    prioridad       VARCHAR(20) DEFAULT 'MEDIA',
                    estado          VARCHAR(20) DEFAULT 'PENDIENTE',
                    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    fecha_limite    DATE NULL,
                    FOREIGN KEY (id_doctor)      REFERENCES doctores(id_doctor),
                    FOREIGN KEY (id_practicante) REFERENCES doctores(id_doctor)
                )
            """);

            executeIgnoreError(stmt,
                "ALTER TABLE usuarios MODIFY COLUMN rol VARCHAR(50) NOT NULL");
            executeIgnoreError(stmt,
                "ALTER TABLE citas MODIFY COLUMN id_doctor INT NULL");
            executeIgnoreError(stmt,
                "ALTER TABLE citas DROP INDEX uq_paciente_horario");
            executeIgnoreError(stmt,
                "ALTER TABLE citas ADD COLUMN tipo_reserva VARCHAR(20) DEFAULT 'BASICA'");
            executeIgnoreError(stmt,
                "ALTER TABLE citas ADD COLUMN id_asistente INT NULL");
            executeIgnoreError(stmt,
                "ALTER TABLE citas ADD COLUMN id_enfermero INT NULL");
            executeIgnoreError(stmt,
                "ALTER TABLE citas ADD COLUMN monto_extra DECIMAL(10,2) DEFAULT 0.00");
            executeIgnoreError(stmt,
                "ALTER TABLE citas ADD COLUMN id_especialidad INT NULL");
            executeIgnoreError(stmt,
                "ALTER TABLE especialidades ADD COLUMN costo_extra DECIMAL(10,2) DEFAULT 0.00");

            executeIgnoreError(stmt,
                "UPDATE especialidades SET costo_extra = 0.00 WHERE nombre = 'Medicina General' AND costo_extra = 0.00");
            executeIgnoreError(stmt,
                "UPDATE especialidades SET costo_extra = 45.00 WHERE nombre = 'Obstetricia' AND costo_extra = 0.00");
            executeIgnoreError(stmt,
                "UPDATE especialidades SET costo_extra = 30.00 WHERE nombre = 'Nutrición' AND costo_extra = 0.00");
            executeIgnoreError(stmt,
                "UPDATE especialidades SET costo_extra = 50.00 WHERE nombre = 'Psicología' AND costo_extra = 0.00");
            executeIgnoreError(stmt,
                "UPDATE especialidades SET costo_extra = 35.00 WHERE nombre = 'Rehabilitación' AND costo_extra = 0.00");
            executeIgnoreError(stmt,
                "UPDATE especialidades SET costo_extra = 30.00 WHERE nombre = 'Fisioterapia' AND costo_extra = 0.00");

            seedDoctores(stmt);

            stmt.executeUpdate("INSERT IGNORE INTO doctores (id_usuario, especialidad, CMP) " +
                    "SELECT u.id_usuario, 'Enfermería', CONCAT('ENF-', LPAD(u.id_usuario, 6, '0')) " +
                    "FROM usuarios u WHERE u.rol = 'ENFERMERO' AND u.id_usuario NOT IN (SELECT id_usuario FROM doctores)");

            System.out.println("[SchemaMigration] Migraciones ejecutadas correctamente.");
        }
    }

    private void seedDoctores(Statement stmt) throws Exception {
        String hash = "$2b$12$kf0cUCIPyhYbiHPuYEVmvuA4hHlGIup1CuOhTQe3Vn0Zz1XhA4Vri";

        Object[][] doctores = {
            {"Julio", "Ramirez", "jramirez@clinica.com", "999888010", "Cardiología", "Cardiólogo especialista en enfermedades cardiovasculares.", "Egresado de la Universidad Peruana Cayetano Heredia."},
            {"Rosa", "Flores", "rflores@clinica.com", "999888011", "Dermatología", "Dermatóloga con experiencia en diagnóstico de piel.", "Universidad Nacional Mayor de San Marcos."},
            {"Gloria", "Paredes", "gparedes@clinica.com", "999888012", "Ginecología", "Ginecóloga con enfoque en salud integral de la mujer.", "Universidad Peruana de Ciencias Aplicadas."},
            {"Hector", "Salas", "hsalas@clinica.com", "999888013", "Neurología", "Neurólogo clínico especializado en trastornos del sistema nervioso.", "Pontificia Universidad Católica del Perú."},
            {"Diana", "Lopez", "dlopez@clinica.com", "999888014", "Oftalmología", "Oftalmóloga con experiencia en cirugía refractiva.", "Universidad de San Martín de Porres."},
            {"Fernando", "Rojas", "frojas@clinica.com", "999888015", "Pediatría", "Pediatra dedicado al cuidado infantil integral.", "Universidad Nacional Mayor de San Marcos."},
            {"Sandra", "Torres", "storres@clinica.com", "999888016", "Traumatología", "Traumatóloga especialista en lesiones osteoarticulares.", "Universidad Peruana Cayetano Heredia."},
        };

        for (Object[] d : doctores) {
            stmt.executeUpdate("INSERT IGNORE INTO usuarios (nombre, apellido, email, password_hash, telefono, rol, estado, password_default) " +
                    "VALUES ('" + d[0] + "', '" + d[1] + "', '" + d[2] + "', '" + hash + "', '" + d[3] + "', 'DOCTOR', 'ACTIVO', 0)");
            stmt.executeUpdate("INSERT IGNORE INTO doctores (id_usuario, especialidad, descripcion, bibliografia, CMP, destacado) " +
                    "VALUES ((SELECT id_usuario FROM usuarios WHERE email = '" + d[2] + "'), '" + d[4] + "', '" + d[5] + "', '" + d[6] + "', 'CMP-" + ((String)d[2]).split("@")[0] + "', TRUE)");
        }

        System.out.println("[SchemaMigration] Doctores de prueba insertados: " + doctores.length);
    }

    private void executeIgnoreError(Statement stmt, String sql) {
        try {
            stmt.executeUpdate(sql);
        } catch (Exception ignored) {
        }
    }
}
