package com.vladi.clubcontrolapp.presentation.controller;

import com.vladi.clubcontrolapp.Launcher;
import com.vladi.clubcontrolapp.domain.entities.Tariff;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import com.vladi.clubcontrolapp.presentation.util.OnDataChangeListener;
import java.math.BigDecimal;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class AddTariffController {
  @FXML private TextField nameField;
  @FXML private TextField priceField;
  @FXML private CheckBox nightCheckBox;
  @FXML private Label errorLabel;
  private Node overlayNode;
  private OnDataChangeListener listener;

  private final PersistanceSession session = Launcher.getSessionContext();

  @FXML
  private void handleSave(){
    try{
      String name = nameField.getText().trim();
      double price = Double.parseDouble(priceField.getText());
      boolean isNight = nightCheckBox.isSelected();

      if (name.isEmpty()) {
        errorLabel.setText("Введіть назву тарифу!");
        return;
      }

      Tariff newTariff = new Tariff(
          UUID.randomUUID(),
          name,
          BigDecimal.valueOf(price),
          isNight
      );

      session.addTariff(newTariff);
      session.commit();

      if(listener != null){
        listener.onDataChanged();
      }
      handleCancel();
    } catch (NumberFormatException e) {
      errorLabel.setText("Ціна повинна бути числом (наприклад: 45.5)");
    } catch (Exception e) {
      errorLabel.setText("Помилка при збереженні!");
      e.printStackTrace();
    }
  }

  @FXML
  private void handleCancel(){
    if (overlayNode != null && overlayNode.getParent() instanceof Pane) {
      ((Pane) overlayNode.getParent()).getChildren().remove(overlayNode);
    }
  }

  public void setOverlayNode(Node overlayNode) {
    this.overlayNode = overlayNode;
  }

  public void setOnDataChangedListener(OnDataChangeListener listener){
    this.listener = listener;
  }
}
