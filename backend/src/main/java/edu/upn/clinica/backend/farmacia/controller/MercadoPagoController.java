package edu.upn.clinica.backend.farmacia.controller;

import edu.upn.clinica.backend.farmacia.service.MercadoPagoService;
import edu.upn.clinica.backend.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/farmacia/pagos")
@Tag(name = "Farmacia - Mercado Pago", description = "Notificaciones de pago de Mercado Pago")
public class MercadoPagoController {

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @PostMapping("/notificacion")
    @Operation(summary = "Webhook de notificación de Mercado Pago")
    public ResponseEntity<ApiResponse<String>> notificacion(@RequestBody Map<String, Object> body) {
        String topic = body.get("topic") != null ? body.get("topic").toString() : null;
        String resourceId = body.get("resource") != null ? body.get("resource").toString() : null;

        if (topic != null && resourceId != null) {
            mercadoPagoService.procesarNotificacion(topic, resourceId);
        }

        return ResponseEntity.ok(ApiResponse.ok("Notificación procesada"));
    }

    @GetMapping("/estado/{paymentId}")
    @Operation(summary = "Consultar estado de un pago")
    public ResponseEntity<ApiResponse<String>> consultarEstado(@PathVariable String paymentId) {
        String estado = mercadoPagoService.consultarEstadoPago(paymentId);
        return ResponseEntity.ok(ApiResponse.ok("Estado: " + estado, estado));
    }
}
