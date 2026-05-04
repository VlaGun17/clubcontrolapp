package com.vladi.clubcontrolapp.domain.entities;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Tariff {
  private final UUID id;
  private String name;
  private BigDecimal pricePerHour;
  private boolean isNight;

  public Tariff(String name, BigDecimal pricePerHour, boolean isNight) {
    this.id = UUID.randomUUID();
    this.name = name;
    this.pricePerHour = pricePerHour;
    this.isNight = isNight;
  }

  public Tariff(UUID id, String name, BigDecimal pricePerHour, boolean isNight) {
    this.id = id;
    this.name = name;
    this.pricePerHour = pricePerHour;
    this.isNight = isNight;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getPricePerHour() {
    return pricePerHour;
  }

  public void setPricePerHour(BigDecimal pricePerHour) {
    this.pricePerHour = pricePerHour;
  }

  public boolean isNight() {
    return isNight;
  }

  public void setNight(boolean night) {
    isNight = night;
  }

  @Override
  public boolean equals(Object o){
    if(this == o) return true;
    if(!(o instanceof Tariff other)) return true;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode(){
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "Tariff{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", price_per_hour=" + pricePerHour +
        ", isNight=" + isNight +
        '}';
  }
}
