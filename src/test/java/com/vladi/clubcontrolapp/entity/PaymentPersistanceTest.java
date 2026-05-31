package com.vladi.clubcontrolapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vladi.clubcontrolapp.BasePersistenceTest;
import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.entities.Payment;
import com.vladi.clubcontrolapp.domain.entities.Session;
import com.vladi.clubcontrolapp.domain.entities.Tariff;
import com.vladi.clubcontrolapp.domain.enums.ComputerStatus;
import com.vladi.clubcontrolapp.domain.enums.ComputerType;
import com.vladi.clubcontrolapp.domain.enums.MethodPayment;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class PaymentPersistanceTest extends BasePersistenceTest {

  @Test
  public void payment_lifecycle_test() {
    UUID clientId = UUID.randomUUID();
    UUID compId = UUID.randomUUID();
    UUID tariffId = UUID.randomUUID();
    UUID sessionId = UUID.randomUUID();

    session.addClient(new Client(clientId, "payer", "p@mail.com", BigDecimal.ZERO, 0, 0, LocalDate.now()));
    session.addComputer(new Computer(compId, 202, ComputerType.Common.name(), ComputerStatus.Available.name()));
    session.addTariff(new Tariff(tariffId, "Standard", BigDecimal.valueOf(100.0), true));
    session.commit();

    session.addSession(new Session(sessionId, clientId, compId, tariffId, LocalDateTime.now(), null, BigDecimal.ZERO, true));
    session.commit();

    UUID paymentId = UUID.randomUUID();
    Payment payment = new Payment(paymentId, clientId, sessionId, BigDecimal.valueOf(500), LocalDateTime.now(),
        MethodPayment.Cash.name());

    session.addPayments(payment);
    session.commit();

    assertTrue(session.getPaymentBySession(sessionId).isPresent());
    assertSame(payment, session.getPaymentBySession(sessionId).get());
  }
}
