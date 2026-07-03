package edu.upn.clinica.backend.farmacia.controller;

import edu.upn.clinica.backend.farmacia.dto.MedicamentoDTO;
import edu.upn.clinica.backend.farmacia.service.MedicamentoService;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmacia/medicamentos")
@Tag(name = "Farmacia - Medicamentos", description = "Catálogo de medicamentos")
@SecurityRequirement(name = "bearerAuth")
public class MedicamentoController {

    @Autowired
    private MedicamentoService medicamentoService;

    @GetMapping
    @Operation(summary = "Listar medicamentos con paginación")
    public ResponseEntity<ApiResponse<PageResult<MedicamentoDTO>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok("Medicamentos obtenidos",
                medicamentoService.listar(page, size)));
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar medicamentos activos con stock")
    public ResponseEntity<ApiResponse<List<MedicamentoDTO>>> listarActivos() {
        return ResponseEntity.ok(ApiResponse.ok("Medicamentos activos",
                medicamentoService.listarActivos()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar medicamento por ID")
    public ResponseEntity<ApiResponse<MedicamentoDTO>> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Medicamento encontrado",
                medicamentoService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo medicamento")
    public ResponseEntity<ApiResponse<MedicamentoDTO>> crear(
            @Valid @RequestBody MedicamentoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Medicamento registrado", medicamentoService.crear(dto)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar medicamento")
    public ResponseEntity<ApiResponse<MedicamentoDTO>> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody MedicamentoDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Medicamento actualizado",
                medicamentoService.actualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar medicamento (soft delete)")
    public ResponseEntity<ApiResponse<?>> desactivar(@PathVariable Integer id) {
        medicamentoService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Medicamento desactivado"));
    }
}
