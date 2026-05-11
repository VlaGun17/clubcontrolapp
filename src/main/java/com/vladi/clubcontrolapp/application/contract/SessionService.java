package com.vladi.clubcontrolapp.application.contract;

import com.vladi.clubcontrolapp.application.BaseService;
import com.vladi.clubcontrolapp.domain.entities.Session;
import java.util.UUID;

public interface SessionService extends BaseService<Session, UUID> {
  void startSession(UUID clientId, UUID computerId, UUID tariffId);
  void endSession(UUID sessionId);
  void addServiceToSession(UUID sessionId, UUID serviceId, int quantity);
}
