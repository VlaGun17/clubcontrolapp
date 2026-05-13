package com.vladi.clubcontrolapp.presentation.controller;

import com.vladi.clubcontrolapp.Launcher;
import com.vladi.clubcontrolapp.application.contract.AuthService;
import com.vladi.clubcontrolapp.application.impl.AuthServiceImpl;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
  @FXML private TextField loginField;
  @FXML private PasswordField passwordField;
  @FXML private Label errorLabel;

  private final AuthService authService = new AuthServiceImpl(Launcher.getSessionContext());

  @FXML
  private void handleLogin() {
    String login = loginField.getText();
    String password = passwordField.getText();

    authService.login(login, password).ifPresentOrElse(
        admin -> switchToMainScene(),
        () -> errorLabel.setText("Невірний логін або пароль")
    );
  }

  @FXML
  private void handleShowRegistration() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/registration.fxml"));
    Parent root = loader.load();
    Stage stage = (Stage) loginField.getScene().getWindow();
    stage.setScene(new Scene(root));
  }

  private void switchToMainScene() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/main_menu.fxml"));
      Scene scene = new Scene(loader.load());
      Stage stage = (Stage) loginField.getScene().getWindow();
      stage.setScene(scene);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
