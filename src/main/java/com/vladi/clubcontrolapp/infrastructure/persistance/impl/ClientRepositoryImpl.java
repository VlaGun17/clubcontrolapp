package com.vladi.clubcontrolapp.infrastructure.persistance.impl;

import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.ClientRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.ConnectionManager;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.exception.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientRepositoryImpl implements ClientRepository {

  private final ConnectionManager connectionManager;

  public ClientRepositoryImpl(ConnectionManager connectionManager){
    this.connectionManager = connectionManager;
  }

  @Override
  public Optional<Client> findByEmail(String email) {
    String sql = """
        SELECT id, nickname, email, balance, discount_percent, visit_count, registration_date
        FROM clients
        WHERE email = ?
        """;
    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, email);

      try(ResultSet rs = stmt.executeQuery()){
        if(rs.next()) {
          return Optional.of(mapRow(rs));
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка пошуку клієнта за email=" + email, e);
    }
  }

  @Override
  public List<Client> findByNameContaining(String name) {
    String sql  = """
        SELECT id, nickname, email, balance, discount_percent, visit_count, registration_date
        FROM clients
        WHERE LOWER(nickname) LIKE LOWER(?)
        ORDER BY nickname
        """;
    List<Client> clients = new ArrayList<>();

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setString(1, "%" + name + "%");

      try(ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          clients.add(mapRow(rs));
        }
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка пошуку клієнтів за nickname=" + name, e);
    }
    return clients;
  }

  @Override
  public List<Client> findByRegistrationDate(LocalDate registrationDate) {
    String sql  = """
        SELECT id, nickname, email, balance, discount_percent, visit_count, registration_date
        FROM clients
        WHERE LOWER(registration_date) LIKE LOWER(?)
        ORDER BY registration_date
        """;
    List<Client> clients = new ArrayList<>();

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setString(1, "%" + registrationDate + "%");

      try(ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          clients.add(mapRow(rs));
        }
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка пошуку клієнтів за registration_date=" + registrationDate, e);
    }
    return clients;
  }

  @Override
  public long count() {
    String sql = "SELECT COUNT(*) FROM clients";
    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      rs.next();
      return rs.getLong(1);
    } catch (SQLException e) {
      throw new DatabaseException("Помилка підрахунку клієнтів", e);
    }
  }

  @Override
  public void save(Client entity) {
    String sql = """
        INSERT INTO clients (id, nickname, email, balance, discount_percent, visit_count, registration_date)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, entity.getId());
      stmt.setString(2, entity.getNickname());
      stmt.setString(3, entity.getEmail());
      stmt.setBigDecimal(4, entity.getBalance());
      stmt.setInt(5, entity.getDiscountPercent());
      stmt.setInt(6, entity.getVisitCount());
      stmt.setObject(7, entity.getRegistrationDate());

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected != 1) {
        throw new DatabaseException("Очікувався 1 рядок, вставлено: " + rowsAffected, null);
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка збереження клієнта: " + entity.getId(), e);
    }
  }

  @Override
  public Optional<Client> findById(UUID id) {
    String sql = """
        SELECT id, nickname, email, balance, discount_percent, visit_count, registration_date
        FROM clients
        WHERE id = ?
        """;
    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, id);

      try(ResultSet rs = stmt.executeQuery()){
        if(rs.next()) {
          return Optional.of(mapRow(rs));
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка пошуку клієнта за id=" + id, e);
    }
  }

  @Override
  public List<Client> findAll() {
    String sql = """
        SELECT id, nickname, email, balance, discount_percent, visit_count, registration_date
        FROM clients
        ORDER BY nickname
        """;
    List<Client> clients = new ArrayList<>();
    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while(rs.next()) {
        clients.add(mapRow(rs));
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка отримання списку клієнтів", e);
    }
    return clients;
  }

  @Override
  public boolean deleteById(UUID id) {
    String sql = "DELETE FROM clients WHERE id = ?";

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setObject(1, id);

      int rowsAffected = stmt.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      throw new DatabaseException("Помилка видалення клієнта з id=" + id, e);
    }
  }

  @Override
  public void update(Client entity) {
    String sql = """
        UPDATE clients
        SET nickname = ?,
        email = ?,
        balance = ?,
        discount_percent = ?,
        visit_count = ?
        WHERE id = ?
        """;
    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setString(1, entity.getNickname());
      stmt.setString(2,entity.getEmail());
      stmt.setBigDecimal(3, entity.getBalance());
      stmt.setInt(4, entity.getDiscountPercent());
      stmt.setInt(5, entity.getVisitCount());
      stmt.setObject(6, entity.getId());

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected == 0) {
        throw new DatabaseException(
            "Клієнта з id=" + entity.getId() + " не знайдено для оновлення", null
        );
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка оновлення клієнта: " + entity.getId(), e);
    }
  }

  private Client mapRow(ResultSet rs) throws SQLException {
    return new Client(
        rs.getObject("id", UUID.class),
        rs.getString("nickname"),
        rs.getString("email"),
        rs.getBigDecimal("balance"),
        rs.getInt("discount_percent"),
        rs.getInt("visit_count"),
        rs.getObject("registration_date", LocalDate.class)
    );
  }
}
