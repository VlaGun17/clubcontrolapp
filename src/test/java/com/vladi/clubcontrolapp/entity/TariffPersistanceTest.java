package com.vladi.clubcontrolapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vladi.clubcontrolapp.BasePersistenceTest;
import com.vladi.clubcontrolapp.domain.entities.Tariff;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class TariffPersistanceTest extends BasePersistenceTest {
  @Test
  public void shouldHandleTariffLifecycle() {
    UUID id = UUID.randomUUID();
    Tariff tariff = new Tariff(id,"Night Pack", BigDecimal.valueOf(200.0), true);

    session.addTariff(tariff);
    session.commit();

    assertTrue(session.getTariff(id).isPresent());
    assertSame(tariff, session.getTariff(id).get());

    tariff.setPricePerHour(BigDecimal.valueOf(200.0));
    session.updateTariff(tariff);
    session.commit();

    assertEquals(0, BigDecimal.valueOf(200.0).compareTo(session.getTariff(id).get().getPricePerHour()));

    session.removeTariff(tariff);
    session.commit();
    assertTrue(session.getTariff(id).isEmpty());
  }
}
