package com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.decorators;

import com.vladi.clubcontrolapp.domain.entities.Session;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.SessionRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.CachedJdbcRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CachedSessionRepository
    extends CachedJdbcRepository<Session, UUID>
    implements SessionRepository {

  private final SessionRepository sessionDelegate;

  public CachedSessionRepository(SessionRepository delegate){
    super(delegate, Session::getId);
    this.sessionDelegate = delegate;
  }

  @Override
  public List<Session> findAllActive() {
    List<Session> sessions = sessionDelegate.findAllActive();
    sessions.forEach(entity -> identityMap.put(entity.getId(), entity));
    return sessions;
  }

  @Override
  public List<Session> findByComputerId(UUID computerId) {
    List<Session> sessions = sessionDelegate.findByComputerId(computerId);
    sessions.forEach(entity -> identityMap.put(entity.getId(), entity));
    return sessions;
  }

  @Override
  public List<Session> findByDate(LocalDateTime date) {
    List<Session> sessions = sessionDelegate.findByDate(date);
    sessions.forEach(entity -> identityMap.put(entity.getId(), entity));
    return sessions;
  }
}
