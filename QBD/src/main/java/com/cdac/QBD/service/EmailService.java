package com.cdac.QBD.service;

public interface EmailService {

    void sendEmail(String to, String subject, String body);
}
