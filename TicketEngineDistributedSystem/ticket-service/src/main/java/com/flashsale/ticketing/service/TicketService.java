package com.flashsale.ticketing.service;

import com.flashsale.ticketing.config.DbContext;
import com.flashsale.ticketing.config.LoggingAspect;
import com.flashsale.ticketing.model.Ticket;
import com.flashsale.ticketing.repository.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
public class TicketService {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);


    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RedisLockService redisLockService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public Ticket createTicket(Ticket ticket) {
        Long eventId = ticket.getEventId();
        String seatNumber = ticket.getSeatNumber();

        String targetShard;
        if (eventId % 2 == 0) {
            targetShard = "SHARD_2";
        } else {
            targetShard = "SHARD_1";
        }
        log.info(">>> 🔀 Switching to [{}] for Event ID: [{}]", targetShard, eventId);
        DbContext.setDbType(targetShard);
        log.info("🔀 Routing Strategy: Event [{}] -> Directed to [{}]", eventId, targetShard);

        String lockKey = "lock:event:" + eventId + ":seat:" + seatNumber;

        try {
            log.debug("🔐 Attempting to acquire lock for key: {}", lockKey);

            boolean isLocked = redisLockService.acquireLock(lockKey, 10);

            if (!isLocked) {
                // Business case: seat is already being processed
                log.warn("⛔ Lock Contention: Failed to acquire lock for [{}]. Seat is busy.", lockKey);
                throw new RuntimeException("Seat " + seatNumber + " is currently being processed by another user!");
            }

            log.info("🔒 Lock Acquired successfully: {}", lockKey);

            Ticket savedTicket = transactionTemplate.execute(status -> {
                log.debug("💾 Starting Database Transaction...");
                Ticket t = ticketRepository.save(ticket);
                log.info("✅ Database Insert Success: Ticket ID [{}]", t.getId());
                return t;
            });

            String jsonMessage = String.format(
                    "{\"ticketId\":%d,\"eventId\":%d,\"seatNumber\":\"%s\",\"userId\":\"%s\",\"status\":\"%s\",\"timestamp\":\"%s\"}",
                    savedTicket.getId(),
                    ticket.getEventId(),
                    ticket.getSeatNumber(),
                    ticket.getUserId(),
                    ticket.getStatus() != null ? ticket.getStatus() : "CONFIRMED",
                    java.time.LocalDateTime.now().toString());

            log.info("📨 Publishing Event to Kafka Topic [ticket-sold-topic]: {}", jsonMessage);

            kafkaTemplate.send("ticket-sold-topic", jsonMessage);

            return savedTicket;

        } catch (Exception e) {
            log.error("💥 Error during ticket creation process: {}", e.getMessage());
            throw e; //
        } finally {
            try {
                redisLockService.releaseLock(lockKey);
                log.info("🔓 Lock Released: {}", lockKey);
            } catch (Exception ex) {
                log.error("⚠️ Failed to release lock: {}", ex.getMessage());
            }

            DbContext.clear();
            log.debug("🧹 DbContext Cleared.");
        }
    }

    @Transactional(readOnly = true) // Routes to replica for read operations
    public Ticket getTicketById(Long ticketId, Long eventId) {
        routeToShard(eventId);

        try {
            return ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
        } finally {
            DbContext.clear();
        }
    }

    @Transactional(readOnly = true) // Routes to replica for read operations
    public List<Ticket> getAvailableTickets(Long eventId) {
        routeToShard(eventId);

        try {
            return ticketRepository.findByStatusAndEventId("Available", eventId);
        } finally {
            DbContext.clear();
        }
    }

    private void routeToShard(Long eventId) {
        if (eventId % 2 == 0) {
            DbContext.setDbType("SHARD_2");
            log.info(">>> 📖 Routing READ request to [SHARD_2] for Event [{}]", eventId);
        } else {
            DbContext.setDbType("SHARD_1");
            log.info(">>> 📖 Routing READ request to [SHARD_1] for Event [{}]", eventId);
        }
    }
}