package com.vladi.clubcontrolapp.presentation.controller;

import com.vladi.clubcontrolapp.Launcher;
import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.enums.ComputerStatus;
import com.vladi.clubcontrolapp.domain.enums.ComputerType;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import com.vladi.clubcontrolapp.presentation.util.NavigationManager;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddComputerController {
  @FXML private TextField numberField;
  @FXML private ComboBox<ComputerType> typeComboBox;
  @FXML private Label errorLabel;

  private final PersistanceSession session = Launcher.getSessionContext();

  @FXML
  public void initialize() {
    typeComboBox.setItems(FXCollections.observableArrayList(ComputerType.values()));
    typeComboBox.setValue(ComputerType.Common);
  }

  @FXML
  private void handleSave() {
    try {
      int number = Integer.parseInt(numberField.getText());
      ComputerType type = typeComboBox.getValue();

      Computer newPc = new Computer(
          UUID.randomUUID(),
          number,
          type.name(),
          ComputerStatus.Available.name()
      );

      session.addComputer(newPc);
      session.commit();

      handleCancel();
    } catch (NumberFormatException e) {
      errorLabel.setText("Номер має бути числом!");
    } catch (Exception e) {
      errorLabel.setText("Помилка при збереженні!");
      e.printStackTrace();
    }
  }

  @FXML
  private void handleCancel() {
    Stage stage = (Stage) numberField.getScene().getWindow();
    NavigationManager.navigate(stage, "/com/vladi/clubcontrolapp/views/main_menu.fxml");
  }
}
