  package com.vladi.clubcontrolapp.presentation.controller;

  import com.vladi.clubcontrolapp.Launcher;
  import com.vladi.clubcontrolapp.application.contract.PaymentInvoiceService;
  import com.vladi.clubcontrolapp.application.impl.PaymentInvoiceServiceImpl;
  import com.vladi.clubcontrolapp.domain.entities.Client;
  import com.vladi.clubcontrolapp.domain.entities.Computer;
  import com.vladi.clubcontrolapp.domain.entities.Payment;
  import com.vladi.clubcontrolapp.domain.entities.Session;
  import com.vladi.clubcontrolapp.domain.entities.SessionService;
  import com.vladi.clubcontrolapp.domain.entities.Tariff;
  import com.vladi.clubcontrolapp.domain.enums.ComputerStatus;
  import com.vladi.clubcontrolapp.domain.enums.MethodPayment;
  import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
  import java.math.BigDecimal;
  import java.math.RoundingMode;
  import java.time.Duration;
  import java.time.LocalDate;
  import java.time.LocalDateTime;
  import java.util.List;
  import java.util.Optional;
  import java.util.UUID;
  import javafx.animation.FadeTransition;
  import javafx.animation.Interpolator;
  import javafx.animation.ParallelTransition;
  import javafx.animation.TranslateTransition;
  import javafx.fxml.FXML;
  import javafx.scene.control.Button;
  import javafx.scene.control.ComboBox;
  import javafx.scene.control.Label;
  import javafx.scene.control.TextField;
  import javafx.scene.layout.VBox;

  public class ManageComputerController {
    @FXML
    private Label pcNameLabel;
    @FXML private Label statusLabel;
    @FXML private Button startSessionBtn;
    @FXML private Button endSessionBtn;
    @FXML private VBox sessionFormBox;
    @FXML private Label errorLabel;

    @FXML private ComboBox<String> clientComboBox;
    @FXML private ComboBox<String> tariffComboBox;
    @FXML private TextField durationField;

    @FXML private VBox endSessionFormBox;
    @FXML private ComboBox<String> paymentMethodComboBox;
    @FXML private Label totalCostLabel;

    private Computer currentPc;
    private MainMenuController mainMenuController;
    private final PersistanceSession session = Launcher.getSessionContext();

    private Session activeSessionToClose;
    private BigDecimal calculatedTotalCost = BigDecimal.ZERO;

    public void initData(Computer pc, MainMenuController mainMenuController) {
      this.currentPc = pc;
      this.mainMenuController = mainMenuController;

      String dbStatusStr = pc.getComputerStatus();
      pcNameLabel.setText("PC-" + pc.getComputerNumber());
      statusLabel.setText(ComputerStatus.valueOf(dbStatusStr).getDisplayName());

      if (errorLabel != null) {
        errorLabel.setText("");
      }

      hideAllForms();
      loadClientsToComboBox();
      loadTariffsToComboBox();
      loadPaymentMethodComboBox();

      if ("Available".equalsIgnoreCase(pc.getComputerStatus())) {
        statusLabel.setStyle("-fx-text-fill: #4caf50;"); // Зелений
        startSessionBtn.setDisable(false);
        endSessionBtn.setDisable(true);
      } else {
        statusLabel.setStyle("-fx-text-fill: #f44336;"); // Червоний
        startSessionBtn.setDisable(true);
        endSessionBtn.setDisable(false);
      }
    }

    private void hideAllForms(){
      if(sessionFormBox != null){sessionFormBox.setVisible(false); sessionFormBox.setManaged(false); sessionFormBox.setTranslateY(0); sessionFormBox.setOpacity(1.0);}
      if(endSessionFormBox != null){endSessionFormBox.setVisible(false); endSessionFormBox.setManaged(false); endSessionFormBox.setTranslateY(0); endSessionFormBox.setOpacity(1.0);}
    }

    private void loadClientsToComboBox(){
      clientComboBox.getItems().clear();

      List<Client> allClients = session.getAllClients();
      for (Client client : allClients) {
        if(!client.getNickname().startsWith("[BANNED]")){
          clientComboBox.getItems().add(client.getNickname());
        }
      }

      clientComboBox.getSelectionModel().selectFirst();
    }

    private void loadTariffsToComboBox() {
      tariffComboBox.getItems().clear();
      List<Tariff> allTariffs = session.getAllTariffs();
      for(Tariff tariff : allTariffs){
        tariffComboBox.getItems().add(tariff.getName());
      }

      tariffComboBox.getSelectionModel().selectFirst();
    }

    private void loadPaymentMethodComboBox(){
      paymentMethodComboBox.getItems().clear();
      paymentMethodComboBox.getItems().add(MethodPayment.Balance.getDisplayName());
      paymentMethodComboBox.getItems().add(MethodPayment.Card.getDisplayName());
      paymentMethodComboBox.getItems().add(MethodPayment.Cash.getDisplayName());
      tariffComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleShowSessionForm() {
      showAnimation(50, 0, sessionFormBox);
    }

    @FXML
    private void handleCancelSessionForm() {
      hideAnimation(0, 50, sessionFormBox);
    }

    @FXML
    private void handleConfirmSession(){
      String selectedClientNickname = clientComboBox.getValue();
      String selectedTariff = tariffComboBox.getValue();
      String durationStr = durationField.getText().trim();

      if (durationStr.isEmpty()) {
        System.out.println("Помилка: Введіть тривалість сесії!");
        return;
      }

      double hours = Double.parseDouble(durationStr.replace(",", "."));
      long minutesToAdd = (long) (hours * 60);
      LocalDateTime endTime = LocalDateTime.now().plusMinutes(minutesToAdd);

      Optional<Client> foundClient = session.getClientByLogin(selectedClientNickname);

      UUID clientId = null;
      if (foundClient.isPresent()) {
        clientId = foundClient.get().getId();
      } else {
        System.out.println("Клієнта не знайдено в базі!");
        return;
      }

      String rawNickname = foundClient.get().getNickname();
      if (rawNickname != null && rawNickname.startsWith("[BANNED]")){
        errorLabel.setText("Даний користувач заблокований!");
        return;
      }

      Optional<Tariff> foundTariff = session.getTariffByName(selectedTariff);

      if (foundTariff.isEmpty()) {
        System.out.println("Тариф не знайдено!");
        return;
      }

      Session newSession = new Session(
          UUID.randomUUID(),
          clientId,
          currentPc.getId(),
          foundTariff.get().getId(),
          LocalDateTime.now(),
          endTime,
          BigDecimal.ZERO,
          true
      );

      session.addSession(newSession);
      currentPc.setComputerStatus(ComputerStatus.Busy.name());
      session.updateComputer(currentPc);
      session.commit();

      startSessionBtn.setDisable(true);
      endSessionBtn.setDisable(false);
      statusLabel.setText("BUSY");
      statusLabel.setStyle("-fx-text-fill: #f44336;");

      hideAnimation(0, 50, sessionFormBox);
    }

    @FXML
    private void handleShowEndSession() {
      Optional<Computer> freshPc = session.getAllComputers().stream()
          .filter(c -> c.getId().equals(currentPc.getId()))
          .findFirst();

      if (freshPc.isPresent()) {
        currentPc = freshPc.get();
      }

      if (!"Busy".equalsIgnoreCase(currentPc.getComputerStatus())) {
        errorLabel.setText("Цей ПК вже вільний — сесію завершено або переведено.");
        startSessionBtn.setDisable(false);
        endSessionBtn.setDisable(true);
        statusLabel.setText("AVAILABLE");
        statusLabel.setStyle("-fx-text-fill: #4caf50;");
        return;
      }

      Optional<Session> activeSessionOpt = session.getAllSession().stream()
          .filter(s -> s.getComputerId().equals(currentPc.getId()) && s.isActive())
          .findFirst();

      if (activeSessionOpt.isEmpty()) {
        errorLabel.setText("Активну сесію для цього ПК не знайдено!");
        return;
      }

      activeSessionToClose = activeSessionOpt.get();

        UUID tariffId = activeSessionToClose.getTariffId();
        UUID clientId = activeSessionToClose.getClientId();
        Optional<Tariff> tariff = session.getTariff(tariffId);
        Optional<Client> client = session.getClient(clientId);
        if(tariff.isPresent()){
          BigDecimal pricePerHour = tariff.get().getPricePerHour();
          Duration duration = Duration.between(activeSessionToClose.getStartTime(), LocalDateTime.now());
          double hoursPassed = duration.toMinutes() / 60.0;

          if (hoursPassed < 0.01) hoursPassed = 0.01;

          BigDecimal cost = pricePerHour.multiply(BigDecimal.valueOf(hoursPassed));
          BigDecimal oldTotalCost = activeSessionToClose.getTotalCost();
          if (oldTotalCost == null) {
            oldTotalCost = BigDecimal.ZERO;
          }
          calculatedTotalCost = oldTotalCost.add(cost);

          if(client.get().getDiscountPercent() > 0){
            BigDecimal discountMultiplier = BigDecimal.valueOf(client.get().getDiscountPercent())
                .divide(BigDecimal.valueOf(100));
            BigDecimal discountValue = calculatedTotalCost.multiply(discountMultiplier);
            calculatedTotalCost = calculatedTotalCost.subtract(discountValue);
          }

          calculatedTotalCost = calculatedTotalCost.setScale(2, RoundingMode.HALF_UP);

          totalCostLabel.setText(String.format("%.2f ₴", calculatedTotalCost));
        }

      showAnimation(50, 0, endSessionFormBox);
    }

    @FXML
    private void handleCancelEndSessionForm() {
      activeSessionToClose = null;
      hideAnimation(0, 50, endSessionFormBox);
    }

    @FXML
    private void handleConfirmEndSession(){
      if (currentPc == null || activeSessionToClose == null){
        errorLabel.setText("Комп'ютер або сесія відсутні!");
        return;
      }
      String selectedPaymentMethod = paymentMethodComboBox.getValue();
      if (selectedPaymentMethod == null) {
        errorLabel.setText("Будь ласка, оберіть метод оплати!");
        return;
      }

      LocalDateTime now = LocalDateTime.now();
      errorLabel.setText("");

      UUID clientId = activeSessionToClose.getClientId();

      if (MethodPayment.Balance.getDisplayName().equals(selectedPaymentMethod)) {
        Optional<Client> clientOpt = session.getClient(clientId);
        if(clientOpt.isPresent()){
          Client client = clientOpt.get();
          int visitCount = client.getVisitCount();
          BigDecimal currentBalance = client.getBalance();
          if (currentBalance == null) currentBalance = BigDecimal.ZERO;
          if(currentBalance.compareTo(calculatedTotalCost) < 0){
            errorLabel.setText("Недостатньо коштів на балансі.");
            return;
          }

          client.setBalance(currentBalance.subtract(calculatedTotalCost));
          client.setVisitCount(visitCount + 1);
          session.updateClient(client);
        }
      } else if (MethodPayment.Cash.getDisplayName().equals(selectedPaymentMethod)){
        System.out.println("Оплата проведена готівкою (в касу клубу): " + calculatedTotalCost + " ₴");
      } else if (MethodPayment.Card.getDisplayName().equals(selectedPaymentMethod)) {
        Optional<Client> clientOpt = session.getClient(clientId);
        Client client = clientOpt.get();
        String clientEmail = client.getEmail();

        if (clientEmail == null || clientEmail.trim().isEmpty()) {
          errorLabel.setText("У клієнта не вказано email для відправки рахунку!");
          return;
        }

        String orderId = "INV-" + activeSessionToClose.getId().toString().substring(0, 8);
        String amountStr = calculatedTotalCost.setScale(2, RoundingMode.HALF_UP).toString();

        Thread sendInvoiceThread = new Thread(() -> {
          try{
            PaymentInvoiceService paymentInvoiceService = new PaymentInvoiceServiceImpl();
            paymentInvoiceService.sendInvoiceToEmail(client.getEmail(), amountStr, orderId);

            javafx.application.Platform.runLater(() -> {
              System.out.println("Інвойс LiqPay успішно згенеровано для " + clientEmail);
            });
          } catch (Exception e) {
            javafx.application.Platform.runLater(() -> {
              System.err.println("Помилка під час виставлення рахунку:");
              e.printStackTrace();
              errorLabel.setText("Не вдалося надіслати рахунок: " + e.getMessage());
            });
          }
        });
        sendInvoiceThread.setDaemon(true);
        sendInvoiceThread.start();
      }

      currentPc.setComputerStatus(ComputerStatus.Available.name());
      session.updateComputer(currentPc);

      activeSessionToClose.setActive(false);
      activeSessionToClose.setEndTime(now);
      activeSessionToClose.setTotalCost(calculatedTotalCost);
      session.updateSession(activeSessionToClose);

      MethodPayment methodPayment = MethodPayment.fromDisplayName(selectedPaymentMethod);

      Payment newPayment = new Payment(
          UUID.randomUUID(),
          clientId,
          activeSessionToClose.getId(),
          calculatedTotalCost,
          now,
          methodPayment.name()
      );

      session.addPayments(newPayment);
      session.commit();

      startSessionBtn.setDisable(false);
      endSessionBtn.setDisable(true);
      statusLabel.setText("AVAILABLE");
      statusLabel.setStyle("-fx-text-fill: #4caf50;");

      hideAnimation(0, 50, endSessionFormBox);
    }

    @FXML
    private void handleClose() {
      if (mainMenuController != null) {
        mainMenuController.hideRightPanel();
      }
    }

    private void showAnimation(double from, double to, VBox formBox){
      hideAllForms();

      formBox.setVisible(true);
      formBox.setManaged(true);

      TranslateTransition slideUp = new TranslateTransition(javafx.util.Duration.millis(250), formBox);
      slideUp.setFromY(from);
      slideUp.setToY(to);
      slideUp.setInterpolator(Interpolator.EASE_OUT);

      FadeTransition fadeIn = new FadeTransition(javafx.util.Duration.millis(250), formBox);
      fadeIn.setFromValue(0.0);
      fadeIn.setToValue(1.0);

      ParallelTransition showAnimation = new ParallelTransition(slideUp, fadeIn);
      showAnimation.play();
    }

    private void hideAnimation(double from, double to, VBox formBox){
      TranslateTransition slideUp = new TranslateTransition(javafx.util.Duration.millis(250), formBox);
      slideUp.setFromY(from);
      slideUp.setToY(to);
      slideUp.setInterpolator(Interpolator.EASE_OUT);

      FadeTransition fadeIn = new FadeTransition(javafx.util.Duration.millis(250), formBox);
      fadeIn.setFromValue(1.0);
      fadeIn.setToValue(0.0);

      ParallelTransition showAnimation = new ParallelTransition(slideUp, fadeIn);
      showAnimation.setOnFinished(actionEvent -> {
        formBox.setVisible(false);
        formBox.setManaged(false);
        formBox.setTranslateY(0);

        if (mainMenuController != null) {
          mainMenuController.refreshData(); // Синхронізуємо сітку комп'ютерів
        }
      });
      showAnimation.play();
    }
  }
