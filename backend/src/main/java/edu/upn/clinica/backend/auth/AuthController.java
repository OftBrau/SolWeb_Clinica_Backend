package edu.upn.clinica.backend.auth;

import edu.upn.clinica.backend.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Autenticación y gestión de perfil propio")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login exitoso", response));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario (DOCTOR o PACIENTE)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(
            @Valid @RequestBody RegisterRequest request) {
        Map<String, Object> result = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registro exitoso", result));
    }

    @GetMapping("/perfil")
    @Operation(summary = "Obtener datos del usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UsuarioDTO>> perfil() {
        String email = getEmail();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(ApiResponse.ok("Perfil obtenido", new UsuarioDTO(usuario)));
    }

    @PutMapping("/perfil")
    @Operation(summary = "Actualizar datos personales del usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UsuarioDTO>> actualizarPerfil(
            @Valid @RequestBody PerfilRequest request) {
        String email = getEmail();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuarioService.editar(usuario.getId(), new EditarUsuarioRequest(
                request.getNombre(), request.getApellido(), request.getTelefono()));
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefono(request.getTelefono());
        return ResponseEntity.ok(ApiResponse.ok("Perfil actualizado", new UsuarioDTO(usuario)));
    }

    @PutMapping("/cambiar-password")
    @Operation(summary = "Cambiar contraseña del usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> cambiarPassword(
            @Valid @RequestBody CambiarPasswordRequest request) {
        String email = getEmail();
        authService.cambiarPassword(email, request);
        return ResponseEntity.ok(ApiResponse.ok("Contraseña actualizada"));
    }

    private String getEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}

class PerfilRequest {
    @NotBlank private String nombre;
    @NotBlank private String apellido;
    private String telefono;
    public String getNombre() { return nombre; }
    public void setNombre(String v) { nombre = v; }
    public String getApellido() { return apellido; }
    public void setApellido(String v) { apellido = v; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String v) { telefono = v; }
}

class CambiarPasswordRequest {
    @NotBlank @Size(min = 6) private String passwordActual;
    @NotBlank @Size(min = 6) private String passwordNueva;
    public String getPasswordActual() { return passwordActual; }
    public void setPasswordActual(String v) { passwordActual = v; }
    public String getPasswordNueva() { return passwordNueva; }
    public void setPasswordNueva(String v) { passwordNueva = v; }
}
