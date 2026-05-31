package com.vladi.clubcontrolapp.presentation.controller;

import com.vladi.clubcontrolapp.Launcher;
import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import com.vladi.clubcontrolapp.presentation.util.OnDataChangeListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class AddClientController {
  @FXML private TextField nicknameField;
  @FXML private TextField emailField;
  @FXML private Label errorLabel;
  private Node overlayNode;
  private OnDataChangeListener listener;

  private final PersistanceSession session = Launcher.getSessionContext();

  @FXML
  private void handleSave(){
    try{
      String nickname = nicknameField.getText();
      String email = emailField.getText();

      if (nickname.isEmpty() || email.isEmpty()) {
        errorLabel.setText("Будь ласка, заповніть усі поля!");
        return;
      }

      Client newClient = new Client(
          UUID.randomUUID(),
          nickname,
          email,
          BigDecimal.ZERO,
          0,
          0,
          LocalDate.now()
      );

      session.addClient(newClient);
      session.commit();

      if(listener != null){
        listener.onDataChanged();
      }
      handleCancel();
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
