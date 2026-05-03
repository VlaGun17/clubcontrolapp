package com.vladi.clubcontrolapp;

import javafx.application.Application;
import org.flywaydb.core.Flyway;

public class Launcher {

  public static void main(String[] args) {
    Application.launch(HelloApplication.class, args);
  }

  public void init() {
    Flyway flyway = Flyway.configure().dataSource("jdbc:h2:./cyberclub_db;DB_CLOSE_DELAY=-1", "sa", "").load();

    flyway.migrate();

    System.out.println("Database migrated successfully!");
  }
}
