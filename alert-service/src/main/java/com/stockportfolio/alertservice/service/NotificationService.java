package com.stockportfolio.alertservice.service;

import com.stockportfolio.alertservice.entity.Alert;
import com.stockportfolio.alertservice.entity.AlertNotification;
import com.stockportfolio.alertservice.repository.AlertNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pluggable notification service supporting Email and DB log notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final AlertNotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${notification.email.from:noreply@stockportfolio.com}")
    private String fromEmail;

    /**
     * Send notification when an alert is triggered.
     * Supports both email and DB log (pluggable architecture).
     */
    public void sendAlertNotification(Alert alert, BigDecimal currentPrice) {
        String message = buildAlertMessage(alert, currentPrice);

        // Always log to DB
        logToDatabase(alert, currentPrice, message);

        // Send email if configured and email is provided
        if (emailEnabled && alert.getNotificationEmail() != null) {
            sendEmail(alert.getNotificationEmail(), "Stock Portfolio Alert Triggered", message);
        }
    }

    private void logToDatabase(Alert alert, BigDecimal currentPrice, String message) {
        AlertNotification notification = AlertNotification.builder()
                .alertId(alert.getId())
                .userId(alert.getUserId())
                .message(message)
                .triggeredPrice(currentPrice)
                .notificationType(AlertNotification.NotificationType.DB_LOG)
                .sentAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        log.info("Alert notification logged to DB: {}", message);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Alert email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send alert email to {}: {}", to, e.getMessage());
        }
    }

    private String buildAlertMessage(Alert alert, BigDecimal currentPrice) {
        return switch (alert.getAlertType()) {
            case PRICE_THRESHOLD -> String.format(
                    "Alert triggered! Stock %s current price %.2f has crossed your threshold of %.2f (%s).",
                    alert.getStockSymbol(), currentPrice, alert.getThresholdValue(),
                    alert.getConditionType().name().toLowerCase());
            case PORTFOLIO_LOSS -> String.format(
                    "Alert triggered! Your portfolio loss has exceeded %.2f%%.",
                    alert.getThresholdValue());
        };
    }
}
