package edu.upn.clinica.backend.auth;

import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.shared.EmailService;
import edu.upn.clinica.backend.shared.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
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

    private static final SecureRandom RANDOM = new SecureRandom();

    public PageResult<UsuarioDTO> listar(int page, int size) {
        List<Usuario> lista = usuarioRepository.findAll(page, size);
        long total = usuarioRepository.count();
        List<UsuarioDTO> dtos = lista.stream().map(UsuarioDTO::new).toList();
        return new PageResult<>(dtos, total, page, size);
    }

    public UsuarioDTO crear(CrearUsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new AppException("El email ya está registrado", HttpStatus.CONFLICT);
        }

        String passwordTemp = generarPasswordTemporal();
        String hash = passwordEncoder.encode(passwordTemp);

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(hash);
        usuario.setTelefono(request.getTelefono());
        usuario.setRol(request.getRol());
        usuario.setEstado("ACTIVO");

        usuario = usuarioRepository.save(usuario);

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
            throw new AppException("Estado inválido. Use ACTIVO o INACTIVO");
        }

        usuarioRepository.updateEstado(id, estado);
    }

    public void asignarRol(Integer id, String rol) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new AppException("Usuario no encontrado", HttpStatus.NOT_FOUND));
        usuarioRepository.updateRol(id, rol);
    }

    private String generarPasswordTemporal() {
        byte[] bytes = new byte[8];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
