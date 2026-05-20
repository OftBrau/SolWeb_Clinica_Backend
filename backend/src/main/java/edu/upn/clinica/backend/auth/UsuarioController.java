package edu.upn.clinica.backend.auth;

import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/usuarios")
@Tag(name = "Usuarios", description = "Administración de usuarios y asignación de roles")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar usuarios con paginación")
    public ResponseEntity<ApiResponse<PageResult<UsuarioDTO>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok("Usuarios obtenidos",
                usuarioService.listar(page, size)));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario")
    public ResponseEntity<ApiResponse<UsuarioDTO>> crear(
            @Valid @RequestBody CrearUsuarioRequest request) {
        UsuarioDTO dto = usuarioService.crear(request);
        return ResponseEntity.ok(ApiResponse.ok("Usuario creado correctamente", dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar datos de usuario")
    public ResponseEntity<ApiResponse<UsuarioDTO>> editar(
            @PathVariable Integer id,
            @Valid @RequestBody EditarUsuarioRequest request) {
        UsuarioDTO dto = usuarioService.editar(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado correctamente", dto));
    }

    @PatchMapping("/{id}/rol")
    @Operation(summary = "Asignar rol a usuario")
    public ResponseEntity<ApiResponse<Void>> asignarRol(
            @PathVariable Integer id,
            @Valid @RequestBody RolRequest request) {
        usuarioService.asignarRol(id, request.getRol());
        return ResponseEntity.ok(ApiResponse.ok("Rol actualizado correctamente"));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Activar o desactivar usuario")
    public ResponseEntity<ApiResponse<Void>> cambiarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody EstadoRequest request) {
        usuarioService.cambiarEstado(id, request.getEstado());
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado correctamente"));
    }
}
