package com.intelliquest.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class PaymentConfirmationMailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmation(String toEmail, String courseTitle, String orderId, String paymentId, BigDecimal amount) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("work.intelliquest@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("✅ IntelliQuest - Course Purchase Confirmation");

            // Formatting the HTML content with the actual data
            String htmlContent = 
                "<div style=\"font-family: Arial, sans-serif; color: #333; background-color: #f9f9f9; padding: 20px; max-width: 600px; margin: 0 auto; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\">" +
                    "<!-- Card Container -->" +
                    "<div style=\"background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\">" +
                        
                        "<!-- Header with Logo and Company Name -->" +
                        "<div style=\"text-align: center; margin-bottom: 30px;\">" + 
                        "<img src=\"https://raw.githubusercontent.com/avro000/minorproject_caloriecraft/refs/heads/main/Untitled%20design%20(1).png\" alt=\"IntelliQuest Logo\" width=\"30\" height=\"30\" style=\"display: inline-block; margin-right: 5px; vertical-align: middle;\" />" + 
                        "<h1 style=\"font-size: 20px; color: #2d6cdf; margin: 0; display: inline-block; vertical-align: middle;\">IntelliQuest</h1>" + 
                        "</div>" +
                
                        "<!-- Email Content -->" +
                        "<h2 style=\"color: #2d6cdf; font-size: 20px; margin-bottom: 10px;\">Thank you for your purchase!</h2>" +
                        "<p style=\"font-size: 16px;\">You have successfully purchased the course:</p>" +
                        "<h3 style=\"color: #2f2f2f; font-weight: 600; font-size: 18px; margin-top: 0;\">" + courseTitle + "</h3>" +
                
                        "<!-- Payment Details -->" +
                        "<p style=\"font-size: 16px; margin-top: 20px;\">Here are your payment details:</p>" +
                        "<table style=\"border-collapse: collapse; width: 100%; margin-top: 10px; overflow: hidden;\">" +
                            "<tr>" +
                                "<th style=\"border: 1px solid #ccc; padding: 12px; background-color: #f0f0f0; text-align: left; color: #333; font-weight: 600;\">Payment ID</th>" +
                                "<td style=\"border: 1px solid #ccc; padding: 12px; text-align: left; color: #555;\">" + paymentId + "</td>" +
                            "</tr>" +
                            "<tr>" +
                                "<th style=\"border: 1px solid #ccc; padding: 12px; background-color: #f0f0f0; text-align: left; color: #333; font-weight: 600;\">Order ID</th>" +
                                "<td style=\"border: 1px solid #ccc; padding: 12px; text-align: left; color: #555;\">" + orderId + "</td>" +
                            "</tr>" +
                            "<tr>" +
                                "<th style=\"border: 1px solid #ccc; padding: 12px; background-color: #f0f0f0; text-align: left; color: #333; font-weight: 600;\">Amount</th>" +
                                "<td style=\"border: 1px solid #ccc; padding: 12px; text-align: left; color: #555;\">₹" + amount + "</td>" +
                            "</tr>" +
                        "</table>" +
                
                        "<!-- Footer -->" +
                        "<p style=\"font-size: 16px; margin-top: 30px;\">You now have full access to all course materials. We wish you the best of luck in your learning journey!</p>" +
                        "<p style=\"font-size: 16px; font-weight: 600;\">Happy learning!<br><strong style=\"font-size: 12px; color: #13b3b3f; font-weight: 300\">– IntelliQuest Team</strong></p>" +
                
                        "<!-- Signature with CTA -->" +
                        "<div style=\"text-align: center; margin-top: 30px;\">" +
                            "<a href=\"http://www.intelliquest.com\" style=\"font-size: 16px; color: #2d6cdf; text-decoration: none; font-weight: 600;\">Visit IntelliQuest</a>" +
                        "</div>" +
                    "</div>" +
                "</div>";

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
