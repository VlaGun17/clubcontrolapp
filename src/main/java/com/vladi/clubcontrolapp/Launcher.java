package com.vladi.clubcontrolapp;

import com.vladi.clubcontrolapp.domain.entities.Admin;
import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.entities.Payment;
import com.vladi.clubcontrolapp.domain.entities.Service;
import com.vladi.clubcontrolapp.domain.entities.Session;
import com.vladi.clubcontrolapp.domain.entities.Tariff;
import com.vladi.clubcontrolapp.domain.enums.ComputerStatus;
import com.vladi.clubcontrolapp.domain.enums.ComputerType;
import com.vladi.clubcontrolapp.domain.enums.MethodPayment;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.ConnectionManager;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.flywaydb.core.Flyway;
import org.h2.tools.Server;

public class Launcher {

  private static PersistanceSession context;
  private static ConnectionManager connectionManager;

  public static void main(String[] args) {
    try {
      String dbPath = "./cyberclub_db";
      runFlywayMigration(dbPath);

      Server webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8084").start();
      connectionManager = ConnectionManager.forH2(dbPath);

      context = new PersistanceSession(connectionManager);

      javafx.application.Application.launch(Application.class, args);

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connectionManager != null) {
        connectionManager.close();
      }
    }
  }

  private static void runFlywayMigration(String dbPath){
    String url = "jdbc:h2:" + dbPath + ";MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE";
    Flyway flyway = Flyway.configure()
        .dataSource(url, "sa", "")
        .locations("classpath:db/migration")
        .load();
    flyway.migrate();
  }

  public static PersistanceSession getSessionContext() {
    return context;
  }
}
