package edu.upn.clinica.backend.teleconsulta.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messaging;
    @Autowired
    private ChatMensajeRepository chatMensajeRepository;

    public ChatController(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    @MessageMapping("/chat/{consultaId}")
    public void enviar(@DestinationVariable Integer consultaId,
                       @Payload ChatMessage mensaje,
                       SimpMessageHeaderAccessor headerAccessor) {
        mensaje.setConsultaId(consultaId);
        chatMensajeRepository.save(mensaje);
        String destino = "/topic/chat/" + consultaId;
        messaging.convertAndSend(destino, mensaje);
    }
}
