package com.cdac.QBD.serviceImplementation;

import com.cdac.QBD.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * EmailServiceImpl
 *
 * Handles email sending with robust error handling.
 * Ensures that one failed email doesn't stop others from being sent.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        // Validate email address
        if (to == null || to.trim().isEmpty()) {
            logger.warn("❌ Email send skipped: recipient address is null or empty");
            return;
        }

        // Validate email format (basic check)
        if (!to.contains("@")) {
            logger.warn("❌ Email send skipped: invalid email format - {}", to);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("krushnamehetre186@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            logger.info("✅ Email sent successfully to: {} | Subject: {}", to, subject);

        } catch (Exception e) {
            // Log the error but don't throw - allows other emails to continue
            logger.error("❌ Failed to send email to: {} | Subject: {} | Error: {}",
                    to, subject, e.getMessage(), e);
        }
    }
}