package com.yearis.e_commerce.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${app.email.from}")
    private String senderEmail;

    @Async
    public void sendVerificationEmail(String receiverEmail, String otp) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(senderEmail);
        mailMessage.setTo(receiverEmail);
        mailMessage.setSubject("Verification Code for User Registration - Cartline");
        mailMessage.setText("""
            Hello,
            
            Your verification code for user registration is: %s
            
            This code will expire in 5 minutes.
            If you did not request this, please ignore this email.
            
            Best Regards,
            Team Cartline
            """.formatted(otp));

        javaMailSender.send(mailMessage);
    }
}
