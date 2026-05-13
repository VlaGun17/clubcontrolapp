package com.vladi.clubcontrolapp.presentation.util;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationManager {
  public static void navigate(Stage stage, String fxmlPath) {
    try {
      FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
      Parent root = loader.load();
      stage.setScene(new Scene(root));
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
