package com.vladi.clubcontrolapp.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Client {

  private final UUID id;
  private String nickname;
  private String email;
  private BigDecimal balance;
  private int discountPercent;
  private int visitCount;
  private LocalDate registrationDate;

  public Client(String nickname, String email){
    this.id = UUID.randomUUID();
    this.nickname = nickname;
    this.email = email;
    this.registrationDate = LocalDate.now();
  }

  public Client(UUID id, String nickname, String email, BigDecimal balance, int discountPercent,
      int visitCount, LocalDate registrationDate) {
    this.id = id;
    this.nickname = nickname;
    this.email = email;
    this.balance = balance;
    this.discountPercent = discountPercent;
    this.visitCount = visitCount;
    this.registrationDate = registrationDate;
  }

  public UUID getId() {
    return id;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public int getDiscountPercent() {
    return discountPercent;
  }

  public void setDiscountPercent(int discountPercent) {
    this.discountPercent = discountPercent;
  }

  public int getVisitCount() {
    return visitCount;
  }

  public void setVisitCount(int visitCount) {
    this.visitCount = visitCount;
  }

  public LocalDate getRegistrationDate() {
    return registrationDate;
  }

  public void setRegistrationDate(LocalDate registrationDate) {
    this.registrationDate = registrationDate;
  }

  @Override
  public boolean equals(Object o){
    if(this == o) return true;
    if(!(o instanceof Client other)) return true;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode(){
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "Client{" +
        "id=" + id +
        ", nickname='" + nickname + '\'' +
        ", email='" + email + '\'' +
        ", balance=" + balance +
        ", discount_percent=" + discountPercent +
        ", visit_count=" + visitCount +
        ", registration_date=" + registrationDate +
        '}';
  }
}