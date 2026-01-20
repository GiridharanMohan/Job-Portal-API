package com.dev.jobportal.util;

import com.dev.jobportal.model.EmailDetails;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailUtil {

    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmailNotification(EmailDetails emailDetails, String username){
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(sender);
            mail.setSubject(emailDetails.getSubject());
            mail.setTo(emailDetails.getRecipient());
            mail.setText(emailDetails.getBody());
            javaMailSender.send(mail);
            log.info(Constant.MAIL_SENT_SUCCESSFUL);
        } catch (Exception e){
            log.error(Constant.MAIL_SENT_FAILURE);
        }
    }

    public void sendEmailNotification(String recipient, String subject, String recipientName, String header, String jobTitle, String status){
        String htmlBody = """
        <html>
        <body style="font-family: Arial; background:#f4f4f4; padding:20px;">
            <div style="max-width:600px; margin:auto; background:white; padding:20px; border-radius:8px;">
                <h2 style="color:#2c3e50;">Career Ladder</h2>

                <p>Hello <b>%s</b>,</p>
                <p> %s </p>

                <div style="padding:10px; background:#e8f4ff; border-left:5px solid #3498db;">
                    <p><b>Job Title:</b> %s</p>
                    <p><b>Status:</b> %s</p>
                </div>

                <p>Thank you,<br/><b>Career Ladder Team<b></p>
            </div>
        </body>
        </html>
        """.formatted(recipientName, header, jobTitle, status);
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(recipient);
            helper.setFrom(sender);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            javaMailSender.send(mimeMessage);
            log.info(Constant.MAIL_SENT_SUCCESSFUL);
        } catch (Exception e){
            log.error(Constant.MAIL_SENT_FAILURE);
        }
    }
}
