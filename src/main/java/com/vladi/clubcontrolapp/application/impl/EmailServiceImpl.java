package com.vladi.clubcontrolapp.application.impl;

import com.vladi.clubcontrolapp.application.contract.EmailService;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailServiceImpl implements EmailService {

  @Override
  public void sendEmail(String toEmail, String subject, String body) {
    final String username = "vladmun329@gmail.com";
    final String password = "ixke wqen asgh yumi";

    Properties properties =   new Properties();
    properties.put("mail.smtp.host", "smtp.gmail.com");
    properties.put("mail.smtp.port", "587");
    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.starttls.enable", "true");

    Session session = Session.getInstance(properties, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });

    try{
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(username));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
      message.setSubject(subject);

      String formattedBody = body.replace("\n", "<br/>");

      String htmlContent = "<html><body style='font-family: \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif; background-color: #121214; margin: 0; padding: 40px 0; color: #eeeeee;'>"
          + "  <div style='max-width: 520px; margin: 0 auto; background-color: #1c1c24; border-radius: 12px; border: 1px solid #2d2d3d; box-shadow: 0 10px 30px rgba(0,0,0,0.6); overflow: hidden;'>"
          + "    "
          + "    "
          + "    <div style='height: 4px; background: linear-gradient(90deg, #4cc9f0, #4361ee);'></div>"
          + "    "
          + "    <div style='padding: 35px;'>"
          + "      "
          + "      <div style='text-align: center; margin-bottom: 30px; padding-bottom: 20px; border-bottom: 1px solid #2d2d3d;'>"
          + "        <span style='font-size: 24px; font-weight: 800; color: #4cc9f0; letter-spacing: 3px; text-transform: uppercase;'>CLUB<span style='color: #ffffff;'>CONTROL</span></span>"
          + "        <div style='font-size: 11px; color: #77778c; margin-top: 4px; letter-spacing: 1px;'>CONTROL SYSTEM</div>"
          + "      </div>"
          + "      "
          + "      "
          + "      <div style='background-color: #23232e; padding: 25px; border-radius: 8px; border-left: 4px solid #4cc9f0; font-size: 15px; line-height: 1.6; color: #e2e2e9; font-weight: 400;'>"
          + "        " + formattedBody + ""
          + "      </div>"
          + "      "
          + "      "
          + "      <div style='text-align: center; margin-top: 35px; padding-top: 20px; border-top: 1px solid #2d2d3d; font-size: 11px; color: #62627a; line-height: 1.5;'>"
          + "        Це автоматичне сповіщення від системи керування клубом.<br/>"
          + "        Будь ласка, не відповідайте на цей лист.<br/>"
          + "        <span style='color: #4cc9f0; margin-top: 12px; display: inline-block; font-weight: 600;'>© 2026 ClubControl</span>"
          + "      </div>"
          + "    </div>"
          + "    "
          + "  </div>"
          + "</body></html>";

      message.setContent(htmlContent, "text/html; charset=utf-8");

      Transport.send(message);
      System.out.println("Лист успішно надіслано на " + toEmail);
    } catch (MessagingException e) {
      System.err.println("Помилка відправки стилізованого листа:");
      e.printStackTrace();
    }
  }
}
