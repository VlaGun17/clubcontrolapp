package com.vladi.clubcontrolapp;

import com.vladi.clubcontrolapp.infrastructure.persistance.util.ConnectionManager;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import javafx.application.Application;
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

      Application.launch(HelloApplication.class, args);

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
