package com.vladi.clubcontrolapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vladi.clubcontrolapp.BasePersistenceTest;
import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.enums.ComputerStatus;
import com.vladi.clubcontrolapp.domain.enums.ComputerType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class ComputerPersistanceTest extends BasePersistenceTest {
  @Test
  public void crud_operations_shouldWorkCorrectly() {
    UUID id = UUID.randomUUID();
    Computer computer = new Computer(id, 999, ComputerType.Common.name(), ComputerStatus.Available.name());

    session.addComputer(computer);
    session.commit();

    assertTrue(session.getComputer(id).isPresent());
    assertEquals(999, session.getComputer(id).get().getComputerNumber());

    computer.setComputerStatus(ComputerStatus.Maintenance.name());
    session.updateComputer(computer);
    session.commit();
    assertEquals(ComputerStatus.Maintenance.name(), session.getComputer(id).get().getComputerStatus());

    session.removeComputer(computer);
    session.commit();
    assertTrue(session.getComputer(id).isEmpty());
  }
}
