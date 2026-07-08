package edu.upn.clinica.backend.pago.controller;

import edu.upn.clinica.backend.pago.dto.PagoCitaResponse;
import edu.upn.clinica.backend.pago.repository.PagoCitaRepository;
import edu.upn.clinica.backend.pago.service.PagoCitaService;
import edu.upn.clinica.backend.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pagos/citas")
@Tag(name = "Pagos de Citas", description = "Gestion de pagos MercadoPago para citas con especialista")
public class PagoCitaController {

    @Autowired
    private PagoCitaService pagoCitaService;

    @Autowired
    private PagoCitaRepository pagoCitaRepository;

    @PostMapping("/crear")
    @Operation(summary = "Crear un pago MercadoPago para cita especialista")
    public ResponseEntity<ApiResponse<PagoCitaResponse>> crearPago(@RequestBody Map<String, Object> body) {
        Integer idCita = body.get("idCita") != null ? ((Number) body.get("idCita")).intValue() : null;
        BigDecimal monto = body.get("monto") != null ? new BigDecimal(body.get("monto").toString()) : null;
        String metodoPago = (String) body.getOrDefault("metodoPago", "MERCADOPAGO");

        if (idCita == null || monto == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("idCita y monto son obligatorios"));
        }

        PagoCitaResponse response = pagoCitaService.crearPago(idCita, monto, metodoPago);
        return ResponseEntity.ok(ApiResponse.ok("Preferencia de pago creada", response));
    }

    @GetMapping("/cita/{idCita}")
    @Operation(summary = "Consultar estado de pago de una cita")
    public ResponseEntity<ApiResponse<PagoCitaResponse>> obtenerPago(@PathVariable Integer idCita) {
        return ResponseEntity.ok(ApiResponse.ok("Estado de pago",
                pagoCitaService.obtenerPagoPorCita(idCita)));
    }

    @GetMapping("/historial/{idPaciente}")
    @Operation(summary = "Historial de pagos de un paciente")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> historial(@PathVariable Integer idPaciente) {
        List<Map<String, Object>> pagos = pagoCitaRepository.findByPaciente(idPaciente).stream()
                .map(p -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("idPago", p.getIdPago());
                    m.put("idCita", p.getIdCita());
                    m.put("monto", p.getMonto());
                    m.put("metodoPago", p.getMetodoPago());
                    m.put("estadoPago", p.getEstadoPago());
                    m.put("referenciaMp", p.getReferenciaMp());
                    m.put("fechaPago", p.getFechaPago() != null ? p.getFechaPago().toString() : null);
                    return m;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok("Historial de pagos", pagos));
    }

    @PostMapping("/notificacion")
    @Operation(summary = "Webhook de notificacion de MercadoPago (publico)")
    public ResponseEntity<ApiResponse<Void>> notificacionMercadoPago(@RequestBody Map<String, Object> body) {
        try {
            Object dataObj = body.get("data");
            String resourceId = null;
            if (dataObj instanceof Map) {
                resourceId = (String) ((Map<?, ?>) dataObj).get("id");
            } else if (dataObj instanceof String) {
                resourceId = (String) dataObj;
            }
            if (resourceId != null) {
                pagoCitaService.confirmarPago(resourceId, "procesando");
            }
        } catch (Exception e) {
            System.err.println("Error procesando notificacion MP: " + e.getMessage());
        }
        return ResponseEntity.ok(ApiResponse.ok("Notificacion recibida"));
    }
}
