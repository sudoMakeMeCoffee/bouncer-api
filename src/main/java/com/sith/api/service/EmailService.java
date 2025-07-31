package com.sith.api.service;

public interface EmailService {
    public void sendEmail(String to, String subject, String code);
}