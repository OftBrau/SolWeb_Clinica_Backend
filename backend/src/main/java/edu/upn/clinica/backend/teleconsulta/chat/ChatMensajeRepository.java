package edu.upn.clinica.backend.teleconsulta.chat;

import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ChatMensajeRepository extends BaseRepository {

    public ChatMessage save(ChatMessage msg) {
        String sql = "INSERT INTO chat_mensajes (id_teleconsulta, usuario, email, texto, rol, hora) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, msg.getConsultaId());
            ps.setString(2, msg.getUsuario());
            ps.setString(3, msg.getEmail());
            ps.setString(4, msg.getTexto());
            ps.setString(5, msg.getRol());
            ps.setString(6, msg.getHora());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) msg.setId(rs.getLong(1));
            return msg;
        } catch (Exception e) {
            throw new RuntimeException("Error guardando mensaje: " + e.getMessage());
        }
    }

    public List<ChatMessage> findByTeleconsulta(Integer idTeleconsulta) {
        String sql = "SELECT id_mensaje, id_teleconsulta, usuario, email, texto, rol, hora, created_at FROM chat_mensajes WHERE id_teleconsulta = ? ORDER BY created_at ASC";
        List<ChatMessage> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTeleconsulta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando mensajes: " + e.getMessage());
        }
        return list;
    }

    private ChatMessage mapRow(ResultSet rs) throws Exception {
        ChatMessage msg = new ChatMessage();
        msg.setId(rs.getLong("id_mensaje"));
        msg.setConsultaId(rs.getInt("id_teleconsulta"));
        msg.setUsuario(rs.getString("usuario"));
        msg.setEmail(rs.getString("email"));
        msg.setTexto(rs.getString("texto"));
        msg.setRol(rs.getString("rol"));
        msg.setHora(rs.getString("hora"));
        return msg;
    }
}
