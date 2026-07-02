package edu.upn.clinica.backend.constants;

import java.time.LocalTime;
import java.time.ZoneId;

public final class ClinicConstants {
    private ClinicConstants() {}

    public static final LocalTime CLINIC_DAY_START = LocalTime.of(8, 0);
    public static final LocalTime CLINIC_DAY_END   = LocalTime.of(17, 0);
    public static final int SLOT_DURATION_MINUTES = 30;
    public static final ZoneId TIMEZONE = ZoneId.of("America/Lima");
}
