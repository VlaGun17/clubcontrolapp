package com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.decorators;

import com.vladi.clubcontrolapp.domain.entities.SessionService;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.SessionServiceRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.CachedJdbcRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.IdentityMap;
import java.util.List;
import java.util.UUID;

public class CachedSessionServiceRepository implements SessionServiceRepository {

  private final SessionServiceRepository delegate;
  private final IdentityMap<String, SessionService> identityMap;

  public CachedSessionServiceRepository(SessionServiceRepository delegate) {
    this.delegate = delegate;
    this.identityMap = new IdentityMap<>();
  }

  @Override
  public void save(SessionService entity) {
    delegate.save(entity);
    identityMap.put(generateKey(entity.getSessionId(), entity.getServiceId()), entity);
  }

  @Override
  public List<SessionService> findBySessionId(UUID sessionId) {
    List<SessionService> services = delegate.findBySessionId(sessionId);
    for (SessionService entity : services) {
      String key = generateKey(entity.getSessionId(), entity.getServiceId());
      identityMap.put(key, entity);
    }
    return services;
  }

  private String generateKey(UUID sessionId, UUID serviceId) {
    return sessionId.toString() + ":" + serviceId.toString();
  }
}
