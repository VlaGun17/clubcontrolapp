module com.vladi.clubcontrolapp {
  requires javafx.controls;
  requires javafx.fxml;
  requires flyway.core;
  requires java.sql;
  requires tools.jackson.core;
  requires bcrypt;
  requires com.h2database;
  requires javafx.graphics;
  requires jakarta.mail;
  requires java.prefs;
  requires liqpay.sdk;
  requires java.desktop;

  opens com.vladi.clubcontrolapp to javafx.fxml;
  opens com.vladi.clubcontrolapp.presentation.controller to javafx.fxml;
  opens db.migration;
  exports com.vladi.clubcontrolapp;
}