package edu.upn.clinica.backend.scheduling.dto;

public enum AvailabilityErrorCode {
    SLOT_ALREADY_BOOKED,
    SLOT_NOT_AVAILABLE,
    INVALID_TIME_RANGE,
    OVERLAP_DETECTED,
    APPOINTMENT_NOT_FOUND,
    UNAUTHORIZED
}
