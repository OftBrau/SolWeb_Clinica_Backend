package edu.upn.clinica.backend.reporte.model;

public class ReporteOperativo {
    private String fecha;
    private long totalCitas;
    private long citasConfirmadas;
    private long citasAtendidas;
    private long citasCanceladas;
    private long citasNoAsistio;
    private long pacientesAtendidos;
    private long doctoresActivos;
    private long consultasRealizadas;

    public ReporteOperativo() {}

    public String getFecha() { return fecha; }
    public void setFecha(String v) { this.fecha = v; }

    public long getTotalCitas() { return totalCitas; }
    public void setTotalCitas(long v) { this.totalCitas = v; }

    public long getCitasConfirmadas() { return citasConfirmadas; }
    public void setCitasConfirmadas(long v) { this.citasConfirmadas = v; }

    public long getCitasAtendidas() { return citasAtendidas; }
    public void setCitasAtendidas(long v) { this.citasAtendidas = v; }

    public long getCitasCanceladas() { return citasCanceladas; }
    public void setCitasCanceladas(long v) { this.citasCanceladas = v; }

    public long getCitasNoAsistio() { return citasNoAsistio; }
    public void setCitasNoAsistio(long v) { this.citasNoAsistio = v; }

    public long getPacientesAtendidos() { return pacientesAtendidos; }
    public void setPacientesAtendidos(long v) { this.pacientesAtendidos = v; }

    public long getDoctoresActivos() { return doctoresActivos; }
    public void setDoctoresActivos(long v) { this.doctoresActivos = v; }

    public long getConsultasRealizadas() { return consultasRealizadas; }
    public void setConsultasRealizadas(long v) { this.consultasRealizadas = v; }
}
