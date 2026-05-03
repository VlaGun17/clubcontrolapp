module com.vladi.clubcontrolapp {
  requires javafx.controls;
  requires javafx.fxml;
  requires flyway.core;

  opens com.vladi.clubcontrolapp to javafx.fxml;
  exports com.vladi.clubcontrolapp;
}