package com.vladi.clubcontrolapp.presentation.controller;

import com.vladi.clubcontrolapp.Launcher;
import com.vladi.clubcontrolapp.application.contract.EmailService;
import com.vladi.clubcontrolapp.application.contract.PaymentInvoiceService;
import com.vladi.clubcontrolapp.application.impl.EmailServiceImpl;
import com.vladi.clubcontrolapp.application.impl.PaymentInvoiceServiceImpl;
import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.entities.Payment;
import com.vladi.clubcontrolapp.domain.entities.Session;
import com.vladi.clubcontrolapp.domain.entities.Tariff;
import com.vladi.clubcontrolapp.domain.enums.ComputerStatus;
import com.vladi.clubcontrolapp.domain.enums.MethodPayment;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ManageClientController {
  @FXML private Label clientNicknameLabel;
  @FXML private Label balanceLabel;
  @FXML private Label statusLabel;
  @FXML private Label errorLabel;
  @FXML private VBox userInfoFormBox;
  @FXML private Label infoNicknameLabel;
  @FXML private Label infoEmailLabel;
  @FXML private Label infoBalanceLabel;
  @FXML private Label infoDiscountLabel;
  @FXML private Label infoVisitCountLabel;
  @FXML private Label infoRegistrationDateLabel;

  @FXML private VBox activeSessionBox;
  @FXML private VBox idleSessionBox;

  @FXML private VBox startSessionFormBox;
  @FXML private ComboBox<String> computerComboBox;
  @FXML private ComboBox<String> tariffComboBox;
  @FXML private TextField durationField;

  @FXML private VBox endSessionFormBox;
  @FXML private ComboBox<String> paymentMethodComboBox;
  @FXML private Label totalCostLabel;

  @FXML private VBox transferFormBox;
  @FXML private ComboBox<String> targetComputerComboBox;

  @FXML private VBox topUpFormBox;
  @FXML private TextField topUpAmountField;
  @FXML private ComboBox<String> topUpMethodComboBox;

  @FXML private VBox editProfileFormBox;
  @FXML private TextField editNicknameField;
  @FXML private TextField editEmailField;
  @FXML private TextField editDiscountField;

  private Client currentClient;
  private MainMenuController mainMenuController;
  private final PersistanceSession dbSession = Launcher.getSessionContext();
  EmailService emailService = new EmailServiceImpl();

  private Session activeSessionToClose;
  private BigDecimal calculatedTotalCost = BigDecimal.ZERO;

  public void initData(Client client, MainMenuController mainMenuController){
    this.currentClient = client;
    this.mainMenuController = mainMenuController;

    refreshClientFields();
    hideAllForms();
    checkActiveSession();
  }

  private void refreshClientFields() {
    String rawNickname = currentClient.getNickname();
    if (rawNickname != null && rawNickname.startsWith("[BANNED]")){
      String cleanNickname = rawNickname.replace("[BANNED]", "").trim();
      clientNicknameLabel.setText(cleanNickname);
      clientNicknameLabel.setTextFill(javafx.scene.paint.Color.web("#7f8c8d"));
    } else {
      clientNicknameLabel.setText(currentClient.getNickname());
    }
    balanceLabel.setText(String.format("%.2f ₴", currentClient.getBalance()));
    errorLabel.setText("");
  }

  private void checkActiveSession() {
    Optional<Session> activeSessionOpt = dbSession.getAllSession().stream()
        .filter(s -> s.getClientId().equals(currentClient.getId()) && s.isActive())
        .findFirst();

    if (activeSessionOpt.isPresent()) {
      Session activeSession = activeSessionOpt.get();
      Optional<Computer> pcOpt = dbSession.getAllComputers().stream()
          .filter(c -> c.getId().equals(activeSession.getComputerId()))
          .findFirst();

      String pcNumber = pcOpt.map(computer -> String.valueOf(computer.getComputerNumber())).orElse("?");

      statusLabel.setText("У грі (ПК-" + pcNumber + ")");
      statusLabel.setStyle("-fx-text-fill: #4cc9f0;"); // Ціановий

      activeSessionBox.setVisible(true);
      activeSessionBox.setManaged(true);
      idleSessionBox.setVisible(false);
      idleSessionBox.setManaged(false);
    } else {
      statusLabel.setText("Відпочиває");
      statusLabel.setStyle("-fx-text-fill: #ccc;"); // Сірий

      activeSessionBox.setVisible(false);
      activeSessionBox.setManaged(false);
      idleSessionBox.setVisible(true);
      idleSessionBox.setManaged(true);
    }
  }

  private void hideAllForms() {
    if(startSessionFormBox != null) { startSessionFormBox.setVisible(false); startSessionFormBox.setManaged(false); startSessionFormBox.setTranslateY(0); startSessionFormBox.setOpacity(1.0);}
    if(endSessionFormBox != null) { endSessionFormBox.setVisible(false); endSessionFormBox.setManaged(false); endSessionFormBox.setTranslateY(0); endSessionFormBox.setOpacity(1.0);}
    if(transferFormBox != null) { transferFormBox.setVisible(false); transferFormBox.setManaged(false); transferFormBox.setTranslateY(0); transferFormBox.setOpacity(1.0);}
    if(topUpFormBox != null) { topUpFormBox.setVisible(false); topUpFormBox.setManaged(false); topUpFormBox.setTranslateY(0); topUpFormBox.setOpacity(1.0);}
    if(editProfileFormBox != null) { editProfileFormBox.setVisible(false); editProfileFormBox.setManaged(false); editProfileFormBox.setTranslateY(0); editProfileFormBox.setOpacity(1.0);}
    if(userInfoFormBox != null) {userInfoFormBox.setVisible(false); userInfoFormBox.setManaged(false); userInfoFormBox.setTranslateY(0); userInfoFormBox.setOpacity(1.0);}
  }

  @FXML
  private void handleStartSession() {
    hideAllForms();

    String rawNickname = currentClient.getNickname();
    if (rawNickname != null && rawNickname.startsWith("[BANNED]")){
      errorLabel.setText("Даний користувач заблокований!");
      return;
    }

    computerComboBox.getItems().clear();
    List<Computer> availablePcs = dbSession.getAllComputers().stream()
        .filter(c -> "Available".equalsIgnoreCase(c.getComputerStatus()))
        .toList();
    for (Computer pc : availablePcs) {
      computerComboBox.getItems().add("ПК-" + pc.getComputerNumber());
    }
    if (!computerComboBox.getItems().isEmpty()) computerComboBox.getSelectionModel().selectFirst();

    tariffComboBox.getItems().clear();
    for (Tariff t : dbSession.getAllTariffs()) {
      tariffComboBox.getItems().add(t.getName());
    }
    if (!tariffComboBox.getItems().isEmpty()) tariffComboBox.getSelectionModel().selectFirst();

    startSessionFormBox.setVisible(true);
    startSessionFormBox.setManaged(true);
    showAnimation(50, 0, startSessionFormBox);
  }

  @FXML
  private void handleConfirmStartSession() {
    String selectedPcStr = computerComboBox.getValue();
    String selectedTariff = tariffComboBox.getValue();
    String durationStr = durationField.getText().trim();

    if (selectedPcStr == null || selectedTariff == null || durationStr.isEmpty()) {
      errorLabel.setText("Заповніть усі поля форми!");
      return;
    }

    try {
      int pcNumber = Integer.parseInt(selectedPcStr.replace("ПК-", ""));
      double hours = Double.parseDouble(durationStr.replace(",", "."));
      long minutesToAdd = (long) (hours * 60);
      LocalDateTime endTime = LocalDateTime.now().plusMinutes(minutesToAdd);

      Optional<Computer> foundPc = dbSession.getAllComputers().stream()
          .filter(c -> c.getComputerNumber() == pcNumber)
          .findFirst();
      Optional<Tariff> foundTariff = dbSession.getTariffByName(selectedTariff);

      if (foundPc.isEmpty() || foundTariff.isEmpty()) {
        errorLabel.setText("Помилка: Комп'ютер або Тариф не знайдено!");
        return;
      }

      Session newSession = new Session(
          UUID.randomUUID(),
          currentClient.getId(),
          foundPc.get().getId(),
          foundTariff.get().getId(),
          LocalDateTime.now(),
          endTime,
          BigDecimal.ZERO,
          true
      );

      System.out.println("Створено сесію з clientId=" + newSession.getClientId());
      System.out.println("currentClient.getId()=" + currentClient.getId());

      dbSession.addSession(newSession);

      Computer pc = foundPc.get();
      pc.setComputerStatus(ComputerStatus.Busy.name());
      dbSession.updateComputer(pc);
      dbSession.commit();

      mainMenuController.refreshData();
      hideAnimation(-50, 0, startSessionFormBox);
    } catch (NumberFormatException e) {
      errorLabel.setText("Некоректний формат часу сесії!");
    }
  }

  @FXML
  private void handleCancelStartSession() {
    hideAnimation(0, 50, startSessionFormBox);
  }

  @FXML
  private void handleStopSession() {
    hideAllForms();

    Optional<Session> activeSessionOpt = dbSession.getAllSession().stream()
        .filter(s -> s.getClientId().equals(currentClient.getId()) && s.isActive())
        .findFirst();

    if (activeSessionOpt.isEmpty()) {
      errorLabel.setText("У клієнта немає активної сесії!");
      return;
    }

    activeSessionToClose = activeSessionOpt.get();
    UUID tariffId = activeSessionToClose.getTariffId();
    Optional<Tariff> tariff = dbSession.getTariff(tariffId);

    if (tariff.isPresent()) {
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

      if(currentClient.getDiscountPercent() > 0){
        BigDecimal discountMultiplier = BigDecimal.valueOf(currentClient.getDiscountPercent())
            .divide(BigDecimal.valueOf(100));
        BigDecimal discountValue = calculatedTotalCost.multiply(discountMultiplier);
        calculatedTotalCost = calculatedTotalCost.subtract(discountValue);
      }

      calculatedTotalCost = calculatedTotalCost.setScale(2, RoundingMode.HALF_UP);

      totalCostLabel.setText(String.format("%.2f ₴", calculatedTotalCost));
    }

    paymentMethodComboBox.getItems().clear();
    paymentMethodComboBox.getItems().addAll(MethodPayment.Balance.getDisplayName(), MethodPayment.Card.getDisplayName(), MethodPayment.Cash.getDisplayName());
    paymentMethodComboBox.getSelectionModel().selectFirst();

    endSessionFormBox.setVisible(true);
    endSessionFormBox.setManaged(true);
    showAnimation(50, 0, endSessionFormBox);
  }

  @FXML
  private void handleConfirmEndSession() {
    if (currentClient == null || activeSessionToClose == null) {
      errorLabel.setText("Клієнт або сесія відсутні!");
      return;
    }
    String selectedPaymentMethod = paymentMethodComboBox.getValue();
    if (selectedPaymentMethod == null) {
      errorLabel.setText("Будь ласка, оберіть метод оплати!");
      return;
    }

    LocalDateTime now = LocalDateTime.now();
    errorLabel.setText("");

    if (MethodPayment.Balance.getDisplayName().equals(selectedPaymentMethod)) {
      BigDecimal currentBalance = currentClient.getBalance();
      if (currentBalance == null) currentBalance = BigDecimal.ZERO;

      if (currentBalance.compareTo(calculatedTotalCost) < 0) {
        errorLabel.setText("Недостатньо коштів на балансі користувача!");
        return;
      }
      currentClient.setBalance(currentBalance.subtract(calculatedTotalCost));
    }

    if(MethodPayment.Card.getDisplayName().equals(selectedPaymentMethod)){
      String clientEmail = currentClient.getEmail();

      if (clientEmail == null || clientEmail.trim().isEmpty()) {
        errorLabel.setText("У клієнта не вказано email для відправки рахунку!");
        return;
      }

      String orderId = "INV-" + activeSessionToClose.getId().toString().substring(0, 8);
      String amountStr = calculatedTotalCost.setScale(2, RoundingMode.HALF_UP).toString();

      Thread sendInvoiceThread = new Thread(() -> {
        try{
          PaymentInvoiceService paymentInvoiceService = new PaymentInvoiceServiceImpl();
          paymentInvoiceService.sendInvoiceToEmail(currentClient.getEmail(), amountStr, orderId);

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

    Optional<Computer> pcOpt = dbSession.getAllComputers().stream()
        .filter(c -> c.getId().equals(activeSessionToClose.getComputerId()))
        .findFirst();
    if (pcOpt.isPresent()) {
      Computer pc = pcOpt.get();
      pc.setComputerStatus(ComputerStatus.Available.name());
      dbSession.updateComputer(pc);
    }

    activeSessionToClose.setActive(false);
    activeSessionToClose.setEndTime(now);
    activeSessionToClose.setTotalCost(calculatedTotalCost);
    dbSession.updateSession(activeSessionToClose);

    currentClient.setVisitCount(currentClient.getVisitCount() + 1);
    dbSession.updateClient(currentClient);

    MethodPayment methodPayment = MethodPayment.fromDisplayName(selectedPaymentMethod);

    Payment newPayment = new Payment(
        UUID.randomUUID(),
        currentClient.getId(),
        activeSessionToClose.getId(),
        calculatedTotalCost,
        now,
        methodPayment.name()
    );
    dbSession.addPayments(newPayment);
    dbSession.commit();

    mainMenuController.refreshData();
    hideAnimation(0, 50, endSessionFormBox);
  }

  @FXML
  private void handleCancelEndSessionForm() {
    hideAnimation(0, 50, endSessionFormBox);
  }

  @FXML
  private void handleTransferClient() {
    hideAllForms();

    String rawNickname = currentClient.getNickname();
    if (rawNickname != null && rawNickname.startsWith("[BANNED]")){
      errorLabel.setText("Даний користувач заблокований!");
      return;
    }

    targetComputerComboBox.getItems().clear();
    List<Computer> availablePcs = dbSession.getAllComputers().stream()
        .filter(c -> "Available".equalsIgnoreCase(c.getComputerStatus()))
        .toList();
    for (Computer pc : availablePcs) {
      targetComputerComboBox.getItems().add("ПК-" + pc.getComputerNumber());
    }

    if (targetComputerComboBox.getItems().isEmpty()) {
      errorLabel.setText("Немає вільних комп'ютерів для пересадки!");
      return;
    }
    targetComputerComboBox.getSelectionModel().selectFirst();
    transferFormBox.setVisible(true);
    transferFormBox.setManaged(true);

    showAnimation(50, 0, transferFormBox);
  }

  @FXML
  private void handleConfirmTransfer() {
    String selectedPcStr = targetComputerComboBox.getValue();
    if (selectedPcStr == null){
      errorLabel.setText("Оберіть комп'ютер для пересадки!");
      return;
    }

    Optional<Session> activeSessionOpt = dbSession.getAllSession().stream()
        .filter(s -> s.getClientId().equals(currentClient.getId()) && s.isActive())
        .findFirst();

    if (activeSessionOpt.isPresent()) {
      Session activeSession = activeSessionOpt.get();
      int newPcNumber = Integer.parseInt(selectedPcStr.replace("ПК-", ""));

      Optional<Computer> newPcOpt = dbSession.getAllComputers().stream()
          .filter(c -> c.getComputerNumber() == newPcNumber)
          .findFirst();
      Optional<Computer> oldPcOpt = dbSession.getAllComputers().stream()
          .filter(c -> String.valueOf(c.getId()).equals(String.valueOf(activeSession.getComputerId())))
          .findFirst();

      if (newPcOpt.isPresent()) {
        if (oldPcOpt.isPresent()) {
          Computer oldPc = oldPcOpt.get();
          oldPc.setComputerStatus(ComputerStatus.Available.name());
          dbSession.updateComputer(oldPc);
        }

        Computer newPc = newPcOpt.get();
        newPc.setComputerStatus(ComputerStatus.Busy.name());
        dbSession.updateComputer(newPc);

        activeSession.setComputerId(newPc.getId());
        dbSession.updateSession(activeSession);

        dbSession.commit();

        Optional<Session> checkSession = dbSession.getAllSession().stream()
            .filter(s -> s.getClientId().equals(currentClient.getId()) && s.isActive())
            .findFirst();

        if (checkSession.isPresent()) {
          System.out.println("ПІСЛЯ КОМІТУ в базі ПК сесії = " + checkSession.get().getComputerId());
          System.out.println("Очікувалось: " + newPc.getId());
        }

        transferFormBox.setVisible(false);
        transferFormBox.setManaged(false);
        errorLabel.setText("");
        mainMenuController.refreshData();
      } else {
        errorLabel.setText("Обраний комп'ютер не знайдено в системі!");
      }
    } else {
      errorLabel.setText("У клієнта немає активної сесії для пересадки!");
    }
    hideAnimation(0, 50, transferFormBox);
  }

  @FXML
  private void handleCancelTransfer() {
    hideAnimation(0, 50, transferFormBox);
  }

  @FXML
  private void handleTopUpBalance() {
    hideAllForms();

    String rawNickname = currentClient.getNickname();
    if (rawNickname != null && rawNickname.startsWith("[BANNED]")){
      errorLabel.setText("Даний користувач заблокований!");
      return;
    }

    topUpMethodComboBox.getItems().clear();
    topUpMethodComboBox.getItems().addAll(MethodPayment.Cash.getDisplayName(), MethodPayment.Card.getDisplayName());
    topUpMethodComboBox.getSelectionModel().selectFirst();
    topUpAmountField.clear();

    topUpFormBox.setVisible(true);
    topUpFormBox.setManaged(true);

    showAnimation(50, 0, topUpFormBox);
  }

  @FXML
  private void handleConfirmTopUp() {
    String amountStr = topUpAmountField.getText().trim();

    if (amountStr.isEmpty()){
      errorLabel.setText("Заповніть всі поля.");
      return;
    }

    String selectedMethod = topUpMethodComboBox.getValue();
    if(selectedMethod == null){
      errorLabel.setText("Будь ласка, оберіть метод оплати!");
      return;
    }

    BigDecimal amount;
    try {
      amount = new BigDecimal(amountStr.replace(",", "."));
      if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        errorLabel.setText("Сума має бути більшою за нуль!");
        return;
      }
    } catch (NumberFormatException e) {
      errorLabel.setText("Некоректний формат суми поповнення!");
      return;
    }

    try {
      if (currentClient == null) {
        errorLabel.setText("Помилка: Клієнта не вибрано!");
        return;
      }

      BigDecimal currentBalance = currentClient.getBalance();
      if (currentBalance == null) currentBalance = BigDecimal.ZERO;

      String clientEmail = currentClient.getEmail();

      if (MethodPayment.Card.getDisplayName().equals(selectedMethod)) {
        if (clientEmail == null || clientEmail.trim().isEmpty()) {
          errorLabel.setText("У клієнта не вказано email для відправки рахунку!");
          return;
        }

        String orderId = "TOPUP-" + System.currentTimeMillis() % 100000;

        Thread sendInvoiceThread = new Thread(() -> {
          try {
            PaymentInvoiceService paymentInvoiceService = new PaymentInvoiceServiceImpl();
            paymentInvoiceService.sendInvoiceToEmail(clientEmail, amount.toString(), orderId);

            javafx.application.Platform.runLater(() -> {
              System.out.println("Рахунок надіслано. Очікуємо оплати...");
              errorLabel.setStyle("-fx-text-fill: #4cc9f0;");
              errorLabel.setText("Рахунок надіслано на пошту. Очікуємо оплати...");
            });
            startPaymentPooling(orderId, amount, currentClient);
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
      } else if (MethodPayment.Cash.getDisplayName().equals(selectedMethod)){
        currentClient.setBalance(currentBalance.add(amount));
        dbSession.updateClient(currentClient);
        dbSession.commit();

        if (clientEmail != null && !clientEmail.trim().isEmpty()) {
          String text = "Привіт, <strong>" + currentClient.getNickname() + "</strong>!\n\n"
              + "Твій баланс аккаунта було успішно поповнено на <strong>" + amount + "₴" + "</strong>.\n"
              + "Гроші вже доступні для використання. Приємної гри та крутих каток!";

          Thread emailThread = new Thread(() -> {
            try {
              emailService.sendEmail(clientEmail, "Поповнення балансу — ClubControl", text);
            } catch (Exception e) {
              System.err.println("Помилка відправки листа про поповнення:");
              e.printStackTrace();
            }
          });
          emailThread.setDaemon(true);
          emailThread.start();
        }

        mainMenuController.refreshData();
        hideAnimation(0, 50, topUpFormBox);
      }

    } catch (Exception e) {
      e.printStackTrace();
      errorLabel.setText("Внутрішня помилка: " + e.getMessage());
    }
  }

  @FXML
  private void handleCancelTopUp() {
    hideAnimation(0, 50, topUpFormBox);
  }

  @FXML
  private void handleEditProfile() {
    hideAllForms();

    String rawNickname = currentClient.getNickname();
    if (rawNickname != null && rawNickname.startsWith("[BANNED]")){
      errorLabel.setText("Даний користувач заблокований!");
      return;
    }

    editNicknameField.setText(currentClient.getNickname());
    editEmailField.setText(currentClient.getEmail());
    editDiscountField.setText(String.format("%d%%", currentClient.getDiscountPercent()));

    editProfileFormBox.setVisible(true);
    editProfileFormBox.setManaged(true);

    showAnimation(50, 0, editProfileFormBox);
  }

  @FXML
  private void handleConfirmEditProfile() {
   try{
     String newNickname = editNicknameField.getText().trim();
     String newEmail = editEmailField.getText().trim();
     if (newNickname.isEmpty()) {
       errorLabel.setText("Нікнейм не може бути порожнім!");
       return;
     }

     int newDiscount;
     try {
       newDiscount = Integer.parseInt(editDiscountField.getText().trim());
       if (newDiscount < 0 || newDiscount > 100) {
         errorLabel.setText("Знижка має бути від 0 до 100%!");
         errorLabel.setVisible(true);
         return;
       }
     } catch (NumberFormatException e) {
       errorLabel.setText("Некоректний формат знижки!");
       errorLabel.setVisible(true);
       return;
     }

     currentClient.setNickname(newNickname);
     currentClient.setEmail(newEmail);
     currentClient.setDiscountPercent(newDiscount);

     dbSession.updateClient(currentClient);
     dbSession.commit();

     clientNicknameLabel.setText(currentClient.getNickname());

     mainMenuController.refreshData();
     hideAnimation(0, 50, transferFormBox);
   } catch (Exception e) {
     e.printStackTrace();
     errorLabel.setText("Помилка при збереженні в БД!");
     errorLabel.setVisible(true);
   }
  }

  @FXML
  private void handleCancelEditProfile() {
    hideAnimation(0, 50, editProfileFormBox);
  }

  @FXML
  private void handleShowClientInfo(){
    hideAllForms();

    String rawNickname = currentClient.getNickname();
    if (rawNickname != null && rawNickname.startsWith("[BANNED]")){
      String cleanNickname = rawNickname.replace("[BANNED]", "").trim();
      infoNicknameLabel.setText(cleanNickname);
    } else {
      infoNicknameLabel.setText(currentClient.getNickname());
    }
    infoEmailLabel.setText(currentClient.getEmail() != null && !currentClient.getEmail().isEmpty() ? currentClient.getEmail() : "Не вказано");
    infoBalanceLabel.setText(String.format("%.2f ₴", currentClient.getBalance()));
    infoDiscountLabel.setText(currentClient.getDiscountPercent() + "%");
    infoVisitCountLabel.setText(String.valueOf(currentClient.getVisitCount()));

    if (currentClient.getRegistrationDate() != null) {
      infoRegistrationDateLabel.setText(currentClient.getRegistrationDate().toString());
    } else {
      infoRegistrationDateLabel.setText("Не вказано");
    }

    userInfoFormBox.setVisible(true);
    userInfoFormBox.setManaged(true);

    showAnimation(50, 0, userInfoFormBox);
  }

  @FXML
  private void handleCloseClientInfo(){
    hideAnimation(0, 50, userInfoFormBox);
  }

  @FXML
  private void handleBanClient() {
    if(!currentClient.getNickname().startsWith("[BANNED]")) {
      currentClient.setNickname("[BANNED] " + currentClient.getNickname());
      dbSession.updateClient(currentClient);
      dbSession.commit();
      clientNicknameLabel.setText(currentClient.getNickname());
      mainMenuController.refreshData();
    } else {
      errorLabel.setText("Користувач вже заблокований!");
    }
  }

  @FXML
  private void handleClose() {
    if (mainMenuController != null) {
      mainMenuController.hideRightPanel();
    }
  }

  private void showAnimation(double from, double to, VBox formBox){
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
      initData(currentClient, mainMenuController);
    });
    showAnimation.play();
  }

  private void startPaymentPooling(String orderId, BigDecimal amount, Client clientToUpdate){
    PaymentInvoiceService paymentInvoiceService = new PaymentInvoiceServiceImpl();
    int maxAttempts = 90;
    int attempt = 0;

    while (attempt < maxAttempts) {
      try {
        Thread.sleep(10000);
        attempt++;

        String status = paymentInvoiceService.checkPaymentStatus(orderId);
        System.out.println("Перевірка статусу " + orderId + " (спроба " + attempt + "): " + status);

        if ("success".equals(status) || "not_found_yet".equals(status)) {

          javafx.application.Platform.runLater(() -> {
            BigDecimal currentBalance = clientToUpdate.getBalance();
            if (currentBalance == null) currentBalance = BigDecimal.ZERO;

            clientToUpdate.setBalance(currentBalance.add(amount));
            dbSession.updateClient(clientToUpdate);
            dbSession.commit();

            mainMenuController.refreshData();

            emailService.sendEmail(clientToUpdate.getEmail(), "Оплата успішна!",
                "Привіт! Твій баланс успішно поповнено на <strong>" + amount + " UAH</strong> через LiqPay.");

            System.out.println("Баланс успішно поповнено після оплати!");
            hideAnimation(0, 50, topUpFormBox);
          });

          break;
        }
        else if ("failure".equals(status)) {
          javafx.application.Platform.runLater(() -> {
            errorLabel.setStyle("-fx-text-fill: #e11d48;");
            errorLabel.setText("Платіж було скасовано або відхилено банком.");
          });
          break;
        }

      } catch (InterruptedException e) {
        e.printStackTrace();
        break;
      }
    }
  }
}
