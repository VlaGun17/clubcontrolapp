package com.vladi.clubcontrolapp.presentation.controller;

import com.vladi.clubcontrolapp.Launcher;
import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.domain.entities.Payment;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StatisticsController {
  @FXML
  private ComboBox<String> periodComboBox;
  @FXML private DatePicker customDatePicker;

  @FXML private Label totalPeriodIncomeLabel;
  @FXML private Label transactionCountLabel;
  @FXML private Label averagePaymentLabel;

  @FXML private BarChart<String, Number> revenueChart;
  @FXML private CategoryAxis xAxis;
  @FXML private NumberAxis yAxis;

  @FXML private TableView<ClientRatingRow> topSpentTableView;
  @FXML private TableColumn<ClientRatingRow, String> spentClientColumn;
  @FXML private TableColumn<ClientRatingRow, BigDecimal> spentValueColumn;

  @FXML private TableView<ClientRatingRow> topHoursTableView;
  @FXML private TableColumn<ClientRatingRow, String> hoursClientColumn;
  @FXML private TableColumn<ClientRatingRow, Double> hoursValueColumn;

  @FXML private TableView<ClientRatingRow> topVisitsTableView;
  @FXML private TableColumn<ClientRatingRow, String> visitsClientColumn;
  @FXML private TableColumn<ClientRatingRow, Integer> visitsValueColumn;

  public static class ClientRatingRow {
    private final String     clientName;
    private final BigDecimal totalSpent;
    private final double     totalHours;
    private final int        totalVisits;

    public ClientRatingRow(String clientName, BigDecimal totalSpent,
        double totalHours, int totalVisits) {
      this.clientName  = clientName;
      this.totalSpent  = totalSpent;
      this.totalHours  = totalHours;
      this.totalVisits = totalVisits;
    }

    public String     getClientName()  { return clientName; }
    public BigDecimal getTotalSpent()  { return totalSpent; }
    public double     getTotalHours()  { return totalHours; }
    public int        getTotalVisits() { return totalVisits; }
  }

  private final PersistanceSession dbSession = Launcher.getSessionContext();
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM");
  private final DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:00");

  @FXML
  public void initialize() {
    if (revenueChart != null) {
      revenueChart.setAnimated(false);
    }

    spentClientColumn.setCellValueFactory(
        data -> new SimpleStringProperty(data.getValue().getClientName()));
    spentValueColumn.setCellValueFactory(
        data -> new SimpleObjectProperty<>(data.getValue().getTotalSpent()));

    hoursClientColumn.setCellValueFactory(
        data -> new SimpleStringProperty(data.getValue().getClientName()));
    hoursValueColumn.setCellValueFactory(
        data -> new SimpleObjectProperty<>(data.getValue().getTotalHours()));

    visitsClientColumn.setCellValueFactory(
        data -> new SimpleStringProperty(data.getValue().getClientName()));
    visitsValueColumn.setCellValueFactory(
        data -> new SimpleObjectProperty<>(data.getValue().getTotalVisits()));

    periodComboBox.setItems(FXCollections.observableArrayList(
        "Сьогодні", "Вчора", "Останні 7 днів", "Поточний місяць"
    ));
    periodComboBox.setValue("Останні 7 днів");

    periodComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal != null) {
        customDatePicker.setValue(null);
        loadStatistics();
      }
    });

    customDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal != null) {
        periodComboBox.getSelectionModel().clearSelection();
        loadStatisticsForCustomDay(newVal);
      }
    });

    loadStatistics();
  }

  private void loadStatistics() {
    String selectedPeriod = periodComboBox.getValue();
    if (selectedPeriod == null) return;

    List<Payment> allPayments = dbSession.getAllPayments();
    LocalDate today = LocalDate.now();
    LocalDate startDate;

    switch (selectedPeriod) {
      case "Сьогодні" -> {
        loadStatisticsForCustomDay(today);
        return;
      }
      case "Вчора" -> {
        loadStatisticsForCustomDay(today.minusDays(1));
        return;
      }
      case "Останні 7 днів" -> startDate = today.minusDays(7);
      case "Поточний місяць" -> startDate = today.withDayOfMonth(1);
      default -> startDate = today.minusDays(7);
    }

    List<Payment> filteredPayments = allPayments.stream()
        .filter(p -> {
          LocalDate pDate = p.getPaymentDate().toLocalDate();
          return (!pDate.isBefore(startDate)) && (!pDate.isAfter(today));
        })
        .collect(Collectors.toList());

    Map<LocalDate, BigDecimal> incomeByDate = filteredPayments.stream()
        .collect(Collectors.groupingBy(
            p -> p.getPaymentDate().toLocalDate(),
            TreeMap::new,
            Collectors.mapping(Payment::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
        ));

    updateKpiLabels(filteredPayments);
    updateRatingTables(filteredPayments);

    revenueChart.getData().clear();
    if (xAxis != null) {
      xAxis.getCategories().clear();
    }
    XYChart.Series<String, Number> series = new XYChart.Series<>();

    incomeByDate.forEach((date, sum) -> {
      series.getData().add(new XYChart.Data<>(date.format(dateFormatter), sum));
    });

    revenueChart.getData().add(series);
  }

  private void loadStatisticsForCustomDay(LocalDate targetDate) {
    List<Payment> allPayments = dbSession.getAllPayments();

    List<Payment> dayPayments = allPayments.stream()
        .filter(p -> p.getPaymentDate().toLocalDate().equals(targetDate))
        .collect(Collectors.toList());

    Map<Integer, BigDecimal> incomeByHour = dayPayments.stream()
        .collect(Collectors.groupingBy(
            p -> p.getPaymentDate().getHour(),
            TreeMap::new,
            Collectors.mapping(Payment::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
        ));

    updateKpiLabels(dayPayments);
    updateRatingTables(dayPayments);

    revenueChart.getData().clear();
    if (xAxis != null) {
      xAxis.getCategories().clear();
    }
    XYChart.Series<String, Number> series = new XYChart.Series<>();

    for (int hour = 0; hour < 24; hour++) {
      BigDecimal sum = incomeByHour.getOrDefault(hour, BigDecimal.ZERO);
      String hourLabel = String.format("%02d:00", hour);
      series.getData().add(new XYChart.Data<>(hourLabel, sum));
    }

    revenueChart.getData().add(series);
  }

  private void updateKpiLabels(List<Payment> payments) {
    BigDecimal totalIncome = payments.stream()
        .map(Payment::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    int totalTransactions = payments.size();

    BigDecimal averageCheck = BigDecimal.ZERO;
    if (totalTransactions > 0) {
      averageCheck = totalIncome.divide(BigDecimal.valueOf(totalTransactions), 2, RoundingMode.HALF_UP);
    }

    totalPeriodIncomeLabel.setText(String.format("%.2f ₴", totalIncome));
    transactionCountLabel.setText(String.valueOf(totalTransactions));
    averagePaymentLabel.setText(String.format("%.2f ₴", averageCheck));
  }

  private void updateRatingTables(List<Payment> payments) {
    Map<String, BigDecimal> spentMap  = new HashMap<>();
    Map<String, Double>     hoursMap  = new HashMap<>();
    Map<String, Integer>    visitsMap = new HashMap<>();
    Map<String, String>     nickMap   = new HashMap<>();

    for (Payment p : payments) {
      String clientKey = p.getClientId().toString();

      if (!nickMap.containsKey(clientKey)) {
        String nick = dbSession.getClient(p.getClientId())
            .map(Client::getNickname)
            .orElse("Невідомий");
        nickMap.put(clientKey, nick);
      }

      spentMap.merge(clientKey, p.getAmount(), BigDecimal::add);

      double sessionHours = dbSession.getSession(p.getSessionId())
          .filter(s -> s.getEndTime() != null && s.getStartTime() != null)
          .map(s -> Duration.between(s.getStartTime(), s.getEndTime()).toMinutes() / 60.0)
          .orElse(0.0);
      hoursMap.merge(clientKey, sessionHours, Double::sum);

      visitsMap.merge(clientKey, 1, Integer::sum);
    }

    List<ClientRatingRow> rows = nickMap.keySet().stream()
        .map(key -> new ClientRatingRow(
            nickMap.get(key),
            spentMap.getOrDefault(key, BigDecimal.ZERO),
            hoursMap.getOrDefault(key, 0.0),
            visitsMap.getOrDefault(key, 0)
        ))
        .collect(Collectors.toList());

    topSpentTableView.setItems(FXCollections.observableArrayList(
        rows.stream()
            .sorted(Comparator.comparing(ClientRatingRow::getTotalSpent).reversed())
            .limit(10)
            .collect(Collectors.toList())
    ));

    topHoursTableView.setItems(FXCollections.observableArrayList(
        rows.stream()
            .sorted(Comparator.comparingDouble(ClientRatingRow::getTotalHours).reversed())
            .limit(10)
            .collect(Collectors.toList())
    ));

    topVisitsTableView.setItems(FXCollections.observableArrayList(
        rows.stream()
            .sorted(Comparator.comparingInt(ClientRatingRow::getTotalVisits).reversed())
            .limit(10)
            .collect(Collectors.toList())
    ));
  }
}
