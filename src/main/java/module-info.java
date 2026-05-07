module com.vladi.clubcontrolapp {
  requires javafx.controls;
  requires javafx.fxml;
  requires flyway.core;
  requires java.sql;
  requires com.vladi.clubcontrolapp;

  opens com.vladi.clubcontrolapp to javafx.fxml;
  exports com.vladi.clubcontrolapp;
}