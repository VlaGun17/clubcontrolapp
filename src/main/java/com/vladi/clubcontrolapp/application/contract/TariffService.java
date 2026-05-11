package com.vladi.clubcontrolapp.application.contract;

import com.vladi.clubcontrolapp.application.BaseService;
import com.vladi.clubcontrolapp.domain.entities.Tariff;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TariffService extends BaseService<Tariff, UUID> {
  Optional<Tariff> getCurrentTariff();
  List<Tariff> getNightTariffs();
  Optional<Tariff> findByName(String name);
}
