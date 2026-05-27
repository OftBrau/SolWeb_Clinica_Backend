-- ============================================================
--  STORED PROCEDURES — Clínica UPN
--  Separador: $$ (ScriptUtils usa $$ como delimitador)
-- ============================================================

-- ============================================================
--  1. usp_registrar_paciente
--  Inserta en usuarios + pacientes + historias_clinicas (TX)
-- ============================================================
DROP PROCEDURE IF EXISTS usp_registrar_paciente$$

CREATE PROCEDURE usp_registrar_paciente(
    IN  p_nombre           VARCHAR(255),
    IN  p_apellido         VARCHAR(255),
    IN  p_email            VARCHAR(255),
    IN  p_password_hash    VARCHAR(255),
    IN  p_telefono         VARCHAR(50),
    IN  p_codigo_estudiante VARCHAR(50),
    IN  p_fecha_nacimiento  DATE,
    IN  p_genero           VARCHAR(20),
    IN  p_tipo_sangre      VARCHAR(10),
    IN  p_alergias         TEXT,
    OUT p_id_usuario       INT,
    OUT p_id_paciente      INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    INSERT INTO usuarios (nombre, apellido, email, password_hash, telefono, rol, estado)
    VALUES (p_nombre, p_apellido, p_email, p_password_hash, p_telefono, 'PACIENTE', 'ACTIVO');
    SET p_id_usuario = LAST_INSERT_ID();

    INSERT INTO pacientes (id_usuario, codigo_estudiante, fecha_nacimiento, genero, tipo_sangre, alergias)
    VALUES (p_id_usuario, p_codigo_estudiante, p_fecha_nacimiento, p_genero, p_tipo_sangre, p_alergias);
    SET p_id_paciente = LAST_INSERT_ID();

    INSERT INTO historias_clinicas (id_paciente) VALUES (p_id_paciente);

    COMMIT;
END$$

-- ============================================================
--  2. usp_actualizar_paciente
--  Actualiza usuarios + pacientes (TX)
-- ============================================================
DROP PROCEDURE IF EXISTS usp_actualizar_paciente$$

CREATE PROCEDURE usp_actualizar_paciente(
    IN p_id_paciente       INT,
    IN p_nombre            VARCHAR(255),
    IN p_apellido          VARCHAR(255),
    IN p_telefono          VARCHAR(50),
    IN p_codigo_estudiante VARCHAR(50),
    IN p_fecha_nacimiento  DATE,
    IN p_genero            VARCHAR(20),
    IN p_tipo_sangre       VARCHAR(10),
    IN p_alergias          TEXT
)
BEGIN
    DECLARE v_id_usuario INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    SELECT id_usuario INTO v_id_usuario FROM pacientes WHERE id_paciente = p_id_paciente;

    UPDATE usuarios
    SET nombre = p_nombre, apellido = p_apellido, telefono = p_telefono
    WHERE id_usuario = v_id_usuario;

    UPDATE pacientes
    SET codigo_estudiante = p_codigo_estudiante,
        fecha_nacimiento  = p_fecha_nacimiento,
        genero            = p_genero,
        tipo_sangre       = p_tipo_sangre,
        alergias          = p_alergias
    WHERE id_paciente = p_id_paciente;

    COMMIT;
END$$

-- ============================================================
--  3. usp_iniciar_consulta
--  Crea consulta + marca cita como ATENDIDA (TX)
-- ============================================================
DROP PROCEDURE IF EXISTS usp_iniciar_consulta$$

CREATE PROCEDURE usp_iniciar_consulta(
    IN  p_id_cita                  INT,
    IN  p_id_paciente              INT,
    IN  p_id_doctor                INT,
    IN  p_diagnostico_cie10        VARCHAR(20),
    IN  p_descripcion_diagnostico  TEXT,
    IN  p_tratamiento              TEXT,
    IN  p_prescripcion             TEXT,
    IN  p_id_practicante           INT,
    OUT p_id_consulta              INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    INSERT INTO consultas (id_cita, id_paciente, id_doctor, diagnostico_cie10,
                           descripcion_diagnostico, tratamiento, prescripcion, id_practicante)
    VALUES (p_id_cita, p_id_paciente, p_id_doctor, p_diagnostico_cie10,
            p_descripcion_diagnostico, p_tratamiento, p_prescripcion, p_id_practicante);
    SET p_id_consulta = LAST_INSERT_ID();

    UPDATE citas SET estado = 'ATENDIDA' WHERE id_cita = p_id_cita;

    COMMIT;
END$$

-- ============================================================
--  4. usp_generar_reporte_diario
--  Retorna 3 result sets: resumen, por_especialidad, por_doctor
-- ============================================================
DROP PROCEDURE IF EXISTS usp_generar_reporte_diario$$

CREATE PROCEDURE usp_generar_reporte_diario(IN p_fecha DATE)
BEGIN
    SELECT
        (SELECT COUNT(*) FROM citas WHERE fecha = p_fecha)                                                   AS total_citas,
        (SELECT COUNT(*) FROM citas WHERE estado = 'CONFIRMADA'  AND fecha = p_fecha)                        AS confirmadas,
        (SELECT COUNT(*) FROM citas WHERE estado = 'ATENDIDA'    AND fecha = p_fecha)                        AS atendidas,
        (SELECT COUNT(*) FROM citas WHERE estado = 'CANCELADA'   AND fecha = p_fecha)                        AS canceladas,
        (SELECT COUNT(*) FROM citas WHERE estado = 'NO_ASISTIO'  AND fecha = p_fecha)                        AS no_asistieron,
        (SELECT COUNT(DISTINCT id_paciente) FROM consultas WHERE DATE(created_at) = p_fecha)                 AS pacientes_atendidos,
        (SELECT COUNT(*) FROM doctores d JOIN usuarios u ON d.id_usuario = u.id_usuario WHERE u.estado = 'ACTIVO') AS doctores_activos;

    SELECT d.especialidad, COUNT(*) AS cantidad
    FROM citas c
    JOIN doctores d ON c.id_doctor = d.id_doctor
    WHERE c.fecha = p_fecha
    GROUP BY d.especialidad
    ORDER BY cantidad DESC;

    SELECT CONCAT(u.nombre, ' ', u.apellido) AS nombre_doctor, d.especialidad, COUNT(*) AS cantidad
    FROM citas c
    JOIN doctores d ON c.id_doctor = d.id_doctor
    JOIN usuarios u ON d.id_usuario = u.id_usuario
    WHERE c.fecha = p_fecha
    GROUP BY c.id_doctor, u.nombre, u.apellido, d.especialidad
    ORDER BY cantidad DESC;
END$$

-- ============================================================
--  5. usp_buscar_historial_paciente
--  HCE: historial clínico por ID de paciente
-- ============================================================
DROP PROCEDURE IF EXISTS usp_buscar_historial_paciente$$

CREATE PROCEDURE usp_buscar_historial_paciente(IN p_id_paciente INT)
BEGIN
    SELECT v.*, c.id_consulta
    FROM vista_historial_paciente v
    LEFT JOIN consultas c ON c.id_paciente = v.id_paciente
        AND COALESCE(c.diagnostico_cie10,'')       = COALESCE(v.diagnostico_cie10,'')
        AND COALESCE(c.descripcion_diagnostico,'') = COALESCE(v.descripcion_diag,'')
        AND COALESCE(c.tratamiento,'')             = COALESCE(v.tratamiento,'')
        AND COALESCE(c.prescripcion,'')            = COALESCE(v.prescripcion,'')
    WHERE v.id_paciente = p_id_paciente
    ORDER BY v.fecha DESC;
END$$

-- ============================================================
--  6. usp_buscar_historial_doctor
--  HCE: historial clínico por nombre de doctor
-- ============================================================
DROP PROCEDURE IF EXISTS usp_buscar_historial_doctor$$

CREATE PROCEDURE usp_buscar_historial_doctor(IN p_nombre_doctor VARCHAR(201))
BEGIN
    SELECT v.*, c.id_consulta
    FROM vista_historial_paciente v
    LEFT JOIN consultas c ON c.id_paciente = v.id_paciente
        AND COALESCE(c.diagnostico_cie10,'')       = COALESCE(v.diagnostico_cie10,'')
        AND COALESCE(c.descripcion_diagnostico,'') = COALESCE(v.descripcion_diag,'')
        AND COALESCE(c.tratamiento,'')             = COALESCE(v.tratamiento,'')
        AND COALESCE(c.prescripcion,'')            = COALESCE(v.prescripcion,'')
    WHERE v.nombre_doctor = p_nombre_doctor
    ORDER BY v.fecha DESC;
END$$

-- ============================================================
--  7. usp_buscar_historial_consulta
--  HCE: obtener historial por ID de consulta
-- ============================================================
DROP PROCEDURE IF EXISTS usp_buscar_historial_consulta$$

CREATE PROCEDURE usp_buscar_historial_consulta(IN p_id_consulta INT)
BEGIN
    SELECT v.*, c.id_consulta
    FROM consultas c
    JOIN citas ct ON ct.id_cita = c.id_cita
    LEFT JOIN vista_historial_paciente v ON v.id_paciente = c.id_paciente
        AND COALESCE(v.diagnostico_cie10,'') = COALESCE(c.diagnostico_cie10,'')
        AND COALESCE(v.descripcion_diag,'')  = COALESCE(c.descripcion_diagnostico,'')
        AND COALESCE(v.tratamiento,'')       = COALESCE(c.tratamiento,'')
        AND COALESCE(v.prescripcion,'')      = COALESCE(c.prescripcion,'')
    WHERE c.id_consulta = p_id_consulta;
END$$

-- ============================================================
--  8. usp_buscar_historial_todos
--  HCE: todo el historial (admin/director)
-- ============================================================
DROP PROCEDURE IF EXISTS usp_buscar_historial_todos$$

CREATE PROCEDURE usp_buscar_historial_todos()
BEGIN
    SELECT v.*, c.id_consulta
    FROM vista_historial_paciente v
    LEFT JOIN consultas c ON c.id_paciente = v.id_paciente
        AND COALESCE(c.diagnostico_cie10,'')       = COALESCE(v.diagnostico_cie10,'')
        AND COALESCE(c.descripcion_diagnostico,'') = COALESCE(v.descripcion_diag,'')
        AND COALESCE(c.tratamiento,'')             = COALESCE(v.tratamiento,'')
        AND COALESCE(c.prescripcion,'')            = COALESCE(v.prescripcion,'')
    ORDER BY v.fecha DESC;
END$$

-- ============================================================
--  9. usp_buscar_pacientes_asignados
--  Busca pacientes por nombre o código (para practicante)
-- ============================================================
DROP PROCEDURE IF EXISTS usp_buscar_pacientes_asignados$$

CREATE PROCEDURE usp_buscar_pacientes_asignados(IN p_query VARCHAR(100))
BEGIN
    SELECT DISTINCT p.id_paciente,
           CONCAT(u.nombre, ' ', u.apellido) AS nombre_completo,
           p.codigo_estudiante,
           (SELECT MAX(c.created_at) FROM consultas c WHERE c.id_paciente = p.id_paciente) AS ultima_consulta
    FROM pacientes p
    JOIN usuarios u ON p.id_usuario = u.id_usuario
    LEFT JOIN consultas c ON c.id_paciente = p.id_paciente
    WHERE CONCAT(u.nombre, ' ', u.apellido) LIKE CONCAT('%', p_query, '%')
       OR p.codigo_estudiante LIKE CONCAT('%', p_query, '%')
    GROUP BY p.id_paciente, u.nombre, u.apellido, p.codigo_estudiante
    ORDER BY u.nombre
    LIMIT 20;
END$$

-- ============================================================
-- 10. usp_buscar_doctores_por_especialidad
--  Lista doctores activos por especialidad
-- ============================================================
DROP PROCEDURE IF EXISTS usp_buscar_doctores_por_especialidad$$

CREATE PROCEDURE usp_buscar_doctores_por_especialidad(IN p_especialidad VARCHAR(100))
BEGIN
    SELECT d.id_doctor,
           CONCAT(u.nombre, ' ', u.apellido) AS nombre_completo,
           d.especialidad
    FROM doctores d
    JOIN usuarios u ON d.id_usuario = u.id_usuario
    WHERE d.especialidad = p_especialidad AND u.estado = 'ACTIVO'
    ORDER BY u.nombre;
END$$

-- ============================================================
-- 11. usp_verificar_conflicto_cita
--  Verifica si un doctor ya tiene cita en fecha/hora
-- ============================================================
DROP PROCEDURE IF EXISTS usp_verificar_conflicto_cita$$

CREATE PROCEDURE usp_verificar_conflicto_cita(
    IN p_id_doctor INT,
    IN p_fecha     DATE,
    IN p_hora      TIME,
    OUT p_conflicto BOOLEAN
)
BEGIN
    SELECT COUNT(*) > 0 INTO p_conflicto
    FROM citas
    WHERE id_doctor = p_id_doctor
      AND fecha = p_fecha
      AND hora = p_hora
      AND estado NOT IN ('CANCELADA', 'NO_ASISTIO');
END$$

-- ============================================================
-- 12. usp_verificar_conflicto_consultorio
--  Verifica conflicto de horario en asignación de consultorio
-- ============================================================
DROP PROCEDURE IF EXISTS usp_verificar_conflicto_consultorio$$

CREATE PROCEDURE usp_verificar_conflicto_consultorio(
    IN p_id_consultorio INT,
    IN p_id_doctor      INT,
    IN p_dia_semana     VARCHAR(20),
    IN p_hora_inicio    TIME,
    IN p_hora_fin       TIME,
    OUT p_conflicto     BOOLEAN
)
BEGIN
    SELECT COUNT(*) > 0 INTO p_conflicto
    FROM doctor_consultorio
    WHERE id_consultorio = p_id_consultorio
      AND id_doctor != COALESCE(p_id_doctor, 0)
      AND dia_semana = p_dia_semana
      AND ((hora_inicio < p_hora_fin AND hora_fin > p_hora_inicio)
        OR (hora_inicio < p_hora_fin AND hora_fin > p_hora_inicio)
        OR (hora_inicio >= p_hora_inicio AND hora_fin <= p_hora_fin));
END$$

-- ============================================================
-- 13. usp_verificar_conflicto_disponibilidad
--  Verifica conflicto de horario en disponibilidad del doctor
-- ============================================================
DROP PROCEDURE IF EXISTS usp_verificar_conflicto_disponibilidad$$

CREATE PROCEDURE usp_verificar_conflicto_disponibilidad(
    IN p_id_doctor   INT,
    IN p_dia_semana  VARCHAR(20),
    IN p_hora_inicio TIME,
    IN p_hora_fin    TIME,
    OUT p_conflicto  BOOLEAN
)
BEGIN
    SELECT COUNT(*) > 0 INTO p_conflicto
    FROM disponibilidad_doctor
    WHERE id_doctor = p_id_doctor
      AND dia_semana = p_dia_semana
      AND activo = 1
      AND ((hora_inicio <= p_hora_inicio AND hora_fin > p_hora_inicio)
        OR (hora_inicio < p_hora_fin AND hora_fin >= p_hora_fin));
END$$

-- ============================================================
-- 14. usp_buscar_consultorios_disponibles
--  Consultorios libres en un día/hora
-- ============================================================
DROP PROCEDURE IF EXISTS usp_buscar_consultorios_disponibles$$

CREATE PROCEDURE usp_buscar_consultorios_disponibles(
    IN p_dia_semana VARCHAR(20),
    IN p_hora       TIME
)
BEGIN
    SELECT c.id_consultorio, c.nombre, c.ubicacion, c.estado
    FROM consultorios c
    WHERE c.estado = 'ACTIVO'
      AND c.id_consultorio NOT IN (
          SELECT dc.id_consultorio
          FROM doctor_consultorio dc
          WHERE dc.dia_semana = p_dia_semana
            AND p_hora BETWEEN dc.hora_inicio AND dc.hora_fin
      )
    ORDER BY c.nombre;
END$$

-- ============================================================
-- 15. usp_desactivar_paciente
--  Soft delete de paciente (estado = 'INACTIVO')
-- ============================================================
DROP PROCEDURE IF EXISTS usp_desactivar_paciente$$

CREATE PROCEDURE usp_desactivar_paciente(IN p_id_usuario INT)
BEGIN
    UPDATE usuarios SET estado = 'INACTIVO' WHERE id_usuario = p_id_usuario;
END$$

-- ============================================================
-- 16. usp_registrar_doctor
--  Inserta en usuarios + doctores (TX), devuelve IDs
-- ============================================================
DROP PROCEDURE IF EXISTS usp_registrar_doctor$$

CREATE PROCEDURE usp_registrar_doctor(
    IN  p_nombre        VARCHAR(255),
    IN  p_apellido      VARCHAR(255),
    IN  p_email         VARCHAR(255),
    IN  p_password_hash VARCHAR(255),
    IN  p_telefono      VARCHAR(50),
    IN  p_especialidad  VARCHAR(100),
    OUT p_id_usuario    INT,
    OUT p_id_doctor     INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    INSERT INTO usuarios (nombre, apellido, email, password_hash, telefono, rol, estado)
    VALUES (p_nombre, p_apellido, p_email, p_password_hash, p_telefono, 'DOCTOR', 'ACTIVO');
    SET p_id_usuario = LAST_INSERT_ID();

    INSERT INTO doctores (id_usuario, especialidad)
    VALUES (p_id_usuario, p_especialidad);
    SET p_id_doctor = LAST_INSERT_ID();

    COMMIT;
END$$

-- ============================================================
-- 17. usp_actualizar_doctor
--  Actualiza usuarios + doctores (TX)
-- ============================================================
DROP PROCEDURE IF EXISTS usp_actualizar_doctor$$

CREATE PROCEDURE usp_actualizar_doctor(
    IN p_id_doctor    INT,
    IN p_nombre       VARCHAR(255),
    IN p_apellido     VARCHAR(255),
    IN p_telefono     VARCHAR(50),
    IN p_especialidad VARCHAR(100)
)
BEGIN
    DECLARE v_id_usuario INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    SELECT id_usuario INTO v_id_usuario FROM doctores WHERE id_doctor = p_id_doctor;

    UPDATE usuarios
    SET nombre = p_nombre, apellido = p_apellido, telefono = p_telefono
    WHERE id_usuario = v_id_usuario;

    UPDATE doctores
    SET especialidad = p_especialidad
    WHERE id_doctor = p_id_doctor;

    COMMIT;
END$$

-- ============================================================
-- 18. usp_eliminar_doctor
--  Soft-delete: pone estado = 'INACTIVO' en usuarios
-- ============================================================
DROP PROCEDURE IF EXISTS usp_eliminar_doctor$$

CREATE PROCEDURE usp_eliminar_doctor(IN p_id_doctor INT)
BEGIN
    DECLARE v_id_usuario INT;

    SELECT id_usuario INTO v_id_usuario FROM doctores WHERE id_doctor = p_id_doctor;

    UPDATE usuarios SET estado = 'INACTIVO' WHERE id_usuario = v_id_usuario;
END$$
