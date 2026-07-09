package edu.upn.clinica.backend.auth;

import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.shared.EmailService;
import edu.upn.clinica.backend.shared.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Base64;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private DataSource dataSource;

    private static final SecureRandom RANDOM = new SecureRandom();

    public PageResult<UsuarioDTO> listar(int page, int size) {
        List<Usuario> lista = usuarioRepository.findAll(page, size);
        long total = usuarioRepository.count();
        List<UsuarioDTO> dtos = lista.stream().map(UsuarioDTO::new).toList();
        return new PageResult<>(dtos, total, page, size);
    }

    public UsuarioDTO crear(CrearUsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new AppException("El email ya esta registrado", HttpStatus.CONFLICT);
        }

        boolean manualPassword = request.getPassword() != null && !request.getPassword().isBlank();
        String passwordTemp = manualPassword ? request.getPassword() : generarPasswordTemporal();
        String hash = passwordEncoder.encode(passwordTemp);

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(hash);
        usuario.setTelefono(request.getTelefono());
        usuario.setRol(request.getRol());
        usuario.setEstado("ACTIVO");
        usuario.setPasswordDefault(!manualPassword);

        usuario = usuarioRepository.save(usuario);

        if ("ENFERMERO".equals(request.getRol())) {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT IGNORE INTO doctores (id_usuario, especialidad, CMP) VALUES (?, 'Enfermeria', ?)")) {
                ps.setInt(1, usuario.getId());
                ps.setString(2, "ENF-" + String.format("%06d", usuario.getId()));
                ps.executeUpdate();
            } catch (Exception e) {
                throw new RuntimeException("Error creando entrada doctores para enfermero: " + e.getMessage());
            }
        }

        if ("PRACTICANTE".equals(request.getRol())) {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT IGNORE INTO doctores (id_usuario, especialidad, CMP) VALUES (?, 'Practicante', ?)")) {
                ps.setInt(1, usuario.getId());
                ps.setString(2, "PRA-" + String.format("%06d", usuario.getId()));
                ps.executeUpdate();
            } catch (Exception e) {
                throw new RuntimeException("Error creando entrada doctores para practicante: " + e.getMessage());
            }
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT IGNORE INTO practicantes (id_usuario, ciclo) VALUES (?, 1)")) {
                ps.setInt(1, usuario.getId());
                ps.executeUpdate();
            } catch (Exception e) {
                throw new RuntimeException("Error creando entrada practicantes para practicante: " + e.getMessage());
            }
        }

        emailService.enviarCredenciales(
                usuario.getEmail(),
                usuario.getNombre() + " " + usuario.getApellido(),
                passwordTemp
        );

        return new UsuarioDTO(usuario);
    }

    public UsuarioDTO editar(Integer id, EditarUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new AppException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        usuarioRepository.update(id, request.getNombre(), request.getApellido(), request.getTelefono());
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        return new UsuarioDTO(usuario);
    }

    public void cambiarEstado(Integer id, String estado) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new AppException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        if (!"ACTIVO".equals(estado) && !"INACTIVO".equals(estado)) {
            throw new AppException("Estado invalido. Use ACTIVO o INACTIVO");
        }

        usuarioRepository.updateEstado(id, estado);
    }

    public void asignarRol(Integer id, String rol) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new AppException("Usuario no encontrado", HttpStatus.NOT_FOUND));
        usuarioRepository.updateRol(id, rol);

        if ("ENFERMERO".equals(rol)) {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT IGNORE INTO doctores (id_usuario, especialidad, CMP) VALUES (?, 'Enfermeria', ?)")) {
                ps.setInt(1, id);
                ps.setString(2, "ENF-" + String.format("%06d", id));
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("Error creando doctores para enfermero: " + e.getMessage());
            }
        }

        if ("PRACTICANTE".equals(rol)) {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT IGNORE INTO doctores (id_usuario, especialidad, CMP) VALUES (?, 'Practicante', ?)")) {
                ps.setInt(1, id);
                ps.setString(2, "PRA-" + String.format("%06d", id));
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("Error creando doctores para practicante: " + e.getMessage());
            }
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT IGNORE INTO practicantes (id_usuario, ciclo) VALUES (?, 1)")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("Error creando practicantes para practicante: " + e.getMessage());
            }
        }
    }

    private String generarPasswordTemporal() {
        byte[] bytes = new byte[8];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
