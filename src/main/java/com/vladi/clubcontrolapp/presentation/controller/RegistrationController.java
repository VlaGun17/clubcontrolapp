package com.vladi.clubcontrolapp.presentation.controller;

import com.vladi.clubcontrolapp.Launcher;
import com.vladi.clubcontrolapp.application.contract.RegistrationService;
import com.vladi.clubcontrolapp.application.exception.ValidationResult;
import com.vladi.clubcontrolapp.application.impl.RegistrationServiceImpl;
import com.vladi.clubcontrolapp.domain.entities.Admin;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistrationController {
  @FXML
  private TextField loginField;
  @FXML private PasswordField passwordField;
  @FXML private PasswordField confirmPasswordField;
  @FXML private Label errorLabel;

  private final PersistanceSession session = Launcher.getSessionContext();
  private final RegistrationService registrationService = new RegistrationServiceImpl(session);

  @FXML
  private void handleRegistration() {
    String login = loginField.getText();
    String pass = passwordField.getText();
    String confirm = confirmPasswordField.getText();

    if (login.isEmpty() || pass.isEmpty()) {
      errorLabel.setText("Поля не можуть бути порожніми!");
      return;
    }

    if (!pass.equals(confirm)) {
      errorLabel.setText("Паролі не збігаються!");
      return;
    }

    ValidationResult validationResult = registrationService.registerAdmin(login, pass);
    if (validationResult.hasErrors()) {
      String errorMessage = String.join("\n", validationResult.getErrors().values());

      errorLabel.setText(errorMessage);

      if (login.isEmpty()) {
        loginField.setStyle("-fx-border-color: #f44336; -fx-background-color: #2a2a32; -fx-text-fill: white;");
      }
    } else {
      errorLabel.setText("");
      handleShowLogin();
    }
  }

  @FXML
  private void handleShowLogin() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/login.fxml"));
      Stage stage = (Stage) loginField.getScene().getWindow();
      stage.setScene(new Scene(loader.load()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
