package com.intelliquest.service;

import com.intelliquest.dto.FeedbackRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendFeedbackEmail(FeedbackRequestDTO feedback) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo("work.intelliquest@gmail.com");
        helper.setSubject("New Feedback Submission");

        String html = """
        	    <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f9fafb; padding: 30px; border-radius: 8px; border: 1px solid #e5e7eb;">
        	        <h2 style="color: #1d4ed8; margin-bottom: 20px;">New Feedback Received</h2>
        	        <table style="width: 100%%; font-size: 16px; color: #111827;">
        	            <tr>
        	                <td style="padding: 8px 0;"><strong>Name:</strong></td>
        	                <td style="padding: 8px 0;">%s</td>
        	            </tr>
        	            <tr>
        	                <td style="padding: 8px 0;"><strong>Email:</strong></td>
        	                <td style="padding: 8px 0;">%s</td>
        	            </tr>
        	            <tr>
        	                <td style="padding: 8px 0;"><strong>Rating:</strong></td>
        	                <td style="padding: 8px 0;">%d star%s</td>
        	            </tr>
        	            <tr>
        	                <td style="padding: 8px 0; vertical-align: top;"><strong>Feedback:</strong></td>
        	                <td style="padding: 8px 0;">%s</td>
        	            </tr>
        	        </table>
        	    </div>
        	""".formatted(
        	    feedback.getName(),
        	    feedback.getEmail(),
        	    feedback.getRating(),
        	    feedback.getRating() > 1 ? "s" : "",
        	    feedback.getFeedback().replace("\n", "<br/>")
        	);


        helper.setText(html, true);
        helper.setFrom("work.intelliquest@gmail.com");

        mailSender.send(message);
    }
}
