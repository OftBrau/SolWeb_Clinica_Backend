package edu.upn.clinica.backend.pago.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.upn.clinica.backend.pago.dto.PagoCitaResponse;
import edu.upn.clinica.backend.pago.model.PagoCita;
import edu.upn.clinica.backend.pago.repository.PagoCitaRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PagoCitaService {

    private static final Logger log = LoggerFactory.getLogger(PagoCitaService.class);

    @Autowired
    private PagoCitaRepository pagoCitaRepository;

    @Value("${mercadopago.access.token:}")
    private String accessToken;

    @Value("${mercadopago.notification.url:}")
    private String notificationUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String MP_API = "https://api.mercadopago.com";

    public PagoCitaService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public PagoCitaResponse crearPago(Integer idCita, BigDecimal monto, String metodoPago) {
        PagoCita pago = new PagoCita();
        pago.setIdCita(idCita);
        pago.setMonto(monto);
        pago.setMetodoPago(metodoPago != null ? metodoPago : "MERCADOPAGO");
        pago.setEstadoPago("PENDIENTE");

        try {
            ObjectNode preference = objectMapper.createObjectNode();
            ObjectNode item = objectMapper.createObjectNode();
            item.put("title", "Cita medica especialista #" + idCita);
            item.put("quantity", 1);
            item.put("unit_price", monto.doubleValue());
            item.put("currency_id", "PEN");
            preference.putArray("items").add(item);
            preference.put("external_reference", "CITA-" + idCita);

            ObjectNode backUrls = objectMapper.createObjectNode();
            backUrls.put("success", "http://localhost:4200/cita-pago-exitoso");
            backUrls.put("failure", "http://localhost:4200/cita-pago-fallido");
            backUrls.put("pending", "http://localhost:4200/cita-pago-pendiente");
            preference.set("back_urls", backUrls);

            if (notificationUrl != null && !notificationUrl.isEmpty()) {
                preference.put("notification_url", notificationUrl);
            } else {
                preference.put("notification_url", "http://localhost:8080/api/pagos/citas/notificacion");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(preference), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    MP_API + "/checkout/preferences", entity, String.class);

            JsonNode json = objectMapper.readTree(response.getBody());
            String prefId = json.get("id").asText();
            String initPoint = json.get("init_point").asText();
            pago.setReferenciaMp(prefId);
            pago = pagoCitaRepository.save(pago);

            PagoCitaResponse resp = new PagoCitaResponse();
            resp.setIdPago(pago.getIdPago());
            resp.setIdCita(pago.getIdCita());
            resp.setMonto(pago.getMonto());
            resp.setMetodoPago(pago.getMetodoPago());
            resp.setEstadoPago(pago.getEstadoPago());
            resp.setReferenciaMp(prefId);
            resp.setUrlPago(initPoint);
            resp.setFechaPago(pago.getFechaPago());
            return resp;
        } catch (Exception e) {
            log.error("Error creando preferencia MP: {}", e.getMessage());
            throw new AppException("Error al crear el pago. Intente nuevamente.");
        }
    }

    public PagoCitaResponse obtenerPagoPorCita(Integer idCita) {
        PagoCita pago = pagoCitaRepository.findByCita(idCita).orElse(null);
        if (pago == null) {
            PagoCitaResponse resp = new PagoCitaResponse();
            resp.setIdCita(idCita);
            resp.setEstadoPago("NO_ENCONTRADO");
            return resp;
        }
        return toResponse(pago);
    }

    public void confirmarPago(String referenciaMp, String estado) {
        log.info("Confirmando pago MP: {} estado: {}", referenciaMp, estado);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<ObjectNode> response = restTemplate.exchange(
                    MP_API + "/v1/payments/" + referenciaMp, HttpMethod.GET, entity, ObjectNode.class);

            if (response.getBody() != null) {
                String status = response.getBody().get("status").asText();
                String externalRef = response.getBody().has("external_reference")
                        ? response.getBody().get("external_reference").asText() : null;

                String estadoInterno = mapStatus(status);
                if (externalRef != null && externalRef.startsWith("CITA-")) {
                    Integer idCita = Integer.parseInt(externalRef.substring(5));
                    Optional<PagoCita> opt = pagoCitaRepository.findByCita(idCita);
                    opt.ifPresent(p -> pagoCitaRepository.updateEstado(
                            p.getIdPago(), estadoInterno, referenciaMp));
                }
            }
        } catch (Exception e) {
            log.error("Error consultando estado de pago: {}", e.getMessage());
        }
    }

    private String mapStatus(String mpStatus) {
        return switch (mpStatus) {
            case "approved" -> "APROBADO";
            case "rejected", "cancelled" -> "RECHAZADO";
            default -> "PENDIENTE";
        };
    }

    private PagoCitaResponse toResponse(PagoCita p) {
        PagoCitaResponse resp = new PagoCitaResponse();
        resp.setIdPago(p.getIdPago());
        resp.setIdCita(p.getIdCita());
        resp.setMonto(p.getMonto());
        resp.setMetodoPago(p.getMetodoPago());
        resp.setEstadoPago(p.getEstadoPago());
        resp.setReferenciaMp(p.getReferenciaMp());
        resp.setUrlPago(null);
        resp.setFechaPago(p.getFechaPago());
        return resp;
    }
}
