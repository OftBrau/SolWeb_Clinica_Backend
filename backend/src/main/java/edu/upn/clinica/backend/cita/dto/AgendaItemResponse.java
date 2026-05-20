package edu.upn.clinica.backend.cita.dto;

public class AgendaItemResponse {

    private Integer idCita;
    private String  hora;
    private String  paciente;
    private String  tipo;
    private String  estado;
    private String  motivo;
    private Integer idPaciente;
    private String  doctor;
    private Integer idDoctor;

    public AgendaItemResponse() {}

    public Integer getIdCita()        { return idCita;     }
    public void   setIdCita(Integer v){ this.idCita = v;   }

    public String getHora()           { return hora;       }
    public void   setHora(String v)   { this.hora = v;     }

    public String getPaciente()       { return paciente;   }
    public void   setPaciente(String v){ this.paciente = v;}

    public String getTipo()           { return tipo;       }
    public void   setTipo(String v)   { this.tipo = v;     }

    public String getEstado()         { return estado;     }
    public void   setEstado(String v) { this.estado = v;   }

    public String getMotivo()         { return motivo;     }
    public void   setMotivo(String v) { this.motivo = v;   }

    public Integer getIdPaciente()    { return idPaciente; }
    public void   setIdPaciente(Integer v){ this.idPaciente = v;}

    public String getDoctor()         { return doctor;     }
    public void   setDoctor(String v) { this.doctor = v;   }

    public Integer getIdDoctor()      { return idDoctor;   }
    public void   setIdDoctor(Integer v){ this.idDoctor = v;}
}
