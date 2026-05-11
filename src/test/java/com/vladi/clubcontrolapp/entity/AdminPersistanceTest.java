package com.vladi.clubcontrolapp.entity;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.vladi.clubcontrolapp.BasePersistenceTest;
import com.vladi.clubcontrolapp.domain.entities.Admin;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class AdminPersistanceTest extends BasePersistenceTest {

  @Test
  public void save_shouldRegisterAndFindAdmin(){
    UUID adminId = UUID.randomUUID();
    Admin admin = new Admin(adminId, "club", "1111");

    session.addAdmin(admin);
    session.commit();

    Optional<Admin> found = session.getAdminByLogin("club");
    assertTrue(found.isPresent(), "Адмін повинен бути знайдений у базі");
    assertEquals("club", found.get().getUsername());
    assertSame(admin, session.getAdmin(adminId).get(), "Об'єкт має бути отриманий з кешу, а не створений заново");
  }

  @Test
  public void deleteById_shouldRemoveAdmin_whenExists(){
    UUID adminId = UUID.randomUUID();
    Admin admin = new Admin(adminId, "bob", "1111");

    session.addAdmin(admin);
    session.commit();
    session.removeAdmin(admin);
    session.commit();

    assertTrue(session.getAdmin(admin.getId()).isEmpty());
  }

  @Test
  public void update_shouldUpdateAdmin_whenExists(){
    UUID adminId = UUID.randomUUID();
    Admin admin = new Admin(adminId, "bob", "1111");
    session.addAdmin(admin);
    admin.setUsername("cool");
    session.commit();

    session.updateAdmin(admin);
    session.commit();

    Optional<Admin> loaded = session.getAdmin(adminId);
    assertEquals("cool", loaded.get().getUsername());
  }
}
