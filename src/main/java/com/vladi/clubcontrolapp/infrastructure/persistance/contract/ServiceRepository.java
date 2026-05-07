package com.vladi.clubcontrolapp.infrastructure.persistance.contract;

import com.vladi.clubcontrolapp.domain.entities.Service;
import com.vladi.clubcontrolapp.infrastructure.persistance.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRepository extends Repository<Service, UUID> {
  Optional<Service> findByName(String name);
  List<Service> findByPriceRange(BigDecimal min, BigDecimal max);
}
