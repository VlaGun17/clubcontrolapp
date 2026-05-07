package com.vladi.clubcontrolapp.infrastructure.persistance.contract;

import com.vladi.clubcontrolapp.domain.entities.Tariff;
import com.vladi.clubcontrolapp.infrastructure.persistance.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TariffRepository extends Repository<Tariff, UUID> {
  Optional<Tariff> findCurrentTariff(LocalDate now);

  List<Tariff> findNightTariffs();

  Optional<Tariff> findByName(String name);
}
