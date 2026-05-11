package com.vladi.clubcontrolapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vladi.clubcontrolapp.BasePersistenceTest;
import com.vladi.clubcontrolapp.domain.entities.Service;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class ServicePersistanceTest extends BasePersistenceTest {
  @Test
  public void service_lifecycle_test() {
    UUID id = UUID.randomUUID();
    Service service = new Service(id, "Coffee", BigDecimal.valueOf(35.0));

    session.addService(service);
    session.commit();
    assertSame(service, session.getService(id).get());

    service.setName("Latte");
    session.updateService(service);
    session.commit();

    assertEquals("Latte", session.getServiceByName("Latte").get().getName());

    session.removeService(service);
    session.commit();
    assertTrue(session.getService(id).isEmpty());
  }
}
