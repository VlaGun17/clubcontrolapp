package com.vladi.clubcontrolapp.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Payment {

  private final UUID id;
  private UUID clientId;
  private UUID sessionId;
  private BigDecimal amount;
  private LocalDate paymentDate;

  public Payment (UUID clientId, UUID sessionId, BigDecimal amount){
    this.id = UUID.randomUUID();
    this.clientId = clientId;
    this.sessionId = sessionId;
    this.amount = amount;
    this.paymentDate = LocalDate.now();
  }

  public Payment(UUID id, UUID clientId, UUID sessionId, BigDecimal amount,
      LocalDate paymentDate) {
    this.id = id;
    this.clientId = clientId;
    this.sessionId = sessionId;
    this.amount = amount;
    this.paymentDate = paymentDate;
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

  public LocalDate getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(LocalDate paymentDate) {
    this.paymentDate = paymentDate;
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
