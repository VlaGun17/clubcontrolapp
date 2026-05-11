package com.vladi.clubcontrolapp.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vladi.clubcontrolapp.BasePersistenceTest;
import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.entities.Session;
import com.vladi.clubcontrolapp.domain.entities.Tariff;
import com.vladi.clubcontrolapp.domain.enums.ComputerStatus;
import com.vladi.clubcontrolapp.domain.enums.ComputerType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class SessionPersistanceTest extends BasePersistenceTest {
  @Test
  public void session_lifecycle_test() {
    UUID clientId = UUID.randomUUID();
    UUID compId = UUID.randomUUID();
    UUID tariffId = UUID.randomUUID();

    session.addClient(new Client(clientId, "gamer", "g@mail.com", BigDecimal.ZERO, 0, 0, LocalDate.now()));
    session.addComputer(new Computer(compId, 101, ComputerType.Common.name(), ComputerStatus.Available.name()));
    session.addTariff(new Tariff(tariffId, "Night", BigDecimal.valueOf(50.0), true));
    session.commit();

    // Тест сесії
    UUID sessionId = UUID.randomUUID();
    Session gameSession = new Session(sessionId, clientId, compId, tariffId,
        LocalDateTime.now(), null, BigDecimal.ZERO, true);

    session.addSession(gameSession);
    session.commit();

    assertTrue(session.getSession(sessionId).isPresent());
    assertSame(gameSession, session.getSession(sessionId).get());

    gameSession.setActive(false);
    session.updateSession(gameSession);
    session.commit();
    assertFalse(session.getSession(sessionId).get().isActive());

    session.removeSession(gameSession);
    session.commit();
    assertTrue(session.getSession(sessionId).isEmpty());
  }
}
