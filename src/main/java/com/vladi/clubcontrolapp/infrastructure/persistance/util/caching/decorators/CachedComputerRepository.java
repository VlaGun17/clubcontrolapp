package com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.decorators;


import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.enums.ComputerStatus;
import com.vladi.clubcontrolapp.domain.enums.ComputerType;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.ComputerRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.CachedJdbcRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CachedComputerRepository
    extends CachedJdbcRepository<Computer, UUID>
    implements ComputerRepository {

  private final ComputerRepository computerDelegate;

  public CachedComputerRepository(ComputerRepository delegate){
    super(delegate, Computer::getId);
    this.computerDelegate = delegate;
  }

  @Override
  public List<Computer> findByComputerType(ComputerType computerType) {
    List<Computer> computers = computerDelegate.findByComputerType(computerType);
    computers.forEach(entity -> identityMap.put(entity.getId(), entity));
    return computers;
  }

  @Override
  public List<Computer> findByComputerStatus(ComputerStatus computerStatus) {
    List<Computer> computers = computerDelegate.findByComputerStatus(computerStatus);
    computers.forEach(entity -> identityMap.put(entity.getId(), entity));
    return computers;
  }

  @Override
  public Optional<Computer> findByNumber(int number) {
    Optional<Computer> fromDb = computerDelegate.findByNumber(number);
    return fromDb.map(computer -> {
      Optional<Computer> cached = identityMap.get(computer.getId());
      if(cached.isPresent()) return cached.get();
      identityMap.put(computer.getId(), computer);
      return computer;
    });
  }
}
