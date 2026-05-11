package com.vladi.clubcontrolapp.infrastructure.persistance.contract;

import com.vladi.clubcontrolapp.domain.entities.SessionService;
import java.util.List;
import java.util.UUID;

public interface SessionServiceRepository {
  void save(SessionService entity);
  List<SessionService> findBySessionId(UUID sessionId);
}
