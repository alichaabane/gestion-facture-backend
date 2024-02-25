package com.sesame.gestionfacture.service;

import com.sesame.gestionfacture.config.ContactMail;
import com.sesame.gestionfacture.exception.ExchangeException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailingService {

    @Autowired
    private JavaMailSender emailSender;


    public void sendMail(ContactMail mail) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getContent(), true);
            helper.setTo(mail.getTo());
            helper.setFrom(mail.getFrom());

            emailSender.send(message);
        } catch (MessagingException e) {
            throw new ExchangeException("Error when sending email: " + e.getMessage());
        }
    }

}
