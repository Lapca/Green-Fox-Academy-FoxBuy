package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.User;
import com.gfa.siemensfoxbuybytemasters.repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailServiceImp implements EmailService{

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;



    @Value("${email.from}")
    private String fromEmailAddress;

    @Autowired
    public EmailServiceImp(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    @Override
    public void sendHtmlEmail(List<String> recipients, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(new InternetAddress(fromEmailAddress));
        for ( String recipient : recipients ) {
            message.setRecipients(MimeMessage.RecipientType.TO, recipient);
        }
        message.setSubject(subject);

        String htmlContent = htmlContentBuilder(content);
        message.setContent(htmlContent, "text/html; charset=utf-8");

        mailSender.send(message);
    }
    @Override
    public String htmlContentBuilder(String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>This is a test Spring Boot email</h1>")
                .append("<p>")
                .append(content)
                .append("</p>");
        return sb.toString();
    }

    @Override
    public void sendVerificationEmail(User user) {
        String token = user.getEmailVerificationToken();
        String recipientEmail = user.getEmail();
        String link = "<a href=\"http://localhost:8080/verify-email?token="
                + token +  "\" " + ">click to verify your account</a>";

        try {
            sendHtmlEmail(List.of(recipientEmail), "Verification email", htmlContentBuilder(link));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendEmailToUser(List<String> userEmails) {
        String subject = "New Matching Ad Alert";
        String body = "Your Ad Watchdog has discovered a new ad that matches your specified criteria.";

        try {
            sendHtmlEmail(userEmails, subject, body);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void sendProductKeyToBuyer(User user, String productKey) {
        String subject = "Successful Digital Product Purchase";
        String body = "Congratulations on your successful purchase of a digital product!" +
                " Your product key is: " + productKey;
        String recipientEmail = user.getEmail();

        try {
            sendHtmlEmail(List.of(recipientEmail), subject, body);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
