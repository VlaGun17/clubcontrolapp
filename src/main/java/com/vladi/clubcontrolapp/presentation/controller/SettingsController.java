package com.vladi.clubcontrolapp.presentation.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.prefs.Preferences;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class SettingsController {
  @FXML
  private VBox lockPane;
  @FXML private VBox settingsPane;
  @FXML private PasswordField passwordField;
  @FXML private Label errorLabel;

  @FXML private TextField publicKeyField;
  @FXML private PasswordField privateKeyField;

  private final Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);

  private static final String OWNER_PASSWORD = "admin666";

  private static final String PREF_PUBLIC_KEY = "liqpay_public_key";
  private static final String PREF_PRIVATE_KEY = "liqpay_private_key";

  @FXML
  public void initialize() {
    handleLockSettings();
  }

  @FXML
  private void handleUnlock() {
    String enteredPassword = passwordField.getText();

    if (OWNER_PASSWORD.equals(enteredPassword)) {
      errorLabel.setText("");
      passwordField.clear();

      lockPane.setVisible(false);
      settingsPane.setVisible(true);

      loadSettingsFromPreferences();
    } else {
      errorLabel.setText("Невірний пароль власника!");
    }
  }

  private void loadSettingsFromPreferences() {
    String publicKey = prefs.get(PREF_PUBLIC_KEY, "");
    String encodedPrivateKey = prefs.get(PREF_PRIVATE_KEY, "");

    publicKeyField.setText(publicKey);

    if (!encodedPrivateKey.isEmpty()) {
      try {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedPrivateKey);
        privateKeyField.setText(new String(decodedBytes, StandardCharsets.UTF_8));
      } catch (IllegalArgumentException e) {
        privateKeyField.clear();
      }
    } else {
      privateKeyField.clear();
    }
  }

  @FXML
  private void handleSaveSettings() {
    String publicKey = publicKeyField.getText().trim();
    String privateKey = privateKeyField.getText().trim();

    if (publicKey.isEmpty() || privateKey.isEmpty()) {
      showStyleAlert(Alert.AlertType.WARNING, "Увага", "Поля ключів не можуть бути порожніми.");
      return;
    }

    prefs.put(PREF_PUBLIC_KEY, publicKey);

    String encodedPrivateKey = Base64.getEncoder().encodeToString(privateKey.getBytes(StandardCharsets.UTF_8));
    prefs.put(PREF_PRIVATE_KEY, encodedPrivateKey);

    showStyleAlert(Alert.AlertType.INFORMATION, "Успіх", "Налаштування LiqPay успішно збережено на цьому ПК!");
  }

  @FXML
  private void handleLockSettings() {
    settingsPane.setVisible(false);
    lockPane.setVisible(true);
    publicKeyField.clear();
    privateKeyField.clear();
  }

  private void showStyleAlert(Alert.AlertType type, String title, String message) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public static String getSavedPublicKey() {
    Preferences instancePrefs = Preferences.userNodeForPackage(SettingsController.class);
    return instancePrefs.get(PREF_PUBLIC_KEY, "");
  }

  public static String getSavedPrivateKey() {
    Preferences instancePrefs = Preferences.userNodeForPackage(SettingsController.class);
    String encoded = instancePrefs.get(PREF_PRIVATE_KEY, "");
    if (encoded.isEmpty()) return "";
    try {
      return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    } catch (Exception e) {
      return "";
    }
  }
}
