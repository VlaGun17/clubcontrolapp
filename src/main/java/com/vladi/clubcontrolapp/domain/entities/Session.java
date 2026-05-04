package com.vladi.clubcontrolapp.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Session {

  private final UUID id;
  private UUID clientId;
  private UUID computerId;
  private UUID tariffId;
  private LocalDate startTime;
  private LocalDate endTime;
  private BigDecimal totalCost;
  private boolean isActive;

  private List<SessionService> services = new ArrayList<>();

  public Session(UUID clientId, UUID computerId, UUID tariffId, LocalDate startTime, LocalDate endTime, BigDecimal totalCost, boolean isActive){
    this.id = UUID.randomUUID();
    this.clientId = clientId;
    this.computerId = computerId;
    this.tariffId = tariffId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.totalCost = totalCost;
    this.isActive = isActive;
  }

  public Session(UUID id, UUID client_id, UUID computerId, UUID tariffId, LocalDate startTime,
      LocalDate endTime, BigDecimal totalCost, boolean isActive) {
    this.id = id;
    this.clientId = client_id;
    this.computerId = computerId;
    this.tariffId = tariffId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.totalCost = totalCost;
    this.isActive = isActive;
  }

  public void addService(Service service, int quantity) {
    SessionService item = new SessionService(this.id, service.getId(), quantity);
    item.setService(service);
    this.services.add(item);
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

  public UUID getComputerId() {
    return computerId;
  }

  public void setComputerId(UUID computerId) {
    this.computerId = computerId;
  }

  public UUID getTariffId() {
    return tariffId;
  }

  public void setTariffId(UUID tariffId) {
    this.tariffId = tariffId;
  }

  public LocalDate getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDate startTime) {
    this.startTime = startTime;
  }

  public LocalDate getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDate endTime) {
    this.endTime = endTime;
  }

  public BigDecimal getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(BigDecimal totalCost) {
    this.totalCost = totalCost;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }

  public List<SessionService> getServices() {
    return services;
  }

  @Override
  public boolean equals(Object o){
    if(this == o) return true;
    if(!(o instanceof Session other)) return true;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode(){
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "Session{" +
        "id=" + id +
        ", client_id=" + clientId +
        ", computer_id=" + computerId +
        ", tariff_id=" + tariffId +
        ", start_time=" + startTime +
        ", end_time=" + endTime +
        ", total_cost=" + totalCost +
        ", isActive=" + isActive +
        '}';
  }
}
