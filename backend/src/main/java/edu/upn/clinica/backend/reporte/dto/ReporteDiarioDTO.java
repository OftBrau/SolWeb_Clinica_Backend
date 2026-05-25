package edu.upn.clinica.backend.reporte.dto;

import java.util.List;

public class ReporteDiarioDTO {
    private String fecha;
    private ResumenGeneralDTO resumen;
    private List<CitasPorEspecialidadDTO> porEspecialidad;
    private List<CitasPorDoctorDTO> porDoctor;

    public String getFecha() { return fecha; }
    public void setFecha(String v) { this.fecha = v; }

    public ResumenGeneralDTO getResumen() { return resumen; }
    public void setResumen(ResumenGeneralDTO v) { this.resumen = v; }

    public List<CitasPorEspecialidadDTO> getPorEspecialidad() { return porEspecialidad; }
    public void setPorEspecialidad(List<CitasPorEspecialidadDTO> v) { this.porEspecialidad = v; }

    public List<CitasPorDoctorDTO> getPorDoctor() { return porDoctor; }
    public void setPorDoctor(List<CitasPorDoctorDTO> v) { this.porDoctor = v; }

    public static class ResumenGeneralDTO {
        private long totalCitas;
        private long confirmadas;
        private long atendidas;
        private long canceladas;
        private long noAsistieron;
        private long pacientesAtendidos;
        private long doctoresActivos;

        public long getTotalCitas() { return totalCitas; }
        public void setTotalCitas(long v) { this.totalCitas = v; }
        public long getConfirmadas() { return confirmadas; }
        public void setConfirmadas(long v) { this.confirmadas = v; }
        public long getAtendidas() { return atendidas; }
        public void setAtendidas(long v) { this.atendidas = v; }
        public long getCanceladas() { return canceladas; }
        public void setCanceladas(long v) { this.canceladas = v; }
        public long getNoAsistieron() { return noAsistieron; }
        public void setNoAsistieron(long v) { this.noAsistieron = v; }
        public long getPacientesAtendidos() { return pacientesAtendidos; }
        public void setPacientesAtendidos(long v) { this.pacientesAtendidos = v; }
        public long getDoctoresActivos() { return doctoresActivos; }
        public void setDoctoresActivos(long v) { this.doctoresActivos = v; }
    }

    public static class CitasPorEspecialidadDTO {
        private String especialidad;
        private long cantidad;

        public String getEspecialidad() { return especialidad; }
        public void setEspecialidad(String v) { this.especialidad = v; }
        public long getCantidad() { return cantidad; }
        public void setCantidad(long v) { this.cantidad = v; }
    }

    public static class CitasPorDoctorDTO {
        private String nombreDoctor;
        private String especialidad;
        private long cantidad;

        public String getNombreDoctor() { return nombreDoctor; }
        public void setNombreDoctor(String v) { this.nombreDoctor = v; }
        public String getEspecialidad() { return especialidad; }
        public void setEspecialidad(String v) { this.especialidad = v; }
        public long getCantidad() { return cantidad; }
        public void setCantidad(long v) { this.cantidad = v; }
    }
}
