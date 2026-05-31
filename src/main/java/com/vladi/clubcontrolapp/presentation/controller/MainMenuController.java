package com.vladi.clubcontrolapp.presentation.controller;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;

import com.vladi.clubcontrolapp.Launcher;
import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.entities.Payment;
import com.vladi.clubcontrolapp.domain.entities.Service;
import com.vladi.clubcontrolapp.domain.enums.ComputerStatus;
import com.vladi.clubcontrolapp.domain.enums.ComputerType;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import com.vladi.clubcontrolapp.presentation.util.NavigationManager;
import java.io.IOException;
import java.util.stream.Stream;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainMenuController {
  @FXML private FlowPane mainContainer;
  @FXML private TextField searchField;
  @FXML private ComboBox sortByStatus;
  @FXML private StackPane mainStackPane;
  @FXML private Button addButton;
  @FXML private Button computersButton;
  @FXML private Button clientsButton;
  @FXML private Button tariffsButton;
  @FXML private Button servicesButton;
  @FXML private Button historySessionButton;
  @FXML private Button historyPaymentButton;
  @FXML private Button statisticsButton;
  @FXML private Button settingsButton;
  @FXML private StackPane rightPanelContainer;
  private HistorySessionController historySessionController;
  private HistoryPaymentController historyPaymentController;
  private StatisticsController statisticsController;
  private SettingsController settingsController;

  private final PersistanceSession session = Launcher.getSessionContext();

  private enum ViewMode{
    COMPUTERS, CLIENTS, TARIFFS, SERVICES, HISTORY_SESSION, HISTORY_PAYMENT, STATISTICS, SETTINGS
  }
  private ViewMode currentMode = ViewMode.COMPUTERS;
  private final double SIDEBAR_WIDTH = 350.0;

  @FXML
  public void initialize() {
    setupComputerFilter();

    renderComputers("");

    searchField.textProperty().addListener((obs, oldVal, newVal) -> {
      if(currentMode == ViewMode.COMPUTERS){
        renderComputers(newVal);
      } else if (currentMode == ViewMode.CLIENTS){
        renderClients(newVal);
      } else if(currentMode == ViewMode.TARIFFS){
        renderTariffs(newVal);
      } else if(currentMode == ViewMode.SERVICES){
        renderServices(newVal);
      } else if (currentMode == ViewMode.HISTORY_SESSION) {
        filterHistory(newVal);
      } else if(currentMode == ViewMode.HISTORY_PAYMENT){
        if(historyPaymentController != null){
          historyPaymentController.filterData(newVal);
        }
      }
    });
    sortByStatus.valueProperty().addListener((obs, oldVal, newVal) -> {
      if (currentMode == ViewMode.COMPUTERS) {
        renderComputers(searchField.getText());
      } else if (currentMode == ViewMode.CLIENTS){
        renderClients(searchField.getText());
      } else if(currentMode == ViewMode.TARIFFS){
        renderTariffs(searchField.getText());
      } else if (currentMode == ViewMode.SERVICES){
        renderServices(searchField.getText());
      } else if (currentMode == ViewMode.HISTORY_SESSION){
        filterHistory(searchField.getText());
      } else if(currentMode == ViewMode.HISTORY_PAYMENT){
        if(historyPaymentController != null){
          historyPaymentController.filterData(searchField.getText());
        }
      }
    });
  }

  private void renderComputers(String filter) {
    mainContainer.getChildren().clear();
    String selectedStatus = sortByStatus.getValue() != null ? sortByStatus.getValue().toString() : "Всі";

    session.getAllComputers().stream()
        .filter(c -> String.valueOf(c.getComputerNumber()).contains(filter))
        .filter(c -> {
          if (selectedStatus.equals("Всі")) return true;
          String dbStatusStr = c.getComputerStatus();
          String pcStatusDisplayName = ComputerStatus.valueOf(dbStatusStr).getDisplayName();
          return pcStatusDisplayName.equalsIgnoreCase(selectedStatus);
        })
        .forEach(pc -> {
          VBox card = createCard(pc);
          mainContainer.getChildren().add(card);
        });
  }

  private void renderClients(String filter) {
    mainContainer.getChildren().clear();
    String selectedFilter = sortByStatus.getValue() != null ? sortByStatus.getValue().toString() : "Всі";

    session.getAllClients().stream()
        .filter(client -> client.getNickname().toLowerCase().contains(filter.toLowerCase()) ||
            client.getEmail().toLowerCase().contains(filter.toLowerCase()))
        .filter(client -> {
          if(selectedFilter.equals("Всі")) return true;
          if(selectedFilter.equals("Зі знижкою")) return client.getDiscountPercent() > 0;
          if (selectedFilter.equals("Без знижки")) return client.getDiscountPercent() == 0;
          return true;
        })
        .forEach(client -> {
          try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/client_card.fxml"));
            VBox card = loader.load();

            Label nicknameLabel = (Label) card.lookup("#nicknameLabel");
            Label bannedBadge = (Label) card.lookup("#bannedBadge");
            Label emailLabel = (Label) card.lookup("#emailLabel");
            Label balanceLabel = (Label) card.lookup("#balanceLabel");
            Label discountLabel = (Label) card.lookup("#discountLabel");
            Label visitCountLabel = (Label) card.lookup("#visitCountLabel");
            Label regDateLabel = (Label) card.lookup("#regDateLabel");
            Button editClientBtn = (Button) card.lookup("#editClientBtn");

            String rawNickname = client.getNickname();
            if (rawNickname != null && rawNickname.startsWith("[BANNED]")) {
              String cleanNickname = rawNickname.replace("[BANNED]", "").trim();
              nicknameLabel.setText(cleanNickname);
              nicknameLabel.setTextFill(javafx.scene.paint.Color.web("#7f8c8d"));

              if (bannedBadge != null) {
                bannedBadge.setVisible(true);
                bannedBadge.setManaged(true);
              }

              card.setStyle("-fx-background-color: #2a2a32; -fx-background-radius: 10; -fx-border-color: #721c24; -fx-border-radius: 10;");
            } else {
              nicknameLabel.setText(rawNickname);
              nicknameLabel.setTextFill(javafx.scene.paint.Color.web("#4cc9f0"));

              if (bannedBadge != null) {
                bannedBadge.setVisible(false);
                bannedBadge.setManaged(false);
              }
            }

            emailLabel.setText(client.getEmail());
            balanceLabel.setText(String.format("%.2f ₴", client.getBalance()));
            discountLabel.setText(client.getDiscountPercent() + "%");
            visitCountLabel.setText(String.valueOf(client.getVisitCount()));
            regDateLabel.setText(client.getRegistrationDate().toString());

            if(editClientBtn != null){
              editClientBtn.setOnAction(event -> {
                try {
                  FXMLLoader loaderClients = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/manage_client.fxml"));
                  Parent buyPanel = loaderClients.load();

                  ManageClientController controller = loaderClients.getController();
                  controller.initData(client, MainMenuController.this);

                  showRightPanel(buyPanel);
                  showSidebarSmoothly();
                } catch (IOException e) {
                  System.err.println("Помилка завантаження manage_client.fxml");
                  e.printStackTrace();
                }
              });
            }

            mainContainer.getChildren().add(card);
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
  }

  private void renderTariffs(String filter){
    mainContainer.getChildren().clear();
    String selectedFilter = sortByStatus.getValue() != null ? sortByStatus.getValue().toString() : "Всі";

    session.getAllTariffs().stream()
        .filter(tariff -> tariff.getName().toLowerCase().contains(filter.toLowerCase()))
        .filter(tariff -> {
          if (selectedFilter.equals("Всі")) return true;
          if (selectedFilter.equals("Нічні")) return tariff.isNight();
          if (selectedFilter.equals("Денні")) return !tariff.isNight();
          return true;
        })
        .forEach(tariff -> {
          try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/tariff_card.fxml"));
            VBox card = loader.load();

            Label nameLabel = (Label) card.lookup("#nameLabel");
            Label timeTypeLabel = (Label) card.lookup("#timeTypeLabel");
            Label priceLabel = (Label) card.lookup("#priceLabel");
            Label periodLabel = (Label) card.lookup("#periodLabel");
            Button editTariffBtn = (Button) card.lookup("#editTariffBtn");

            nameLabel.setText(tariff.getName());
            priceLabel.setText(String.format("%.2f ₴", tariff.getPricePerHour()));

            if (tariff.isNight()) {
              timeTypeLabel.setText("НІЧНИЙ");
              timeTypeLabel.setStyle("-fx-background-color: #7209b7; -fx-text-fill: white; -fx-background-radius: 5;");
              periodLabel.setText("22:00 - 08:00");
            } else {
              timeTypeLabel.setText("ДЕННИЙ");
              timeTypeLabel.setStyle("-fx-background-color: #4cc9f0; -fx-text-fill: #1e1e24; -fx-background-radius: 5; -fx-font-weight: bold;");
              periodLabel.setText("08:00 - 22:00");
            }

            if (editTariffBtn != null) {
              editTariffBtn.setOnAction(event -> {
                try {
                  FXMLLoader loaderPanel = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/manage_tariff.fxml"));
                  Parent managePanel = loaderPanel.load();

                  ManageTariffController controller = loaderPanel.getController();
                  controller.initData(tariff, MainMenuController.this);

                  showRightPanel(managePanel);
                  showSidebarSmoothly();
                } catch (IOException e) {
                  System.err.println("Помилка завантаження manage_tariff.fxml");
                }

              });
            }
            mainContainer.getChildren().add(card);
          } catch (IOException e) {
            System.err.println("Помилка завантаження tariff_card.fxml для тарифу: " + tariff.getName());
            e.printStackTrace();
          }
        });
  }

  private void renderServices(String filter){
    mainContainer.getChildren().clear();
    String selectedFilter = sortByStatus.getValue() != null ? sortByStatus.getValue().toString() : "Всі";

    Stream<Service> stream = session.getAllServices().stream()
            .filter(service -> service.getName().toLowerCase().contains(filter.toLowerCase()));

    if ("Дешеві спочатку".equals(selectedFilter)) {
      stream = stream.sorted(comparing(Service::getPrice));
    } else if ("Дорогі спочатку".equals(selectedFilter)) {
      stream = stream.sorted(comparing(Service::getPrice).reversed());
    }

    stream.forEach(service -> {
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/service_card.fxml"));
        VBox card = loader.load();

        Label nameLabel = (Label) card.lookup("#nameLabel");
        Label priceLabel = (Label) card.lookup("#priceLabel");
        Button buyServiceBtn = (Button) card.lookup("#buyServiceBtn");

        nameLabel.setText(service.getName());
        priceLabel.setText(String.format("%.2f ₴", service.getPrice()));

        if (buyServiceBtn != null) {
          buyServiceBtn.setOnAction(event -> {
            try {
              FXMLLoader loaderService = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/buy_service.fxml"));
              Parent buyPanel = loaderService.load();

              BuyServiceController controller = loaderService.getController();
              controller.initData(service, MainMenuController.this);

              showRightPanel(buyPanel);
              showSidebarSmoothly();
            } catch (IOException e) {
              System.err.println("Помилка завантаження buy_service.fxml");
              e.printStackTrace();
            }
          });
        }

        mainContainer.getChildren().add(card);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  @FXML
  private void handleShowComputers(){
    currentMode = ViewMode.COMPUTERS;
    searchField.clear();
    searchField.setPromptText("Пошук комп'ютера...");

    setupComputerFilter();

    addButton.setText("+ Додати ПК");
    addButton.setVisible(true);
    sortByStatus.setVisible(true);
    searchField.setVisible(true);

    renderComputers("");
  }

  @FXML
  private void handleShowClients(){
    currentMode = ViewMode.CLIENTS;
    searchField.clear();
    searchField.setPromptText("Пошук клієнта (нік/email)...");

    setupClientsFilter();

    addButton.setVisible(true);
    addButton.setText("+ Додати клієнта");
    sortByStatus.setVisible(true);
    searchField.setVisible(true);
    renderClients("");
  }

  @FXML
  private void handleShowTariffs(){
    currentMode = ViewMode.TARIFFS;
    searchField.clear();
    searchField.setPromptText("Пошук тарифу за назвою...");

    sortByStatus.getItems().setAll("Всі", "Денні", "Нічні");
    sortByStatus.setValue("Всі");
    sortByStatus.setPromptText("Тип часу");

    addButton.setVisible(true);
    addButton.setText("+ Додати тариф");
    sortByStatus.setVisible(true);
    searchField.setVisible(true);

    renderTariffs("");
  }

  @FXML
  private void handleShowServices(){
    currentMode = ViewMode.SERVICES;
    searchField.clear();
    searchField.setPromptText("Пошук послуги за назвою...");

    sortByStatus.getItems().setAll("Всі", "Дешеві спочатку", "Дорогі спочатку");
    sortByStatus.setValue("Всі");
    sortByStatus.setPromptText("Сортування");

    addButton.setVisible(true);
    addButton.setText("+ Додати послугу");
    sortByStatus.setVisible(true);
    searchField.setVisible(true);


    renderServices("");
  }

  @FXML
  private void handleShowHistorySession(){
    currentMode = ViewMode.HISTORY_SESSION;
    mainContainer.getChildren().clear();

    addButton.setVisible(false);
    searchField.setPromptText("Пошук сесії за клієнтом...");
    sortByStatus.setVisible(false);


    try{
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/history_session.fxml"));
      Parent historyView = loader.load();
      historySessionController = loader.getController();

      if (historyView instanceof Region) {
        ((Region) historyView).prefWidthProperty().bind(mainContainer.widthProperty());
      }
      mainContainer.getChildren().add(historyView);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleShowHistoryPayment(){
    currentMode = ViewMode.HISTORY_PAYMENT;
    mainContainer.getChildren().clear();

    addButton.setVisible(false);
    searchField.setPromptText("Пошук за клієнтом...");
    sortByStatus.setVisible(false);

    try{
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/history_payment.fxml"));
      Parent paymentView = loader.load();

      historyPaymentController = loader.getController();
      if (paymentView instanceof Region) {
        ((Region) paymentView).prefWidthProperty().bind(mainContainer.widthProperty());
      }
      mainContainer.getChildren().add(paymentView);
    } catch (IOException e) {
      System.err.println("Помилка завантаження history_transaction.fxml");
      e.printStackTrace();
    }
  }

  @FXML
  private void handleShowStatistics(){
    currentMode = ViewMode.STATISTICS;
    mainContainer.getChildren().clear();

    addButton.setVisible(false);
    searchField.setVisible(false);
    sortByStatus.setVisible(false);

    try{
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/statistics.fxml"));
      Parent statisticsView = loader.load();

      statisticsController = loader.getController();
      if(statisticsView instanceof Region){
        ((Region) statisticsView).prefWidthProperty().bind(mainContainer.widthProperty());
      }
      mainContainer.getChildren().add(statisticsView);
    } catch (IOException e) {
      System.err.println("Помилка завантаження statistics.fxml");
      e.printStackTrace();
    }
  }

  @FXML
  private void handleShowSettings(){
    currentMode = ViewMode.SETTINGS;
    mainContainer.getChildren().clear();

    addButton.setVisible(false);
    searchField.setVisible(false);
    sortByStatus.setVisible(false);

    try{
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/settings.fxml"));
      Parent settingsView = loader.load();

      settingsController = loader.getController();
      if(settingsView instanceof Region){
        ((Region)settingsView).prefWidthProperty().bind(mainContainer.widthProperty());
      }
      mainContainer.getChildren().add(settingsView);
    } catch (IOException e) {
      System.err.println("Помилка завантаження statistics.fxml");
      e.printStackTrace();
    }
  }

  @FXML
  private void handleLogout(ActionEvent event){
    try{
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/login.fxml"));
      Parent loginRoot = loader.load();
      Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      Scene loginScene = new Scene(loginRoot);
      stage.setScene(loginScene);
      stage.centerOnScreen();
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleMainAction() throws IOException{
    if (currentMode == ViewMode.COMPUTERS) {
      handleShowAddComputer();
    } else if (currentMode == ViewMode.CLIENTS) {
      handleShowAddClient();
    } else if (currentMode == ViewMode.TARIFFS) {
      handleShowAddTariffs();
    } else {
      handleShowAddServices();
    }
  }

  private void handleShowAddComputer() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/add_computer.fxml"));
    Parent addForm = loader.load();
    if(addForm != null){
      StackPane overlay = new StackPane();
      overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
      overlay.getChildren().add(addForm);

      AddComputerController controller = loader.getController();
      controller.setOverlayNode(overlay);

      controller.setOnDataChangedListener(() -> {
        renderComputers(searchField.getText());
      });

      mainStackPane.getChildren().add(overlay);
    }
  }

  private void handleShowAddClient() throws IOException{
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/add_client.fxml"));
    Parent addForm = loader.load();
    if(addForm != null){
      StackPane overlay = new StackPane();
      overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
      overlay.getChildren().add(addForm);

      AddClientController controller = loader.getController();
      controller.setOverlayNode(overlay);

      controller.setOnDataChangedListener(() -> {
        renderClients(searchField.getText());
      });

      mainStackPane.getChildren().add(overlay);
    }
  }

  private void handleShowAddTariffs() throws IOException{
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/add_tariff.fxml"));
    Parent addForm = loader.load();
    if(addForm != null){
      StackPane overlay = new StackPane();
      overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
      overlay.getChildren().add(addForm);

      AddTariffController controller = loader.getController();
      controller.setOverlayNode(overlay);

      controller.setOnDataChangedListener(() -> {
        renderTariffs(searchField.getText());
      });

      mainStackPane.getChildren().add(overlay);
    }
  }

  private void handleShowAddServices() throws IOException{
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/add_service.fxml"));
    Parent addForm = loader.load();
    if(addForm != null){
      StackPane overlay = new StackPane();
      overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
      overlay.getChildren().add(addForm);

      AddServiceController controller = loader.getController();
      controller.setOverlayNode(overlay);

      controller.setOnDataChangedListener(() -> {
        renderServices(searchField.getText());
      });

      mainStackPane.getChildren().add(overlay);
    }
  }

  private void showRightPanel(Parent contentNode) {
    rightPanelContainer.getChildren().clear();
    rightPanelContainer.getChildren().add(contentNode);
    rightPanelContainer.setVisible(true);
  }

  public void hideRightPanel() {
    if (rightPanelContainer != null) {
      hideSidebarSmoothly();
      rightPanelContainer.getChildren().clear();
    }
  }

  public void refreshData() {
    if(currentMode == ViewMode.COMPUTERS){
      renderComputers(searchField.getText());
    } else if (currentMode == ViewMode.CLIENTS){
      renderClients(searchField.getText());
    } else if(currentMode == ViewMode.TARIFFS){
      renderTariffs(searchField.getText());
    } else if(currentMode == ViewMode.SERVICES){
      renderServices(searchField.getText());
    } else if (currentMode == ViewMode.HISTORY_SESSION) {
      filterHistory(searchField.getText());
    } else if(currentMode == ViewMode.HISTORY_PAYMENT){
      if(historyPaymentController != null){
        historyPaymentController.filterData(searchField.getText());
      }
    }
  }

  private void deleteComputer(Computer pc) {
    session.removeComputer(pc);
    session.commit();
    renderComputers(searchField.getText()); // Оновлюємо інтерфейс
  }

  private VBox createCard(Computer pc) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/computer_card.fxml"));
      VBox card = loader.load();

      Label nameLabel = (Label) card.lookup("#nameLabel");
      Label statusLabel = (Label) card.lookup("#statusLabel");
      Button actionBtn = (Button) card.lookup("#actionBtn");
      Label typeLabel = (Label) card.lookup("#typeLabel");
      Button deleteBtn = (Button) card.lookup("#deleteBtn");

      String dbStatusStr = pc.getComputerStatus();
      String dbTypeStr = pc.getComputerType();
      nameLabel.setText("PC-" + pc.getComputerNumber());
      statusLabel.setText(ComputerStatus.valueOf(dbStatusStr).getDisplayName().toUpperCase());
      typeLabel.setText(ComputerType.valueOf(dbTypeStr).getDisplayName().toUpperCase());
      actionBtn.setText("КЕРУВАТИ");

      if ("Available".equalsIgnoreCase(pc.getComputerStatus())) {
        statusLabel.setStyle("-fx-text-fill: #4caf50;");
      } else {
        statusLabel.setStyle("-fx-text-fill: #f44336;");
      }

      actionBtn.setOnAction(event -> {
        try{
          FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/manage_computer.fxml"));
          Parent managePanel = loader1.load();

          ManageComputerController controller = loader1.getController();
          controller.initData(pc, this);

          showRightPanel(managePanel);
          showSidebarSmoothly();
        } catch (IOException e) {
          System.err.println("Помилка завантаження manage_computer.fxml");
          e.printStackTrace();
        }
      });

      if (deleteBtn != null) {
        deleteBtn.setOnAction(event -> {
          deleteComputer(pc);
        });
      }

      ContextMenu contextMenu = new ContextMenu();
      MenuItem deleteItem = new MenuItem("Видалити комп'ютер");
      deleteItem.setOnAction(e -> deleteComputer(pc));
      contextMenu.getItems().add(deleteItem);

      card.setOnContextMenuRequested(e ->
          contextMenu.show(card, e.getScreenX(), e.getScreenY())
      );

      return card;

    } catch (IOException e) {
      e.printStackTrace();
      return new VBox(new Label("Помилка завантаження ПК"));
    }
  }

  public void showSidebarSmoothly(){
    rightPanelContainer.setVisible(true);
    TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), rightPanelContainer);
    slideIn.setFromX(SIDEBAR_WIDTH);
    slideIn.setToX(0);
    slideIn.setInterpolator(Interpolator.EASE_OUT);
    slideIn.play();
  }

  public void hideSidebarSmoothly(){
    TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), rightPanelContainer);
    slideOut.setFromX(0);
    slideOut.setToX(SIDEBAR_WIDTH);
    slideOut.setInterpolator(Interpolator.EASE_IN);
    slideOut.setOnFinished(event -> {
      rightPanelContainer.setVisible(false);
    });
    slideOut.play();
  }

  private void filterHistory(String filterText) {
    if (historySessionController != null) {
      historySessionController.filterData(filterText);
    }
  }

  private void setupComputerFilter(){
    sortByStatus.getItems().setAll("Всі", "Вільний", "Зайнятий");
    sortByStatus.setValue("Всі");
    sortByStatus.setPromptText("Статус");
  }

  private void setupClientsFilter() {
    sortByStatus.getItems().setAll("Всі", "Зі знижкою", "Без знижки");
    sortByStatus.setValue("Всі");
    sortByStatus.setPromptText("Фільтр знижок");
  }
}
