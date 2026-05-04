package com.vladi.clubcontrolapp.domain.entities;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Service {

  private final UUID id;
  private String name;
  private BigDecimal price;

  public Service(String name, BigDecimal price){
    this.id = UUID.randomUUID();
    this.name = name;
    this.price = price;
  }

  public Service(UUID id, String name, BigDecimal price) {
    this.id = id;
    this.name = name;
    this.price = price;
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

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  @Override
  public boolean equals(Object o){
    if(this == o) return true;
    if(!(o instanceof Service other)) return true;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode(){
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "Service{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", price=" + price +
        '}';
  }
}
