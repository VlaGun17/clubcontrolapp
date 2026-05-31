package com.vladi.clubcontrolapp.application.contract;

public interface EmailService {
  void sendEmail(String toEmail, String subject, String body);
}
