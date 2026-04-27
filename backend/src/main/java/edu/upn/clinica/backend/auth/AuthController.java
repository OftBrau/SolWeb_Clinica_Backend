package edu.upn.clinica.backend.auth;

import edu.upn.clinica.backend.shared.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ============================================================
//  AuthController.java
//  Endpoint: POST /api/auth/login
//  Público — no requiere token JWT
// ============================================================
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.ok("Login exitoso", response)
        );
    }
}