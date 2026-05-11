package com.vladi.clubcontrolapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vladi.clubcontrolapp.BasePersistenceTest;
import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.entities.Service;
import com.vladi.clubcontrolapp.domain.entities.Session;
import com.vladi.clubcontrolapp.domain.entities.SessionService;
import com.vladi.clubcontrolapp.domain.entities.Tariff;
import com.vladi.clubcontrolapp.domain.enums.ComputerStatus;
import com.vladi.clubcontrolapp.domain.enums.ComputerType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
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

  @Test
  public void addServiceToSession_shouldPersistCorrectly() {
    UUID clientId = UUID.randomUUID();
    UUID compId = UUID.randomUUID();
    UUID tariffId = UUID.randomUUID();
    UUID serviceId = UUID.randomUUID();
    UUID sessionId = UUID.randomUUID();

    session.addClient(new Client(clientId, "service_user", "service@mail.com", BigDecimal.ZERO, 0, 0, LocalDate.now()));
    session.addComputer(new Computer(compId, 202, ComputerType.VIP.name(), ComputerStatus.Available.name()));
    session.addTariff(new Tariff(tariffId, "Standard", BigDecimal.valueOf(100.0), true));

    Service coffee = new Service(serviceId, "Coffee", BigDecimal.valueOf(45.0));
    session.addService(coffee);

    Session gameSession = new Session(sessionId, clientId, compId, tariffId,
        LocalDateTime.now(), null, BigDecimal.ZERO, true);
    session.addSession(gameSession);

    session.commit();

    int quantity = 2;
    SessionService sessionServiceLink = new SessionService(sessionId, serviceId, quantity);

    session.addServiceToSession(gameSession, sessionServiceLink);
    session.getServicesForSession(gameSession.getId()).add(sessionServiceLink);
    gameSession.setTotalCost(BigDecimal.valueOf(90.0));
    session.updateSession(gameSession);

    session.commit();

    Optional<Session> updatedSession = session.getSession(sessionId);
    assertTrue(updatedSession.isPresent());
    assertEquals(0, BigDecimal.valueOf(90.0).compareTo(updatedSession.get().getTotalCost()), "Ціна сесії має бути 90.0");
    assertEquals(1, updatedSession.get().getServices().size());
    assertEquals(serviceId, updatedSession.get().getServices().get(0).getServiceId());
    assertSame(gameSession, updatedSession.get(), "Об'єкт повинен бути той самий з кешу");
  }
}
