package com.vladi.clubcontrolapp.application.impl;

import com.vladi.clubcontrolapp.application.contract.ServiceService;
import com.vladi.clubcontrolapp.domain.entities.Service;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ServiceServiceImpl implements ServiceService {
  private final PersistanceSession session;

  public ServiceServiceImpl(PersistanceSession session) {
    this.session = session;
  }

  @Override
  public Service create(Service entity) {
    session.addService(entity);
    session.commit();
    return entity;
  }

  @Override
  public Service update(UUID id, Service entity) {
    session.updateService(entity);
    session.commit();
    return entity;
  }

  @Override
  public void delete(UUID id) {
    session.getService(id).ifPresent(service -> {
      session.removeService(service);
      session.commit();
    });
  }

  @Override
  public Optional<Service> findById(UUID id) {
    return session.getService(id);
  }

  @Override
  public Optional<Service> findByName(String name) {
    return session.getServiceByName(name);
  }

  @Override
  public List<Service> findByPriceRange(BigDecimal min, BigDecimal max) {
    return session.getServicesInPriceRange(min, max);
  }
}
