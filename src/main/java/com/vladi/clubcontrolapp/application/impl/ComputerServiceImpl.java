package com.vladi.clubcontrolapp.application.impl;

import com.vladi.clubcontrolapp.application.contract.ComputerService;
import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.enums.ComputerStatus;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ComputerServiceImpl implements ComputerService {
  private final PersistanceSession session;

  public ComputerServiceImpl(PersistanceSession session) {
    this.session = session;
  }

  @Override
  public List<Computer> getAvailableComputers() {
    return session.getComputerByStatus(ComputerStatus.Available);
  }

  @Override
  public Optional<Computer> getByNumber(int number) {
    return session.getComputerByNumber(number);
  }

  @Override
  public Computer create(Computer entity) {
    session.addComputer(entity);
    session.commit();
    return entity;
  }

  @Override
  public Computer update(UUID id, Computer entity) {
    session.updateComputer(entity);
    session.commit();
    return entity;
  }

  @Override
  public void delete(UUID id) {
    session.getComputer(id).ifPresent(c -> {
      session.removeComputer(c);
      session.commit();
    });
  }

  @Override
  public Optional<Computer> findById(UUID id) {
    return session.getComputer(id);
  }
}
