package com.vladi.clubcontrolapp;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/com/vladi/clubcontrolapp/views/login.fxml"));
    Scene scene = new Scene(fxmlLoader.load());
    stage.setTitle("ClubControl");
    stage.setScene(scene);
    stage.show();

    stage.setOnCloseRequest(windowEvent -> {
      Platform.exit();
      System.exit(0);
    });
  }
}
