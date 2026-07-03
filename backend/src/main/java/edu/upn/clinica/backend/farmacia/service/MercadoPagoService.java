package edu.upn.clinica.backend.farmacia.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.upn.clinica.backend.farmacia.dto.DetalleVentaDTO;
import edu.upn.clinica.backend.farmacia.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class MercadoPagoService {

    @Value("${mercadopago.access.token:}")
    private String accessToken;

    @Value("${mercadopago.public.key:}")
    private String publicKey;

    @Value("${mercadopago.back.url.success:http://localhost:4200/farmacia/pago-exitoso}")
    private String successUrl;

    @Value("${mercadopago.back.url.failure:http://localhost:4200/farmacia/pago-fallido}")
    private String failureUrl;

    @Value("${mercadopago.back.url.pending:http://localhost:4200/farmacia/pago-pendiente}")
    private String pendingUrl;

    @Value("${mercadopago.notification.url:}")
    private String notificationUrl;

    @Autowired
    private VentaRepository ventaRepository;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String MP_API = "https://api.mercadopago.com";

    public MercadoPagoService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoService.class);

    public String crearPreferencia(Integer idVenta, List<DetalleVentaDTO> detalles) {
        if (accessToken == null || accessToken.isEmpty()) {
            log.warn("MercadoPago no configurado (access token vacio)");
            return null;
        }
        try {
            return crearPreferenciaInternal(idVenta, detalles);
        } catch (Exception e) {
            log.error("Error creando preferencia MercadoPago: {}", e.getMessage(), e);
            return null;
        }
    }

    private String crearPreferenciaInternal(Integer idVenta, List<DetalleVentaDTO> detalles) throws Exception {
        ObjectNode preference = objectMapper.createObjectNode();

            ArrayNode items = objectMapper.createArrayNode();
            for (DetalleVentaDTO d : detalles) {
                ObjectNode item = objectMapper.createObjectNode();
                item.put("title", d.getNombreComercial() != null ? d.getNombreComercial() : "Medicamento");
                item.put("quantity", d.getCantidad());
                item.put("unit_price", d.getPrecioUnitario().doubleValue());
                item.put("currency_id", "PEN");
                items.add(item);
            }
            preference.set("items", items);

            ObjectNode backUrls = objectMapper.createObjectNode();
            backUrls.put("success", successUrl);
            backUrls.put("failure", failureUrl);
            backUrls.put("pending", pendingUrl);
            preference.set("back_urls", backUrls);

            preference.put("external_reference", "VENTA-" + idVenta);

            if (notificationUrl != null && !notificationUrl.isEmpty()) {
                preference.put("notification_url", notificationUrl);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(preference), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    MP_API + "/checkout/preferences", request, String.class);

            JsonNode json = objectMapper.readTree(response.getBody());
            String preferenceId = json.get("id").asText();
            String initPoint = json.get("init_point").asText();

            ventaRepository.updatePreferenciaMp(idVenta, preferenceId);

            return initPoint;
    }

    public String consultarEstadoPago(String paymentId) {
        if (accessToken == null || accessToken.isEmpty()) {
            return "DESCONOCIDO";
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    MP_API + "/v1/payments/" + paymentId,
                    HttpMethod.GET, request, String.class);

            JsonNode json = objectMapper.readTree(response.getBody());
            String status = json.get("status").asText();

            if (json.has("external_reference")) {
                String externalRef = json.get("external_reference").asText();
                if (externalRef.startsWith("VENTA-")) {
                    Integer idVenta = Integer.parseInt(externalRef.substring(6));
                    String estadoInterno = mapStatus(status);
                    ventaRepository.updateEstado(idVenta, estadoInterno, paymentId);
                }
            }

            return status;

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public void procesarNotificacion(String topic, String resourceId) {
        if ("payment".equals(topic)) {
            consultarEstadoPago(resourceId);
        } else if ("merchant_order".equals(topic)) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
                HttpEntity<Void> request = new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        MP_API + "/merchant_orders/" + resourceId,
                        HttpMethod.GET, request, String.class);

                JsonNode order = objectMapper.readTree(response.getBody());
                if (order.has("payments") && order.get("payments").isArray()
                        && order.get("payments").size() > 0) {
                    String paymentId = order.get("payments").get(0).get("id").asText();
                    consultarEstadoPago(paymentId);
                }
            } catch (Exception e) {
                // log error, no lanzar
            }
        }
    }

    private String mapStatus(String mpStatus) {
        return switch (mpStatus.toLowerCase()) {
            case "approved" -> "PAGADO";
            case "rejected", "cancelled", "null" -> "CANCELADO";
            case "in_process", "pending", "authorized" -> "PENDIENTE";
            default -> "PENDIENTE";
        };
    }
}
