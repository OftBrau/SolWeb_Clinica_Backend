package edu.upn.clinica.backend.teleconsulta.notificacion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificacionDTO {
    private String tipo;
    private String mensaje;
    private Integer teleconsultaId;
    private String timestamp;

    public NotificacionDTO() {}

    public NotificacionDTO(String tipo, String mensaje, Integer teleconsultaId) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.teleconsultaId = teleconsultaId;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Integer getTeleconsultaId() { return teleconsultaId; }
    public void setTeleconsultaId(Integer teleconsultaId) { this.teleconsultaId = teleconsultaId; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
