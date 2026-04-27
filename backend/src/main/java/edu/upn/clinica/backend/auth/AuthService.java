package edu.upn.clinica.backend.auth;

import edu.upn.clinica.backend.security.JwtUtil;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// ============================================================
//  AuthService.java
//  Lógica de autenticación
// ============================================================
@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {

        // 1. Buscar usuario por email
        Usuario usuario = usuarioRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                    new AppException("Credenciales incorrectas", HttpStatus.UNAUTHORIZED));

        // 2. Verificar que la cuenta esté activa
        if (!"ACTIVO".equals(usuario.getEstado())) {
            throw new AppException("Cuenta inactiva o suspendida", HttpStatus.UNAUTHORIZED);
        }

        // 3. Verificar contraseña con BCrypt
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new AppException("Credenciales incorrectas", HttpStatus.UNAUTHORIZED);
        }

        // 4. Generar token JWT
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());

        // 5. Devolver respuesta con token + datos básicos
        return new LoginResponse(
                token,
                usuario.getRol(),
                usuario.getNombre() + " " + usuario.getApellido(),
                usuario.getEmail()
        );
    }
}