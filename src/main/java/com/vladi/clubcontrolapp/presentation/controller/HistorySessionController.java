package com.vladi.clubcontrolapp.presentation.controller;

import com.vladi.clubcontrolapp.Launcher;
import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.entities.Session;
import com.vladi.clubcontrolapp.domain.entities.SessionService;
import com.vladi.clubcontrolapp.domain.entities.Tariff;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import com.vladi.clubcontrolapp.presentation.util.OnDataChangeListener;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HistorySessionController {
  @FXML private TableView<Session> sessionTableView;
  @FXML
  private TableColumn<Session, String> clientColumn;
  @FXML private TableColumn<Session, String> computerColumn;
  @FXML private TableColumn<Session, String> tariffColumn;
  @FXML private TableColumn<Session, String> startTimeColumn;
  @FXML private TableColumn<Session, String> endTimeColumn;
  @FXML private TableColumn<Session, String> totalCostColumn;
  @FXML private TableColumn<Session, String> statusColumn;

  @FXML private ListView<String> servicesListView;
  @FXML private Label servicesTotalLabel;

  private final PersistanceSession dbSession = Launcher.getSessionContext();
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
  private ObservableList<Session> masterData = FXCollections.observableArrayList();
  private FilteredList<Session> filteredData;
  private Node overlayNode;
  private OnDataChangeListener listener;

  @FXML
  public void initialize(){
    setupTableColumn();
    loadSessionData();

    sessionTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
      if (newSelection != null) {
        showSessionServices(newSelection);
      }
    });
  }

  private void setupTableColumn(){
    clientColumn.setCellValueFactory(cellData -> {
      Optional<Client> c = dbSession.getClient(cellData.getValue().getClientId());
      return new SimpleStringProperty(c.isPresent() ? c.get().getNickname() : "Гість (без акаунта)");
    });

    computerColumn.setCellValueFactory(cellData -> {
      Optional<Computer> pc = dbSession.getComputer(cellData.getValue().getComputerId());
      return new SimpleStringProperty(pc.isPresent() ? "PC-" + pc.get().getComputerNumber() : "Невідомо");
    });

    tariffColumn.setCellValueFactory(cellData -> {
      Optional<Tariff> t = dbSession.getTariff(cellData.getValue().getTariffId());
      return new SimpleStringProperty(t.isPresent() ? t.get().getName() : "Базовий");
    });

    startTimeColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().getStartTime().format(formatter)));

    endTimeColumn.setCellValueFactory(cellData -> {
      if (cellData.getValue().getEndTime() != null) {
        return new SimpleStringProperty(cellData.getValue().getEndTime().format(formatter));
      }
      return new SimpleStringProperty("-");
    });

    totalCostColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(String.format("%.2f ₴", cellData.getValue().getTotalCost())));

    statusColumn.setCellValueFactory(cellData ->
        new SimpleStringProperty(cellData.getValue().isActive() ? "ACTIVE" : "CLOSED"));

    statusColumn.setCellFactory(column -> new TableCell<>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
          setStyle("");
        } else {
          setText(item);
          if ("ACTIVE".equals(item)) {
            setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;"); // Зелений для активних
          } else {
            setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;"); // Червоний для закритих
          }
        }
      }
    });
  }

  private void loadSessionData(){
    List<Session> allSessions = dbSession.getAllSession();
    masterData.setAll(allSessions);
    masterData.sort((s1, s2) -> Boolean.compare(s2.isActive(), s1.isActive()));
    filteredData = new FilteredList<>(masterData, p -> true);
    sessionTableView.setItems(filteredData);
  }

  public void filterData(String searchText){
    if (searchText == null || searchText.isEmpty()) {
      filteredData.setPredicate(session -> true);
      return;
    }

    String lowerCaseFilter = searchText.toLowerCase();

    filteredData.setPredicate(session -> {
      Optional<Client> clientOpt = dbSession.getClient(session.getClientId());
      if (clientOpt.isPresent() && clientOpt.get().getNickname().toLowerCase().contains(lowerCaseFilter)) {
        return true;
      }

      Optional<Computer> pcOpt = dbSession.getComputer(session.getComputerId());
      if (pcOpt.isPresent() && String.valueOf(pcOpt.get().getComputerNumber()).contains(lowerCaseFilter)) {
        return true;
      }

      return false;
    });
  }

  private void showSessionServices(Session session){
    servicesListView.getItems().clear();

    List<SessionService> sessionServices = dbSession.getServicesForSession(session.getId());

    if (sessionServices == null || sessionServices.isEmpty()) {
      servicesListView.getItems().add("Немає куплених послуг");
      servicesTotalLabel.setText("0.00 ₴");
      return;
    }

    BigDecimal totalServicesCost = BigDecimal.ZERO;

    for (SessionService ss : sessionServices) {
      if (ss.getService() == null) {
        dbSession.getService(ss.getServiceId()).ifPresent(ss::setService);
      }

      if (ss.getService() != null) {
        String serviceName = ss.getService().getName();
        BigDecimal price = ss.getService().getPrice();
        int qty = ss.getQuantity();
        BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(qty));

        totalServicesCost = totalServicesCost.add(itemTotal);

        servicesTotalLabel.setText(totalServicesCost.toString());
        servicesListView.getItems().add(String.format("%s (x%d) — %.2f ₴", serviceName, qty, itemTotal));
      }
    }
  }
  public void setOverlayNode(Node overlayNode) {
    this.overlayNode = overlayNode;
  }

  public void setOnDataChangedListener(OnDataChangeListener listener){
    this.listener = listener;
  }
}
