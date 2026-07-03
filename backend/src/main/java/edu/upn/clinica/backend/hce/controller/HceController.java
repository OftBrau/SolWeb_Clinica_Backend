package edu.upn.clinica.backend.hce.controller;

import edu.upn.clinica.backend.hce.model.HistorialItem;
import edu.upn.clinica.backend.hce.service.HceService;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.EmailService;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.AppException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hce")
@Tag(name = "HCE", description = "Historia Clínica Electrónica del paciente")
public class HceController {

    @Autowired private HceService hceService;
    @Autowired private EmailService emailService;
    @Autowired private PacienteRepository pacienteRepository;

    @GetMapping("/documentos")
    @Operation(summary = "Listar historial clínico del paciente autenticado")
    public ResponseEntity<ApiResponse<List<HistorialItem>>> listar() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        var authorities = auth.getAuthorities();
        boolean isAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR") || a.getAuthority().equals("ROLE_DIRECTOR"));
        boolean isDoctor = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        boolean isPracticante = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PRACTICANTE"));
        if (isAdmin) {
            return ResponseEntity.ok(ApiResponse.ok("Historial",
                    hceService.listarTodos()));
        }
        if (isDoctor) {
            return ResponseEntity.ok(ApiResponse.ok("Historial",
                    hceService.listarPorDoctorEmail(email)));
        }
        if (isPracticante) {
            return ResponseEntity.ok(ApiResponse.ok("Historial",
                    hceService.listarPorPracticanteEmail(email)));
        }
        return ResponseEntity.ok(ApiResponse.ok("Historial",
                hceService.listarPorEmail(email)));
    }

    @GetMapping("/documentos/{id}/descargar")
    @Operation(summary = "Descargar documento de la HCE en PDF (CUS_07)")
    public ResponseEntity<byte[]> descargar(@PathVariable Integer id) {
        byte[] pdf = hceService.generarReportePDF(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "hce_documento_" + id + ".pdf");
        headers.setContentLength(pdf.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    @PostMapping("/documentos/{id}/enviar")
    @Operation(summary = "Enviar documento de la HCE en PDF al correo del paciente")
    public ResponseEntity<ApiResponse<Void>> enviarPorEmail(@PathVariable Integer id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        String nombrePaciente = pacienteRepository.findByEmail(email)
                .map(p -> p.getNombre() + " " + p.getApellido())
                .orElse("Paciente");

        byte[] pdf = hceService.generarReportePDF(id);
        emailService.enviarHistorialPDF(email, nombrePaciente, pdf, id.toString());

        return ResponseEntity.ok(ApiResponse.ok("Documento enviado a tu correo: " + email));
    }
}
