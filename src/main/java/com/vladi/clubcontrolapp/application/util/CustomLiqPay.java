package com.vladi.clubcontrolapp.application.util;

import com.liqpay.LiqPay;
import java.util.Map;

public class CustomLiqPay extends LiqPay {
  public CustomLiqPay(String publicKey, String privateKey) {
    super(publicKey, privateKey);
  }

  public Map<String, String> getPaymentData(Map<String, String> params) {
    return this.generateData(params);
  }
}
