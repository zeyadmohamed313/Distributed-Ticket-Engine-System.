package com.flashsale.notification.service;

import com.flashsale.notification.model.TicketBookingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${notification.email.from}")
    private String fromEmail;

    /**
     * Send booking confirmation email
     */
    public void sendBookingConfirmation(TicketBookingEvent event) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            event.setUserId("mzeyad39@gmail.com");
            helper.setFrom(fromEmail);
            helper.setTo(event.getUserId()); // Assuming userId is the email
            helper.setSubject("🎫 Ticket Booking Confirmation - Event #" + event.getEventId());

            String htmlContent = buildEmailTemplate(event);
            helper.setText(htmlContent, true);

            log.info("📧 Sending email to: {}", event.getUserId());
            mailSender.send(message);
            log.info("✅ Email sent successfully to: {}", event.getUserId());

        } catch (MessagingException e) {
            log.error("❌ Failed to send email to {}: {}", event.getUserId(), e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    /**
     * Build HTML email template
     */
    private String buildEmailTemplate(TicketBookingEvent event) {
        String template = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }" +
                "        .container { background-color: white; padding: 30px; border-radius: 10px; max-width: 600px; margin: 0 auto; }"
                +
                "        .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 20px; border-radius: 10px; text-align: center; }"
                +
                "        .content { padding: 20px; }" +
                "        .ticket-info { background-color: #f8f9fa; padding: 15px; border-left: 4px solid #667eea; margin: 20px 0; }"
                +
                "        .footer { text-align: center; color: #666; font-size: 12px; margin-top: 30px; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">" +
                "            <h1>🎫 Ticket Booked Successfully!</h1>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <p>Dear Customer,</p>" +
                "            <p>Your ticket has been successfully booked. Here are your booking details:</p>" +
                "            " +
                "            <div class=\"ticket-info\">" +
                "                <p><strong>🎟️ Ticket ID:</strong> #%s</p>" +
                "                <p><strong>🎪 Event ID:</strong> #%s</p>" +
                "                <p><strong>💺 Seat Number:</strong> %s</p>" +
                "                <p><strong>📅 Booking Time:</strong> %s</p>" +
                "                <p><strong>✅ Status:</strong> %s</p>" +
                "            </div>" +
                "            " +
                "            <p>Please keep this email for your records. Show this confirmation at the event entrance.</p>"
                +
                "            <p>We look forward to seeing you at the event! 🎉</p>" +
                "        </div>" +
                "        <div class=\"footer\">" +
                "            <p>This is an automated message from Flash-Sale Ticketing System</p>" +
                "            <p>© 2025 Flash-Sale Ticketing. All rights reserved.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";

        return String.format(template,
                event.getTicketId(),
                event.getEventId(),
                event.getSeatNumber(),
                event.getTimestamp() != null ? event.getTimestamp().toString() : "N/A",
                event.getStatus() != null ? event.getStatus() : "CONFIRMED");
    }
}
