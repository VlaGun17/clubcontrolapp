package com.vladi.clubcontrolapp.application.impl;

import com.vladi.clubcontrolapp.application.contract.TariffService;
import com.vladi.clubcontrolapp.domain.entities.Tariff;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TariffServiceImpl implements TariffService {
  private final PersistanceSession session;

  public TariffServiceImpl(PersistanceSession session) {
    this.session = session;
  }

  @Override
  public Tariff create(Tariff entity) {
    session.addTariff(entity);
    session.commit();
    return entity;
  }

  @Override
  public Tariff update(UUID id, Tariff entity) {
    session.updateTariff(entity);
    session.commit();
    return entity;
  }

  @Override
  public void delete(UUID id) {
    session.getTariff(id).ifPresent(tariff -> {
      session.removeTariff(tariff);
      session.commit();
    });
  }

  @Override
  public Optional<Tariff> findById(UUID id) {
    return session.getTariff(id);
  }

  @Override
  public Optional<Tariff> getCurrentTariff() {
    return session.getCurrentTariff();
  }

  @Override
  public List<Tariff> getNightTariffs() {
    return session.getNightTariffs();
  }

  @Override
  public Optional<Tariff> findByName(String name) {
    return session.getTariffByName(name);
  }
}
