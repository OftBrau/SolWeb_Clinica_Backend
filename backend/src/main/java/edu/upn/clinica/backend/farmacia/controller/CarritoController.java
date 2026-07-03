package edu.upn.clinica.backend.farmacia.controller;

import edu.upn.clinica.backend.farmacia.dto.CarritoItemDTO;
import edu.upn.clinica.backend.farmacia.service.CarritoService;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.AppException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/farmacia/carrito")
@Tag(name = "Farmacia - Carrito", description = "Carrito de compras de farmacia")
@SecurityRequirement(name = "bearerAuth")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;
    @Autowired
    private PacienteRepository pacienteRepository;

    @GetMapping
    @Operation(summary = "Listar items del carrito")
    public ResponseEntity<ApiResponse<List<CarritoItemDTO>>> listar(Authentication auth) {
        Integer idPaciente = obtenerIdPaciente(auth);
        return ResponseEntity.ok(ApiResponse.ok("Carrito obtenido", carritoService.listar(idPaciente)));
    }

    @GetMapping("/count")
    @Operation(summary = "Contar items del carrito")
    public ResponseEntity<ApiResponse<Integer>> contar(Authentication auth) {
        Integer idPaciente = obtenerIdPaciente(auth);
        return ResponseEntity.ok(ApiResponse.ok("Total items", carritoService.contar(idPaciente)));
    }

    @PostMapping
    @Operation(summary = "Agregar producto al carrito")
    public ResponseEntity<ApiResponse<CarritoItemDTO>> agregar(
            Authentication auth,
            @RequestBody Map<String, Object> body) {
        Integer idPaciente = obtenerIdPaciente(auth);
        Integer idMedicamento = (Integer) body.get("idMedicamento");
        int cantidad = body.get("cantidad") != null ? ((Number) body.get("cantidad")).intValue() : 1;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Agregado al carrito",
                        carritoService.agregar(idPaciente, idMedicamento, cantidad)));
    }

    @PutMapping("/{idCarrito}")
    @Operation(summary = "Actualizar cantidad de un item")
    public ResponseEntity<ApiResponse<CarritoItemDTO>> actualizar(
            Authentication auth,
            @PathVariable Integer idCarrito,
            @RequestBody Map<String, Integer> body) {
        Integer idPaciente = obtenerIdPaciente(auth);
        int cantidad = body.getOrDefault("cantidad", 1);
        CarritoItemDTO dto = carritoService.actualizarCantidad(idPaciente, idCarrito, cantidad);
        return ResponseEntity.ok(ApiResponse.ok("Carrito actualizado", dto));
    }

    @DeleteMapping("/{idCarrito}")
    @Operation(summary = "Eliminar item del carrito")
    public ResponseEntity<ApiResponse<?>> eliminar(@PathVariable Integer idCarrito) {
        carritoService.eliminar(idCarrito);
        return ResponseEntity.ok(ApiResponse.ok("Item eliminado"));
    }

    @DeleteMapping
    @Operation(summary = "Vaciar carrito completo")
    public ResponseEntity<ApiResponse<?>> vaciar(Authentication auth) {
        Integer idPaciente = obtenerIdPaciente(auth);
        carritoService.vaciar(idPaciente);
        return ResponseEntity.ok(ApiResponse.ok("Carrito vaciado"));
    }

    private Integer obtenerIdPaciente(Authentication auth) {
        String email = auth.getName();
        return pacienteRepository.findByEmail(email)
                .map(p -> p.getIdPaciente())
                .orElseThrow(() -> new AppException("Paciente no encontrado", HttpStatus.NOT_FOUND));
    }
}
