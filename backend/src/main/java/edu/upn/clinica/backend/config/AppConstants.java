package edu.upn.clinica.backend.config;

// ============================================================
//  AppConstants.java
//  edu.upn.clinica.backend.config
//  Constantes globales del sistema
// ============================================================
public final class AppConstants {

    private AppConstants() {}

    // --- Roles ---
    public static final String ROL_PACIENTE       = "PACIENTE";
    public static final String ROL_DOCTOR         = "DOCTOR";
    public static final String ROL_PRACTICANTE    = "PRACTICANTE";
    public static final String ROL_ADMINISTRATIVO = "ADMINISTRATIVO";
    public static final String ROL_ADMINISTRADOR  = "ADMINISTRADOR";
    public static final String ROL_DIRECTOR       = "DIRECTOR";

    // --- Estados de cita ---
    public static final String CITA_CONFIRMADA    = "CONFIRMADA";
    public static final String CITA_EN_ATENCION   = "EN_ATENCION";
    public static final String CITA_ATENDIDA      = "ATENDIDA";
    public static final String CITA_CANCELADA     = "CANCELADA";
    public static final String CITA_NO_ASISTIO    = "NO_ASISTIO";

    // --- Tipos de cita ---
    public static final String TIPO_PRESENCIAL    = "PRESENCIAL";
    public static final String TIPO_TELECONSULTA  = "TELECONSULTA";

    // --- Estados de usuario ---
    public static final String USUARIO_ACTIVO     = "ACTIVO";
    public static final String USUARIO_INACTIVO   = "INACTIVO";

    // --- Paginación ---
    public static final int PAGE_SIZE_DEFAULT     = 10;
    public static final int PAGE_SIZE_MAX         = 50;

    // --- API prefix ---
    public static final String API_PREFIX         = "/api";
}