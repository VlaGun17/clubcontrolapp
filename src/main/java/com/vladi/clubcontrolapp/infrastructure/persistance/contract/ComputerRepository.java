package com.vladi.clubcontrolapp.infrastructure.persistance.contract;

import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.enums.ComputerStatus;
import com.vladi.clubcontrolapp.domain.enums.ComputerType;
import com.vladi.clubcontrolapp.infrastructure.persistance.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComputerRepository extends Repository<Computer, UUID> {
  List<Computer> findByComputerType(ComputerType computerType);

  List<Computer> findByComputerStatus(ComputerStatus computerStatus);

  Optional<Computer> findByNumber(int number);
}
