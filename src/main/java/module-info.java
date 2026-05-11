module com.vladi.clubcontrolapp {
  requires javafx.controls;
  requires javafx.fxml;
  requires flyway.core;
  requires java.sql;

  opens com.vladi.clubcontrolapp to javafx.fxml;
  opens db.migration;
  exports com.vladi.clubcontrolapp;
}