package com.vladi.clubcontrolapp.presentation.util;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationManager {
  public static Parent loadView(String fxmlPath) {
    try {
      FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(fxmlPath));
      return loader.load();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
