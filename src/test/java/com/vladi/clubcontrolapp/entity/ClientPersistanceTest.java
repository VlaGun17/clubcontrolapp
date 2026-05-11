package com.vladi.clubcontrolapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vladi.clubcontrolapp.BasePersistenceTest;
import com.vladi.clubcontrolapp.domain.entities.Client;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class ClientPersistanceTest extends BasePersistenceTest {

  @Test
  public void save_shouldRegisterAndFindClient() {
    UUID clientId = UUID.randomUUID();
    Client client = new Client(clientId, "bob", "bob@gmail.com", BigDecimal.valueOf(2000.0), 10, 10, LocalDate.now());

    session.addClient(client);
    session.commit();

    Optional<Client> found = session.getClient(clientId);
    assertTrue(found.isPresent());
    assertEquals("bob", found.get().getNickname());
    assertSame(client, found.get());
  }

  @Test
  public void update_shouldUpdateClient_whenExists() {
    UUID clientId = UUID.randomUUID();
    Client client = new Client(clientId, "original", "upd@mail.com", BigDecimal.ZERO, 0, 0, LocalDate.now());
    session.addClient(client);
    session.commit();

    client.setNickname("updated_nick");
    session.updateClient(client);
    session.commit();

    assertEquals("updated_nick", session.getClient(clientId).get().getNickname());
  }

  @Test
  public void delete_shouldRemoveClient_whenExists() {
    UUID clientId = UUID.randomUUID();
    Client client = new Client(clientId, "to_del", "del@mail.com", BigDecimal.ZERO, 0, 0, LocalDate.now());
    session.addClient(client);
    session.commit();

    session.removeClient(client);
    session.commit();

    assertTrue(session.getClient(clientId).isEmpty());
  }
}
