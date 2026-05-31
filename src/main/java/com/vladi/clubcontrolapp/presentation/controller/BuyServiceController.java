package com.vladi.clubcontrolapp.presentation.controller;

import com.vladi.clubcontrolapp.Launcher;
import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.entities.Service;
import com.vladi.clubcontrolapp.domain.entities.Session;
import com.vladi.clubcontrolapp.domain.entities.SessionService;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class BuyServiceController {
  @FXML private Label serviceNameLabel;
  @FXML private Label servicePriceLabel;
  @FXML private ComboBox<String> activePcComboBox;
  @FXML private TextField quantityField;
  @FXML private Label errorLabel;

  private Service currentService;
  private MainMenuController mainMenuController;
  private final PersistanceSession dbSession = Launcher.getSessionContext();

  private Map<String, Computer> pcMap = new HashMap<>();

  public void initData(Service service, MainMenuController mainMenuController) {
    this.currentService = service;
    this.mainMenuController = mainMenuController;

    serviceNameLabel.setText(service.getName());
    servicePriceLabel.setText(String.format("%.2f ₴", service.getPrice()));

    loadActiveComputers();
  }

  private void loadActiveComputers(){
    activePcComboBox.getItems().clear();
    pcMap.clear();

    List<Computer> computers = dbSession.getAllComputers();
    boolean hasActive = false;

    for(Computer pc : computers){
      if("Busy".equalsIgnoreCase(pc.getComputerStatus())){
        String pcDisplayName = "PC-" + pc.getComputerNumber();
        activePcComboBox.getItems().add(pcDisplayName);
        pcMap.put(pcDisplayName, pc);
        hasActive = true;
      }
    }

    if (hasActive) {
      activePcComboBox.getSelectionModel().selectFirst();
    } else {
      activePcComboBox.setPromptText("Немає активних сесій");
      activePcComboBox.setDisable(true);
    }
  }

  @FXML
  private void handleConfirmPurchase(){
    errorLabel.setText("");
    String selectedPcName = activePcComboBox.getValue();

    if (selectedPcName == null) {
      errorLabel.setText("Оберіть комп'ютер!");
      return;
    }

    int quantity;
    try {
      quantity = Integer.parseInt(quantityField.getText().trim());
      if (quantity <= 0) throw new NumberFormatException();
    } catch (NumberFormatException e) {
      errorLabel.setText("Введіть коректну кількість (більше 0)!");
      return;
    }

    Computer selectedPc = pcMap.get(selectedPcName);

    Optional<Session> activeSessionOpt = dbSession.getAllSession().stream()
        .filter(s -> s.getComputerId().equals(selectedPc.getId()) && s.isActive())
        .findFirst();

    if (activeSessionOpt.isEmpty()) {
      errorLabel.setText("Не знайдено активної сесії для цього ПК!");
      return;
    }

    Session activeSession = activeSessionOpt.get();

    SessionService sessionService = new SessionService(activeSession.getId(), currentService.getId(), quantity);
    dbSession.addServiceToSession(activeSession,sessionService);

    BigDecimal totalServiceCost = currentService.getPrice().multiply(BigDecimal.valueOf(quantity));
    BigDecimal currentTotalCost = activeSession.getTotalCost() != null ? activeSession.getTotalCost() : BigDecimal.ZERO;

    activeSession.setTotalCost(currentTotalCost.add(totalServiceCost));
    dbSession.updateSession(activeSession);

    dbSession.commit();

    System.out.println("Продано " + quantity + "x " + currentService.getName() + " для " + selectedPcName);
    handleClose();
  }

  @FXML
  private void handleClose() {
    if (mainMenuController != null) {
      mainMenuController.hideRightPanel();
    }
  }
}
