package com.vladi.clubcontrolapp;

import com.vladi.clubcontrolapp.infrastructure.persistance.util.ConnectionManager;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.PoolConfig;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BasePersistenceTest {

  @Container
  protected  static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15-alpine")
          .withDatabaseName("club_control_test")
          .withUsername("sa")
          .withPassword("sa");

  protected PersistanceSession session;

  @BeforeAll
  static void runMigrations(){
    Flyway flyway = Flyway.configure()
        .dataSource(
            postgres.getJdbcUrl(),
            postgres.getUsername(),
            postgres.getPassword()
        ).load();

    flyway.migrate();
  }

  @BeforeEach
  void setup() {
    PoolConfig config = new PoolConfig(
        postgres.getJdbcUrl(),
        postgres.getUsername(),
        postgres.getPassword(),
        2,
        5,
        5000L
    );

    ConnectionManager connectionManager = new ConnectionManager(config);
    this.session = new PersistanceSession(connectionManager);
  }
}
