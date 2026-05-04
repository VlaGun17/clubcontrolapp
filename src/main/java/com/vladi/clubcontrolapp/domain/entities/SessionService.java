package com.vladi.clubcontrolapp.domain.entities;

import java.util.UUID;

public class SessionService {

  private UUID sessionId;
  private UUID serviceId;
  private int quantity;

  private Service service;

  public SessionService(UUID sessionId, UUID serviceId, int quantity) {
    this.sessionId = sessionId;
    this.serviceId = serviceId;
    this.quantity = quantity;
  }

  public UUID getSessionId() {
    return sessionId;
  }

  public void setSessionId(UUID sessionId) {
    this.sessionId = sessionId;
  }

  public UUID getServiceId() {
    return serviceId;
  }

  public void setServiceId(UUID serviceId) {
    this.serviceId = serviceId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public Service getService() {
    return service;
  }

  public void setService(Service service) {
    this.service = service;
  }
}
