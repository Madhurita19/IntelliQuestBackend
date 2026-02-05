package com.intelliquest.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class ApplyInstructorMailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendInstructorApplicationEmail(String fullName, String email, String experience, String expertise, String motivation) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo("work.intelliquest@gmail.com");
        helper.setSubject("New Instructor Application");

        String html = """
            <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f9fafb; padding: 30px; border-radius: 8px; border: 1px solid #e5e7eb;">
                <h2 style="color: #1d4ed8; margin-bottom: 20px;">New Instructor Application</h2>
                <table style="width: 100%%; font-size: 16px; color: #111827;">
                    <tr>
                        <td style="padding: 8px 0;"><strong>Full Name:</strong></td>
                        <td style="padding: 8px 0;">%s</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px 0;"><strong>Email:</strong></td>
                        <td style="padding: 8px 0;">%s</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px 0;"><strong>Experience:</strong></td>
                        <td style="padding: 8px 0;">%s</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px 0;"><strong>Expertise:</strong></td>
                        <td style="padding: 8px 0;">%s</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px 0; vertical-align: top;"><strong>Motivation:</strong></td>
                        <td style="padding: 8px 0;">%s</td>
                    </tr>
                </table>
            </div>
        """.formatted(
            fullName,
            email,
            experience,
            expertise,
            motivation.replace("\n", "<br/>")
        );

        helper.setText(html, true);
        helper.setFrom(fromEmail);

        mailSender.send(message);
    }
}
