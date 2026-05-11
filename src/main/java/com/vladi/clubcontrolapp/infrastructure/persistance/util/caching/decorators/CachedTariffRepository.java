package com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.decorators;

import com.vladi.clubcontrolapp.domain.entities.Tariff;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.TariffRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.CachedJdbcRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CachedTariffRepository
    extends CachedJdbcRepository<Tariff, UUID>
    implements TariffRepository {

  private final TariffRepository tariffDelegate;

  public CachedTariffRepository(TariffRepository delegate){
    super(delegate, Tariff::getId);
    this.tariffDelegate = delegate;
  }

  @Override
  public Optional<Tariff> findCurrentTariff(LocalDate now) {
    Optional<Tariff> fromDb = tariffDelegate.findCurrentTariff(now);
    return fromDb.map(tariff -> {
      Optional<Tariff> cached = identityMap.get(tariff.getId());
      if(cached.isPresent()) return cached.get();
      identityMap.put(tariff.getId(), tariff);
      return tariff;
    });
  }

  @Override
  public List<Tariff> findNightTariffs() {
    List<Tariff> tariffs = tariffDelegate.findNightTariffs();
    tariffs.forEach(entity -> identityMap.put(entity.getId(), entity));
    return tariffs;
  }

  @Override
  public Optional<Tariff> findByName(String name) {
    Optional<Tariff> fromDb = tariffDelegate.findByName(name);
    return fromDb.map(tariff -> {
      Optional<Tariff> cached = identityMap.get(tariff.getId());
      if(cached.isPresent()) return cached.get();
      identityMap.put(tariff.getId(), tariff);
      return tariff;
    });
  }
}
