package edu.upn.clinica.backend.asistente.dto;

import java.util.List;

public class AgendaDiariaResponse {

    private String fecha;
    private int    totalCitas;
    private int    confirmadas;
    private int    pendientes;
    private int    atendidas;
    private int    canceladas;
    private List<AgendaItem> items;

    public AgendaDiariaResponse() {}

    public String getFecha() { return fecha; }
    public void   setFecha(String fecha) { this.fecha = fecha; }

    public int    getTotalCitas() { return totalCitas; }
    public void   setTotalCitas(int totalCitas) { this.totalCitas = totalCitas; }

    public int    getConfirmadas() { return confirmadas; }
    public void   setConfirmadas(int confirmadas) { this.confirmadas = confirmadas; }

    public int    getPendientes() { return pendientes; }
    public void   setPendientes(int pendientes) { this.pendientes = pendientes; }

    public int    getAtendidas() { return atendidas; }
    public void   setAtendidas(int atendidas) { this.atendidas = atendidas; }

    public int    getCanceladas() { return canceladas; }
    public void   setCanceladas(int canceladas) { this.canceladas = canceladas; }

    public List<AgendaItem> getItems() { return items; }
    public void              setItems(List<AgendaItem> items) { this.items = items; }

    public static class AgendaItem {
        private Integer idCita;
        private String  hora;
        private String  paciente;
        private String  doctor;
        private String  especialidad;
        private String  consultorio;
        private String  estado;
        private String  tipoReserva;
        private String  tipo;

        public Integer getIdCita() { return idCita; }
        public void    setIdCita(Integer idCita) { this.idCita = idCita; }

        public String  getHora() { return hora; }
        public void    setHora(String hora) { this.hora = hora; }

        public String  getPaciente() { return paciente; }
        public void    setPaciente(String paciente) { this.paciente = paciente; }

        public String  getDoctor() { return doctor; }
        public void    setDoctor(String doctor) { this.doctor = doctor; }

        public String  getEspecialidad() { return especialidad; }
        public void    setEspecialidad(String especialidad) { this.especialidad = especialidad; }

        public String  getConsultorio() { return consultorio; }
        public void    setConsultorio(String consultorio) { this.consultorio = consultorio; }

        public String  getEstado() { return estado; }
        public void    setEstado(String estado) { this.estado = estado; }

        public String  getTipoReserva() { return tipoReserva; }
        public void    setTipoReserva(String tipoReserva) { this.tipoReserva = tipoReserva; }

        public String  getTipo() { return tipo; }
        public void    setTipo(String tipo) { this.tipo = tipo; }
    }
}
