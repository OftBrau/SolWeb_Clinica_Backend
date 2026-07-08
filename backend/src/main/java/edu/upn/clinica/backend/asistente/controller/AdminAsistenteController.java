package edu.upn.clinica.backend.asistente.controller;

import edu.upn.clinica.backend.shared.BaseRepository;
import edu.upn.clinica.backend.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/asistentes")
@Tag(name = "Admin Asistentes", description = "Gestion administrativa de asistentes")
@SecurityRequirement(name = "bearerAuth")
public class AdminAsistenteController {

    @Autowired
    private AsistenteQueryHelper queryHelper;

    @GetMapping
    @Operation(summary = "Listar todos los asistentes")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok("Asistentes obtenidos", queryHelper.listarAsistentes()));
    }
}

@Repository
class AsistenteQueryHelper extends BaseRepository {

    public List<Map<String, Object>> listarAsistentes() {
        String sql = "SELECT u.id_usuario, u.nombre, u.apellido, u.email, u.telefono, u.estado " +
                "FROM usuarios u WHERE u.rol = 'ASISTENTE' ORDER BY u.nombre";
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("idUsuario", rs.getInt("id_usuario"));
                m.put("nombre", rs.getString("nombre"));
                m.put("apellido", rs.getString("apellido"));
                m.put("email", rs.getString("email"));
                m.put("telefono", rs.getString("telefono"));
                m.put("estado", rs.getString("estado"));
                lista.add(m);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando asistentes: " + e.getMessage());
        }
        return lista;
    }
}
