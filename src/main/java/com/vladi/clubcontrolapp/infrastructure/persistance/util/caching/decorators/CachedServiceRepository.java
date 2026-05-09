package com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.decorators;

import com.vladi.clubcontrolapp.domain.entities.Service;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.ServiceRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.CachedJdbcRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CachedServiceRepository
    extends CachedJdbcRepository<Service, UUID>
    implements ServiceRepository {

  private final ServiceRepository serviceDelegate;

  public CachedServiceRepository(ServiceRepository delegate){
    super(delegate, Service::getId);
    this.serviceDelegate = delegate;
  }

  @Override
  public Optional<Service> findByName(String name) {
    Optional<Service> service = serviceDelegate.findByName(name);
    service.ifPresent(entity -> identityMap.put(entity.getId(), entity));
    return service;
  }

  @Override
  public List<Service> findByPriceRange(BigDecimal min, BigDecimal max) {
    List<Service> services = serviceDelegate.findByPriceRange(min, max);
    services.forEach(entity -> identityMap.put(entity.getId(), entity));
    return services;
  }
}
