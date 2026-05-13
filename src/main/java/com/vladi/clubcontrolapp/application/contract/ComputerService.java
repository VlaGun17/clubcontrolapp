package com.vladi.clubcontrolapp.application.contract;

import com.vladi.clubcontrolapp.application.BaseService;
import com.vladi.clubcontrolapp.domain.entities.Computer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComputerService extends BaseService<Computer, UUID> {
  List<Computer> getAvailableComputers();
  Optional<Computer> getByNumber(int number);
  List<Computer> getAllComputers();
}
