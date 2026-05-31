package com.vladi.clubcontrolapp.application.impl;

import com.liqpay.LiqPay;
import com.vladi.clubcontrolapp.application.contract.PaymentInvoiceService;
import com.vladi.clubcontrolapp.application.util.CustomLiqPay;
import com.vladi.clubcontrolapp.presentation.controller.SettingsController;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PaymentInvoiceServiceImpl implements PaymentInvoiceService {

  private static final String username = "vladmun329@gmail.com";
  private static final String password = "ixke wqen asgh yumi";

  @Override
  public void sendInvoiceToEmail(String clientEmail, String amount, String orderId) {
    String pubKey = SettingsController.getSavedPublicKey();
    String privKey = SettingsController.getSavedPrivateKey();

    if (pubKey.isEmpty() || privKey.isEmpty()) {
      System.err.println("Помилка: Ключі LiqPay не налаштовані!");
      return;
    }

    LiqPay liqPay = new LiqPay(pubKey, privKey);
    Map<String, String> params = new HashMap<>();
    params.put("action", "pay");
    params.put("version", "3");
    params.put("amount", amount);
    params.put("currency", "UAH");
    params.put("description", "Оплата комп'ютерної сесії в клубі (Ордер: " + orderId + ")");
    params.put("order_id", orderId);
    params.put("sandbox", "1");

    String checkoutUrl;
    try{
      CustomLiqPay customLiqPay = new CustomLiqPay(pubKey, privKey);
      Map<String, String> lqParams = customLiqPay.getPaymentData(params);

      String data = lqParams.get("data");
      String signature = lqParams.get("signature");
      checkoutUrl = "https://www.liqpay.ua/api/3/checkout?data=" + data + "&signature=" + signature;
    } catch (Exception e) {
      throw new RuntimeException("Помилка генерації платіжних параметрів LiqPay: " + e.getMessage(), e);
    }
    sendHtmlEmail(clientEmail, amount, orderId, checkoutUrl);
  }

  private void sendHtmlEmail(String toEmail, String amount, String orderId, String paymentUrl) {
    Properties prop = new Properties();
    prop.put("mail.smtp.host", "smtp.gmail.com");
    prop.put("mail.smtp.port", "587");
    prop.put("mail.smtp.auth", "true");
    prop.put("mail.smtp.starttls.enable", "true");

    Session session = Session.getInstance(prop, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });

    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(username));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
      message.setSubject("Рахунок на оплату ігрового часу — Ордер " + orderId);

      String htmlContent = "<html><body style='font-family: Arial, sans-serif; background-color: #1a1a1a; color: #ffffff; padding: 20px;'>"
          + "<div style='max-width: 500px; margin: 0 auto; background-color: #2a2a32; padding: 30px; border-radius: 10px; border: 1px solid #444;'>"
          + "<h2 style='color: #4cc9f0; text-align: center;'>Ваш рахунок готовий!</h2>"
          + "<p>Вітаємо! Ваша ігрова сесія завершена. Для оплати натисніть кнопку нижче:</p>"
          + "<div style='background-color: #1e1e24; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #4cc9f0;'>"
          + "<strong>Сума до оплати:</strong> " + amount + " UAH<br/>"
          + "<strong>Номер замовлення:</strong> " + orderId + "</div>"
          + "<div style='text-align: center; margin-top: 30px;'>"
          + "<a href='" + paymentUrl + "' style='background-color: #4caf50; color: white; padding: 12px 30px; text-decoration: none; font-weight: bold; border-radius: 5px; display: inline-block;'>ОПЛАТИТИ КАРТКОЮ</a>"
          + "</div>"
          + "<p style='font-size: 11px; color: #888; text-align: center; margin-top: 30px;'>Тестовий режим білінгу клубу. Дякуємо, що ви з нами!</p>"
          + "</div></body></html>";

      message.setContent(htmlContent, "text/html; charset=utf-8");

      Transport.send(message);
      System.out.println("【EMAIL SUCCESS】 Тестовий лист із посиланням LiqPay надіслано на " + toEmail);

    } catch (MessagingException e) {
      System.err.println("Помилка відправки листа через SMTP:");
      e.printStackTrace();
    }
  }

  @Override
  public String checkPaymentStatus(String orderId) {
    String pubKey = SettingsController.getSavedPublicKey();
    String privKey = SettingsController.getSavedPrivateKey();

    if (pubKey.isEmpty() || privKey.isEmpty()) {
      return "error";
    }

    LiqPay liqPay = new LiqPay(pubKey, privKey);
    Map<String, String> params = new HashMap<>();
    params.put("action", "status");
    params.put("version", "3");
    params.put("order_id", orderId);

    try {
      Map<String, Object> response = liqPay.api("request", params);
      String status = (String) response.get("status");
      if ("error".equals(status)) {
        String errCode = (String) response.get("err_code");
        if ("payment_not_found".equals(errCode)) {
          return "not_found_yet";
        }
      }
      return status != null ? status : "unknown";
    } catch (Exception e) {
      System.err.println("Помилка перевірки статусу LiqPay: " + e.getMessage());
      return "error";
    }
  }
}
