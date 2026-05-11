package com.vladi.clubcontrolapp.application.impl;

import com.vladi.clubcontrolapp.application.contract.SessionService;
import com.vladi.clubcontrolapp.domain.entities.Service;
import com.vladi.clubcontrolapp.domain.entities.Session;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class SessionServiceImpl implements SessionService {
  private final PersistanceSession persistenceSession;

  public SessionServiceImpl(PersistanceSession persistenceSession){
    this.persistenceSession = persistenceSession;
  }

  @Override
  public void startSession(UUID clientId, UUID computerId, UUID tariffId) {
    Session session = new Session(clientId, computerId, tariffId, LocalDateTime.now(), null, BigDecimal.ZERO, true);
    persistenceSession.addSession(session);
    persistenceSession.commit();
  }

  @Override
  public void endSession(UUID sessionId) {
    persistenceSession.getSession(sessionId).ifPresent(session -> {
      session.setActive(false);
      session.setEndTime(LocalDateTime.now());
      persistenceSession.commit();
    });
  }

  @Override
  public void addServiceToSession(UUID sessionId, UUID serviceId, int quantity) {
    Session session = persistenceSession.getSession(sessionId)
        .orElseThrow(() -> new RuntimeException("Session not found"));
    Service service = persistenceSession.getService(serviceId)
        .orElseThrow(() -> new RuntimeException("Service not found"));

    com.vladi.clubcontrolapp.domain.entities.SessionService sessionServiceItem =
        new com.vladi.clubcontrolapp.domain.entities.SessionService(sessionId, serviceId, quantity);

    BigDecimal serviceCost = service.getPrice().multiply(BigDecimal.valueOf(quantity));
    session.setTotalCost(session.getTotalCost().add(serviceCost));

    persistenceSession.addServiceToSession(session, sessionServiceItem);
    persistenceSession.commit();
  }

  @Override
  public Session create(Session entity) {
    persistenceSession.addSession(entity);
    persistenceSession.commit();
    return entity;
  }

  @Override
  public Session update(UUID uuid, Session entity) {
    persistenceSession.commit();
    return entity;
  }

  @Override
  public void delete(UUID uuid) {
    persistenceSession.getSession(uuid).ifPresent(persistenceSession::removeSession);
    persistenceSession.commit();
  }

  @Override
  public Optional<Session> findById(UUID uuid) {
    return persistenceSession.getSession(uuid);
  }
}
