package com.vladi.clubcontrolapp.application.contract;

public interface PaymentInvoiceService {
  void sendInvoiceToEmail(String clientEmail, String amount, String orderId);
  String checkPaymentStatus(String orderId);
}
