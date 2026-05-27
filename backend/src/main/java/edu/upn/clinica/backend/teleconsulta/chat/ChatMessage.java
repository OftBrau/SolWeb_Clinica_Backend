package edu.upn.clinica.backend.teleconsulta.chat;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatMessage {
    private Long id;
    private Integer consultaId;
    private String usuario;
    private String email;
    private String texto;
    private String rol;
    private String hora;

    public ChatMessage() {}

    public ChatMessage(Integer consultaId, String usuario, String email, String texto, String rol) {
        this.id = System.currentTimeMillis();
        this.consultaId = consultaId;
        this.usuario = usuario;
        this.email = email;
        this.texto = texto;
        this.rol = rol;
        this.hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getConsultaId() { return consultaId; }
    public void setConsultaId(Integer consultaId) { this.consultaId = consultaId; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
}
