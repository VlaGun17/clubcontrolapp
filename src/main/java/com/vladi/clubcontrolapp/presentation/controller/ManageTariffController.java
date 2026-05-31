package com.vladi.clubcontrolapp.presentation.controller;

import com.vladi.clubcontrolapp.Launcher;
import com.vladi.clubcontrolapp.domain.entities.Tariff;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import java.math.BigDecimal;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ManageTariffController {
  @FXML private Label tariffTitleLabel;
  @FXML private Label currentPriceLabel;
  @FXML private Label errorLabel;

  @FXML
  private TextField nameField;
  @FXML private TextField priceField;
  @FXML private CheckBox isNightTariffCheckBox;

  private Tariff currentTariff;
  private MainMenuController mainMenuController;
  private final PersistanceSession dbSession = Launcher.getSessionContext();

  public void initData(Tariff tariff, MainMenuController mainMenuController) {
    this.currentTariff = tariff;
    this.mainMenuController = mainMenuController;

    tariffTitleLabel.setText(tariff.getName().toUpperCase());
    currentPriceLabel.setText(String.format("%.2f ₴ / год", tariff.getPricePerHour()));

    nameField.setText(tariff.getName());
    priceField.setText(tariff.getPricePerHour().toString());
    isNightTariffCheckBox.setSelected(tariff.isNight());

    errorLabel.setText("");
  }

  @FXML
  private void handleConfirmUpdate() {
    String newName = nameField.getText().trim();
    String priceStr = priceField.getText().trim();

    if (newName.isEmpty() || priceStr.isEmpty()) {
      errorLabel.setText("Всі поля мають бути заповнені!");
      return;
    }

    try {
      BigDecimal newPrice = new BigDecimal(priceStr.replace(",", "."));
      if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
        errorLabel.setText("Ціна не може бути від'ємною!");
        return;
      }

      currentTariff.setName(newName);
      currentTariff.setPricePerHour(newPrice);
      currentTariff.setNight(isNightTariffCheckBox.isSelected());

      dbSession.updateTariff(currentTariff);
      dbSession.commit();

      mainMenuController.refreshData();

      handleClose();

    } catch (NumberFormatException e) {
      errorLabel.setText("Некоректний формат ціни (використовуйте крапку)!");
    } catch (Exception e) {
      errorLabel.setText("Помилка при збереженні в базу даних.");
      e.printStackTrace();
    }
  }

  @FXML
  private void handleCancel() {
    handleClose();
  }

  @FXML
  private void handleClose() {
    if (mainMenuController != null) {
      mainMenuController.hideRightPanel();
    }
  }
}
