package com.vladi.clubcontrolapp.application.contract;

import com.vladi.clubcontrolapp.application.BaseService;
import com.vladi.clubcontrolapp.domain.entities.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceService extends BaseService<Service, UUID> {
  Optional<Service> findByName(String name);
  List<Service> findByPriceRange(BigDecimal min, BigDecimal max);
}
