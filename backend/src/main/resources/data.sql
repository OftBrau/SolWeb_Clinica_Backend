INSERT IGNORE INTO consultas (id_consulta, id_cita, id_paciente, id_doctor, diagnostico_cie10, descripcion_diagnostico, tratamiento, prescripcion, estado_revision, created_at) VALUES
(1, 1, 1, 1, 'G43.9', 'Migraña sin especificación', 'Reposo, evitar estrés', 'Ibuprofeno 400mg cada 8h por 5 días', 'APROBADO', '2026-04-26 10:00:00'),
(2, 2, 2, 1, 'Z00.00', 'Examen médico general sin hallazgos', 'Estilo de vida saludable', 'Multivitamínico 1 diario', 'APROBADO', '2026-04-26 11:00:00'),
(3, 3, 1, 2, 'F41.9', 'Trastorno de ansiedad no especificado', 'Terapia semanal por 2 meses', 'Sertralina 50mg cada 24h', 'APROBADO', '2026-04-28 16:00:00'),
(4, 5, 3, 1, 'R10.4', 'Dolor abdominal inespecífico', 'Dieta blanda por 3 días, hidratación', 'Omeprazol 20mg cada 24h, Hioscina 10mg si dolor', 'APROBADO', '2026-05-25 17:30:00');
