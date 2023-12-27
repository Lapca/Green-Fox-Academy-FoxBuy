package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.User;
import jakarta.mail.MessagingException;

import java.util.List;

public interface EmailService {

    void sendHtmlEmail(List<String> recipients, String subject, String content) throws MessagingException;
    String htmlContentBuilder(String content);
    void sendVerificationEmail(User user);

    void sendEmailToUser(List<String> userEmails);

    void sendProductKeyToBuyer(User user, String productKey);
}
