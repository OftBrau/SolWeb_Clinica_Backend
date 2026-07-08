package edu.upn.clinica.backend.auth;

import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.security.JwtUtil;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private DataSource dataSource;

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                    new AppException("Credenciales incorrectas", HttpStatus.UNAUTHORIZED));

        if (!"ACTIVO".equals(usuario.getEstado())) {
            throw new AppException("Cuenta inactiva o suspendida", HttpStatus.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new AppException("Credenciales incorrectas", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());

        return new LoginResponse(
                token,
                usuario.getRol(),
                usuario.getNombre() + " " + usuario.getApellido(),
                usuario.getEmail(),
                usuario.isPasswordDefault()
        );
    }

    @Transactional
    public Map<String, Object> register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new AppException("El email ya está registrado", HttpStatus.CONFLICT);
        }

        String role = request.getRole().toUpperCase();
        if (!"DOCTOR".equals(role) && !"PACIENTE".equals(role)
                && !"ENFERMERO".equals(role) && !"ASISTENTE".equals(role)) {
            throw new AppException("Rol invalido. Use DOCTOR, PACIENTE, ENFERMERO o ASISTENTE", HttpStatus.BAD_REQUEST);
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getFirstName());
        usuario.setApellido(request.getLastName());
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setTelefono("");
        usuario.setRol(role);
        usuario.setEstado("ACTIVO");
        usuario.setPasswordDefault(false);
        usuario = usuarioRepository.save(usuario);

        if ("DOCTOR".equals(role)) {
            if (request.getSpecialty() == null || request.getSpecialty().isBlank()) {
                throw new AppException("La especialidad es obligatoria para doctores", HttpStatus.BAD_REQUEST);
            }
            String sql = "INSERT INTO doctores (id_usuario, especialidad, CMP) VALUES (?, ?, ?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, usuario.getId());
                ps.setString(2, request.getSpecialty());
                ps.setString(3, request.getLicenseNumber() != null ? request.getLicenseNumber() :
                        "CMP-" + String.format("%06d", usuario.getId()));
                ps.executeUpdate();
            } catch (Exception e) {
                throw new RuntimeException("Error creando doctor: " + e.getMessage());
            }
        } else if ("PACIENTE".equals(role)) {
            String sql = "INSERT INTO pacientes (id_usuario, fecha_nacimiento, genero) VALUES (?, ?, ?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, usuario.getId());
                ps.setDate(2, java.sql.Date.valueOf("2000-01-01"));
                ps.setString(3, "OTRO");
                ps.executeUpdate();
            } catch (Exception e) {
                throw new RuntimeException("Error creando paciente: " + e.getMessage());
            }
        } else if ("ENFERMERO".equals(role)) {
            String sql = "INSERT INTO doctores (id_usuario, especialidad, CMP) VALUES (?, 'Enfermería', ?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, usuario.getId());
                ps.setString(2, "ENF-" + String.format("%06d", usuario.getId()));
                ps.executeUpdate();
            } catch (Exception e) {
                throw new RuntimeException("Error creando enfermero: " + e.getMessage());
            }
        }

        String token = jwtUtil.generateToken(usuario.getEmail(), role);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", Map.of(
                "id", usuario.getId().toString(),
                "email", usuario.getEmail(),
                "firstName", usuario.getNombre(),
                "lastName", usuario.getApellido(),
                "role", role
        ));
        return result;
    }

    public void cambiarPassword(String email, CambiarPasswordRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPasswordHash())) {
            throw new AppException("La contraseña actual no es correcta", HttpStatus.BAD_REQUEST);
        }

        String hash = passwordEncoder.encode(request.getPasswordNueva());
        usuarioRepository.updatePassword(usuario.getId(), hash);
    }
}