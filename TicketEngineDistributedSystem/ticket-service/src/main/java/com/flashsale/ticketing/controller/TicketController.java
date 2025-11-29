package com.flashsale.ticketing.controller;

import com.flashsale.ticketing.model.Ticket;
import com.flashsale.ticketing.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // POST http://localhost:8080/api/tickets
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        Ticket createdTicket = ticketService.createTicket(ticket);
        return ResponseEntity.ok(createdTicket);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicket(@PathVariable Long id, @RequestParam Long eventId) {
        Ticket ticket = ticketService.getTicketById(id, eventId);
        return ResponseEntity.ok(ticket);
    }


    @GetMapping("/available/{eventId}")
    public ResponseEntity<List<Ticket>> getAvailableTickets(@PathVariable Long eventId) {
        List<Ticket> tickets = ticketService.getAvailableTickets(eventId);
        return ResponseEntity.ok(tickets);
    }
}