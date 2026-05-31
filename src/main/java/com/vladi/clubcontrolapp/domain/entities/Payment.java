package com.vladi.clubcontrolapp.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Payment {

  private final UUID id;
  private UUID clientId;
  private UUID sessionId;
  private BigDecimal amount;
  private LocalDateTime paymentDate;
  private String paymentMethod;

  public Payment (UUID clientId, UUID sessionId, BigDecimal amount){
    this.id = UUID.randomUUID();
    this.clientId = clientId;
    this.sessionId = sessionId;
    this.amount = amount;
    this.paymentDate = LocalDateTime.now();
  }

  public Payment(UUID id, UUID clientId, UUID sessionId, BigDecimal amount,
      LocalDateTime paymentDate, String paymentMethod) {
    this.id = id;
    this.clientId = clientId;
    this.sessionId = sessionId;
    this.amount = amount;
    this.paymentDate = paymentDate;
    this.paymentMethod = paymentMethod;
  }

  public UUID getId() {
    return id;
  }

  public UUID getClientId() {
    return clientId;
  }

  public void setClientId(UUID clientId) {
    this.clientId = clientId;
  }

  public UUID getSessionId() {
    return sessionId;
  }

  public void setSessionId(UUID sessionId) {
    this.sessionId = sessionId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public LocalDateTime getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(LocalDateTime paymentDate) {
    this.paymentDate = paymentDate;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  @Override
  public boolean equals(Object o){
    if(this == o) return true;
    if(!(o instanceof Payment other)) return true;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode(){
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "Payment{" +
        "id=" + id +
        ", client_id=" + clientId +
        ", session_id=" + sessionId +
        ", amount=" + amount +
        ", payment_date=" + paymentDate +
        '}';
  }
}
