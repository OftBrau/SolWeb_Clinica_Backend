package edu.upn.clinica.backend.cita.dto;

// ============================================================
//  CitaPublicaResponse.java
//  Respuesta del POST /api/cita-publica/agendar
// ============================================================
public class CitaPublicaResponse {

    private Integer idCita;
    private String  paciente;
    private String  doctor;
    private String  especialidad;
    private String  fecha;
    private String  hora;
    private String  estado;
    private String  tipo;

    public CitaPublicaResponse() {}

    public CitaPublicaResponse(Integer idCita, String paciente, String doctor,
                                String especialidad, String fecha, String hora,
                                String estado, String tipo) {
        this.idCita      = idCita;
        this.paciente    = paciente;
        this.doctor      = doctor;
        this.especialidad = especialidad;
        this.fecha       = fecha;
        this.hora        = hora;
        this.estado      = estado;
        this.tipo        = tipo;
    }

    public Integer getIdCita()                            { return idCita; }
    public void    setIdCita(Integer idCita)             { this.idCita = idCita; }

    public String  getPaciente()                          { return paciente; }
    public void    setPaciente(String paciente)          { this.paciente = paciente; }

    public String  getDoctor()                            { return doctor; }
    public void    setDoctor(String doctor)              { this.doctor = doctor; }

    public String  getEspecialidad()                      { return especialidad; }
    public void    setEspecialidad(String especialidad)  { this.especialidad = especialidad; }

    public String  getFecha()                             { return fecha; }
    public void    setFecha(String fecha)                { this.fecha = fecha; }

    public String  getHora()                              { return hora; }
    public void    setHora(String hora)                  { this.hora = hora; }

    public String  getEstado()                            { return estado; }
    public void    setEstado(String estado)              { this.estado = estado; }

    public String  getTipo()                              { return tipo; }
    public void    setTipo(String tipo)                  { this.tipo = tipo; }
}