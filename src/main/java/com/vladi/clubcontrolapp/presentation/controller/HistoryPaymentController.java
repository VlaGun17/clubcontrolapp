package com.vladi.clubcontrolapp.presentation.controller;

import com.vladi.clubcontrolapp.Launcher;
import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.entities.Payment;
import com.vladi.clubcontrolapp.domain.entities.Session;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HistoryPaymentController {
  @FXML private TableView<Payment> transactionTableView;
  @FXML
  private TableColumn<Payment, String> clientColumn;
  @FXML private TableColumn<Payment, String> sessionColumn;
  @FXML private TableColumn<Payment, String> amountColumn;
  @FXML private TableColumn<Payment, String> dateColumn;
  @FXML private TableColumn<Payment, String> methodColumn;

  private final PersistanceSession dbSession = Launcher.getSessionContext();
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  private ObservableList<Payment> masterData = FXCollections.observableArrayList();
  private FilteredList<Payment> filteredData;

  @FXML
  public void initialize() {
    setupTableColumns();
    loadPaymentData();
  }

  private void setupTableColumns(){
    clientColumn.setCellValueFactory(cellData -> {
      Payment payment = cellData.getValue();
      Optional<Client> clientOpt = dbSession.getClient(payment.getClientId());
      return new SimpleStringProperty(clientOpt.map(Client::getNickname).orElse("Гість / Видалений"));
    });

    sessionColumn.setCellValueFactory(cellData -> {
      Payment payment = cellData.getValue();
      if (payment.getSessionId() == null) return new SimpleStringProperty("—");

      Optional<Session> sessionOpt = dbSession.getAllSession().stream()
          .filter(s -> s.getId().equals(payment.getSessionId()))
          .findFirst();

      if (sessionOpt.isPresent()) {
        Optional<Computer> pcOpt = dbSession.getComputer(sessionOpt.get().getComputerId());
        return new SimpleStringProperty(pcOpt.map(pc -> "PC-" + pc.getComputerNumber()).orElse("ПК"));
      }
      return new SimpleStringProperty("ID: " + payment.getSessionId());
    });

    amountColumn.setCellValueFactory(cellData -> {
      BigDecimal amt = cellData.getValue().getAmount();
      return new SimpleStringProperty(amt != null ? String.format("%.2f ₴", amt) : "0.00 ₴");
    });

    amountColumn.setCellFactory(column -> new TableCell<>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
          setStyle("");
        } else {
          setText(item);
          setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;");
        }
      }
    });

    dateColumn.setCellValueFactory(cellData -> {
      Payment payment = cellData.getValue();
      if (payment.getPaymentDate() != null) {
        return new SimpleStringProperty(payment.getPaymentDate().format(formatter));
      }
      return new SimpleStringProperty("—");
    });

    methodColumn.setCellValueFactory(cellData -> {
      String method = cellData.getValue().getPaymentMethod();
      return new SimpleStringProperty(method != null ? method.toUpperCase() : "CASH");
    });
  }

  private void loadPaymentData(){
    List<Payment> allPayments = dbSession.getAllPayments();
    masterData.setAll(allPayments);
    masterData.sort((p1, p2) -> p2.getPaymentDate().compareTo(p1.getPaymentDate()));
    filteredData = new FilteredList<>(masterData, p -> true);
    transactionTableView.setItems(filteredData);
  }

  public void filterData(String searchText) {
    if (searchText == null || searchText.isEmpty()) {
      filteredData.setPredicate(p -> true);
      return;
    }

    String lowerCaseFilter = searchText.toLowerCase();

    filteredData.setPredicate(payment -> {
      Optional<Client> clientOpt = dbSession.getClient(payment.getClientId());
      if (clientOpt.isPresent() && clientOpt.get().getNickname().toLowerCase().contains(lowerCaseFilter)) {
        return true;
      }
      if (payment.getPaymentMethod() != null && payment.getPaymentMethod().toLowerCase().contains(lowerCaseFilter)) {
        return true;
      }
      return false;
    });
  }
}
