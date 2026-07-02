package edu.upn.clinica.backend.enums;

public enum DayOfWeek {
    LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO;

    public static DayOfWeek from(java.time.DayOfWeek dow) {
        return switch (dow) {
            case MONDAY    -> LUNES;
            case TUESDAY   -> MARTES;
            case WEDNESDAY -> MIERCOLES;
            case THURSDAY  -> JUEVES;
            case FRIDAY    -> VIERNES;
            case SATURDAY  -> SABADO;
            case SUNDAY    -> DOMINGO;
        };
    }
}
