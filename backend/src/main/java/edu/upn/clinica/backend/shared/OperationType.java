package edu.upn.clinica.backend.shared;

public enum OperationType {
    REGISTRO("registro", "registrado exitosamente", "Error al registrar"),
    ACTUALIZACION("actualizacion", "actualizado exitosamente", "Error al actualizar"),
    ELIMINACION("eliminacion", "eliminado exitosamente", "Error al eliminar"),
    ACEPTACION("aceptacion", "aceptado exitosamente", "Error al aceptar"),
    EJECUCION("ejecucion", "ejecutado exitosamente", "Error al ejecutar");

    private final String code;
    private final String successMessage;
    private final String errorMessage;

    OperationType(String code, String successMessage, String errorMessage) {
        this.code = code;
        this.successMessage = successMessage;
        this.errorMessage = errorMessage;
    }

    public String getCode() { return code; }
    public String getSuccessMessage() { return successMessage; }
    public String getErrorMessage() { return errorMessage; }
}
