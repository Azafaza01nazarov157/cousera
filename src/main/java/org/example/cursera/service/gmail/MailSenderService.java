package org.example.cursera.service.gmail;

public interface MailSenderService {

    void sendNewMail(String to, String subject, String body);
}
