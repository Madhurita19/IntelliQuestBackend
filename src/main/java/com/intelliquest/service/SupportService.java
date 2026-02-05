package com.intelliquest.service;

import com.intelliquest.dto.SupportRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class SupportService {

    @Autowired
    private JavaMailSender mailSender;

    private final String supportEmail = "work.intelliquest@gmail.com";

    public void sendSupportRequest(SupportRequestDTO request) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(supportEmail);
        helper.setSubject("New Support Request: " + request.getSubject());

        String body = """
        	    <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 20px; background-color: #f9f9f9; border-radius: 8px; max-width: 600px; margin: 0 auto; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);">
        	        <h2 style="font-size: 24px; color: #333; text-align: center; margin-bottom: 20px;">New Support Request</h2>
        	        <p style="font-size: 16px; color: #555; margin-bottom: 10px;"><strong style="color: #333;">Name:</strong> %s</p>
        	        <p style="font-size: 16px; color: #555; margin-bottom: 10px;"><strong style="color: #333;">Email:</strong> %s</p>
        	        <p style="font-size: 16px; color: #555; margin-bottom: 10px;"><strong style="color: #333;">Subject:</strong> %s</p>
        	        <p style="font-size: 16px; color: #555; margin-bottom: 20px;"><strong style="color: #333;">Message:</strong><br/>%s</p>
        	        <hr style="border: 0; border-top: 1px solid #ddd; margin-bottom: 20px;">
        	        <p style="font-size: 12px; color: #999; text-align: center;">This is an automated message. Please do not reply directly to this email.</p>
        	    </div>
        	""".formatted(
        	    request.getName(),
        	    request.getEmail(),
        	    request.getSubject(),
        	    request.getMessage().replace("\n", "<br/>")
        	);

        helper.setText(body, true);
        helper.setFrom(supportEmail);

        mailSender.send(message);
    }
}
