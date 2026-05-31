package com.vladi.clubcontrolapp.domain.enums;

public enum MethodPayment {
  Cash("Готівка"),
  Card("Картка"),
  Balance("Баланс");

  private final String displayName;

  MethodPayment(String displayName){
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static MethodPayment fromDisplayName(String text) {
    for (MethodPayment method : MethodPayment.values()) {
      if (method.getDisplayName().equalsIgnoreCase(text) || method.name().equalsIgnoreCase(text)) {
        return method;
      }
    }
    return Cash;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
