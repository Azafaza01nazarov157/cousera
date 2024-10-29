package org.example.cursera.service.impl;

import org.example.cursera.service.gmail.MailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static org.example.cursera.constants.MailConstants.FROM;

@Service
public class MailSenderServiceImpl implements MailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendNewMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
