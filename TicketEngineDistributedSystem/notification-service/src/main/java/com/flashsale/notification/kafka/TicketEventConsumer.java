package com.flashsale.notification.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flashsale.notification.model.TicketBookingEvent;
import com.flashsale.notification.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TicketEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TicketEventConsumer.class);

    @Autowired
    private EmailService emailService;

    private final ObjectMapper objectMapper;

    public TicketEventConsumer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Listen to Kafka messages from topic: ticket-sold-topic
     */
    @KafkaListener(topics = "ticket-sold-topic", groupId = "notification-group")
    public void handleTicketSoldEvent(String message) {
        log.info("========================================");
        log.info("🔔 Kafka Message Received!");
        log.info("📩 Raw Message: {}", message);

        try {
            // Try to parse message as JSON
            TicketBookingEvent event = parseMessage(message);

            log.info("✅ Parsed Event: Ticket ID={}, Event ID={}, Seat={}, User={}",
                    event.getTicketId(),
                    event.getEventId(),
                    event.getSeatNumber(),
                    event.getUserId());

            // Send email
            emailService.sendBookingConfirmation(event);

            log.info("========================================");

        } catch (Exception e) {
            log.error("❌ Error processing Kafka message: {}", e.getMessage());
            log.error("========================================");
        }
    }

    /**
     * Parse text message to Object
     * Supports JSON or plain text
     */
    private TicketBookingEvent parseMessage(String message) {
        try {
            // Try to parse as JSON
            return objectMapper.readValue(message, TicketBookingEvent.class);
        } catch (JsonProcessingException e) {
            // If not JSON, extract data from plain text
            log.warn("⚠️ Message is not JSON, parsing as plain text");
            return parseSimpleMessage(message);
        }
    }

    /**
     * Extract data from simple text message
     * Example: "Ticket Booked! ID: 123, User: user@example.com"
     */
    private TicketBookingEvent parseSimpleMessage(String message) {
        TicketBookingEvent event = new TicketBookingEvent();

        // Extract Ticket ID
        if (message.contains("ID:")) {
            String idPart = message.substring(message.indexOf("ID:") + 3).trim();
            String id = idPart.split(",")[0].trim();
            try {
                event.setTicketId(Long.parseLong(id));
            } catch (NumberFormatException e) {
                log.warn("Could not parse ticket ID from: {}", id);
            }
        }

        // Extract User
        if (message.contains("User:")) {
            String userPart = message.substring(message.indexOf("User:") + 5).trim();
            event.setUserId(userPart);
        }

        event.setStatus("CONFIRMED");

        return event;
    }
}
