package edu.upn.clinica.backend.farmacia.controller;

import edu.upn.clinica.backend.farmacia.dto.CrearVentaRequest;
import edu.upn.clinica.backend.farmacia.dto.CheckoutPublicRequest;
import edu.upn.clinica.backend.farmacia.dto.VentaResponseDTO;
import edu.upn.clinica.backend.farmacia.service.VentaService;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.AppException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmacia/ventas")
@Tag(name = "Farmacia - Ventas", description = "Ventas de farmacia con Mercado Pago")
@SecurityRequirement(name = "bearerAuth")
public class VentaController {

    @Autowired
    private VentaService ventaService;
    @Autowired
    private PacienteRepository pacienteRepository;

    @PostMapping("/checkout")
    @Operation(summary = "Crear venta e iniciar pago con Mercado Pago")
    public ResponseEntity<ApiResponse<VentaResponseDTO>> checkout(
            Authentication auth,
            @Valid @RequestBody CrearVentaRequest request) {
        Integer idPaciente = obtenerIdPaciente(auth);
        VentaResponseDTO venta = ventaService.crearVentaConMercadoPago(idPaciente, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Venta creada. Redirigir a Mercado Pago", venta));
    }

    @GetMapping
    @Operation(summary = "Listar ventas del paciente autenticado")
    public ResponseEntity<ApiResponse<List<VentaResponseDTO>>> misVentas(Authentication auth) {
        Integer idPaciente = obtenerIdPaciente(auth);
        return ResponseEntity.ok(ApiResponse.ok("Ventas del paciente",
                ventaService.listarPorPaciente(idPaciente)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar detalle de una venta")
    public ResponseEntity<ApiResponse<VentaResponseDTO>> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Venta encontrada",
                ventaService.buscarPorId(id)));
    }

    @PostMapping("/checkout-public")
    @Operation(summary = "Checkout público sin login (guest)")
    public ResponseEntity<ApiResponse<VentaResponseDTO>> checkoutPublic(
            @Valid @RequestBody CheckoutPublicRequest request) {
        VentaResponseDTO venta = ventaService.checkoutPublic(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Venta creada. Redirigir a Mercado Pago", venta));
    }

    private Integer obtenerIdPaciente(Authentication auth) {
        String email = auth.getName();
        return pacienteRepository.findByEmail(email)
                .map(p -> p.getIdPaciente())
                .orElseThrow(() -> new AppException("Paciente no encontrado", HttpStatus.NOT_FOUND));
    }
}
