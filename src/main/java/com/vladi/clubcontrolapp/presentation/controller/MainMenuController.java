package com.vladi.clubcontrolapp.presentation.controller;

import com.vladi.clubcontrolapp.Launcher;
import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.enums.ComputerStatus;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import com.vladi.clubcontrolapp.presentation.util.NavigationManager;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenuController {
  @FXML private FlowPane computerContainer;
  @FXML private TextField searchField;
  @FXML private ComboBox sortByStatus;

  private final PersistanceSession session = Launcher.getSessionContext();

  @FXML
  public void initialize() {
    sortByStatus.getItems().addAll("Всі", "Available", "Busy");
    sortByStatus.setValue("Всі");

    renderComputers("");

    searchField.textProperty().addListener((obs, oldVal, newVal) -> {
      renderComputers(newVal);
    });
    sortByStatus.valueProperty().addListener((obs, oldVal, newVal) -> {
      renderComputers(searchField.getText());
    });
  }

  private void renderComputers(String filter) {
    computerContainer.getChildren().clear();
    String selectedStatus = sortByStatus.getValue().toString();

    session.getAllComputers().stream()
        .filter(c -> String.valueOf(c.getComputerNumber()).contains(filter))
        .filter(c -> {
          if (selectedStatus == null || selectedStatus.equals("Всі")) return true;
          return c.getComputerStatus().equalsIgnoreCase(selectedStatus);
        })
        .forEach(pc -> {
          VBox card = createCard(pc);
          computerContainer.getChildren().add(card);
        });
  }

  @FXML
  private void handleShowAddComputer() {
    Stage stage = (Stage) computerContainer.getScene().getWindow();
    NavigationManager.navigate(stage, "/com/vladi/clubcontrolapp/views/add_computer.fxml");
  }

  public void switchToMainScene() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vladi/clubcontrolapp/views/main_menu.fxml"));
      Stage stage = (Stage) computerContainer.getScene().getWindow();
      stage.setScene(new Scene(loader.load()));
    } catch (IOException e) {
      e.printStackTrace();
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
      Button deleteBtn = (Button) card.lookup("#deleteBtn");

      nameLabel.setText("PC-" + pc.getComputerNumber());
      statusLabel.setText(pc.getComputerStatus().toUpperCase());

      if ("Available".equalsIgnoreCase(pc.getComputerStatus())) {
        statusLabel.setStyle("-fx-text-fill: #4caf50;");
        actionBtn.setText("ЗАНЯТИ");
      } else {
        statusLabel.setStyle("-fx-text-fill: #f44336;");
        actionBtn.setText("КЕРУВАТИ");
      }

      actionBtn.setOnAction(event -> {
        if ("Available".equalsIgnoreCase(pc.getComputerStatus())){
          pc.setComputerStatus(ComputerStatus.Busy.name());

          session.updateComputer(pc);
          session.commit();

          renderComputers(searchField.getText());

          System.out.println("Статус ПК №" + pc.getComputerNumber() + " змінено на Busy");
        } else {
          System.out.println("Відкриття меню керування для ПК №" + pc.getComputerNumber());
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
}
