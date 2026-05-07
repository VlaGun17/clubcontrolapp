package com.vladi.clubcontrolapp.infrastructure.persistance.impl;

import com.vladi.clubcontrolapp.domain.entities.Payment;
import com.vladi.clubcontrolapp.domain.enums.MethodPayment;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.PaymentRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.ConnectionManager;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.exception.DatabaseException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PaymentRepositoryImpl implements PaymentRepository {

  private final ConnectionManager connectionManager;

  public PaymentRepositoryImpl(ConnectionManager connectionManager){
    this.connectionManager = connectionManager;
  }

  @Override
  public Optional<Payment> findBySessionId(UUID sessionId) {
    String sql = """
        SELECT id, client_id, session_id, amount, payment_date, method
        FROM payments
        WHERE session_id = ?
        """;

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setObject(1, sessionId);

      try(ResultSet rs = stmt.executeQuery()){
        if(rs.next()){
          return Optional.of(mapRow(rs));
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка пошуку транзакції за sessionId=" + sessionId, e);
    }
  }

  @Override
  public BigDecimal getTotalRevenue(LocalDate date) {
    String sql = """
        SELECT SUM(amount)
        FROM payments
        WHERE payment_date = ?
        """;

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, date);

      try(ResultSet rs = stmt.executeQuery()) {
        if(rs.next()){
          BigDecimal total = rs.getBigDecimal(1);
          return (total != null) ? total : BigDecimal.ZERO;
        }
      }
      return BigDecimal.ZERO;
    } catch (SQLException e) {
      throw new DatabaseException("Помилка сумування транзакцій за payment_date=" + date, e);
    }
  }

  @Override
  public List<Payment> findByMethod(MethodPayment method) {
    String sql = """
        SELECT id, client_id, session_id, amount, payment_date, method
        FROM payments
        WHERE method = ?
        """;
    List<Payment> payments = new ArrayList<>();

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setObject(1, method);

      try(ResultSet rs = stmt.executeQuery()){
       while(rs.next()){
         payments.add(mapRow(rs));
       }
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка пошуку транзакції за method=" + method, e);
    }
    return payments;
  }

  @Override
  public void save(Payment entity) {
    String sql = """
        INSERT INTO payments(id, client_id, session_id, amount, payment_date, method)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, entity.getId());
      stmt.setObject(2, entity.getClientId());
      stmt.setObject(3, entity.getSessionId());
      stmt.setBigDecimal(4, entity.getAmount());
      stmt.setObject(5, entity.getPaymentDate());
      stmt.setObject(6, entity.getPaymentMethod());

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected != 1) {
        throw new DatabaseException("Очікувався 1 рядок, вставлено: " + rowsAffected, null);
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка збереження транзакції: " + entity.getId(), e);
    }
  }

  @Override
  public Optional<Payment> findById(UUID id) {
    String sql = """
        SELECT id, client_id, session_id, amount, payment_date, method
        FROM payments
        WHERE id = ?
        """;

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setObject(1, id);

      try(ResultSet rs = stmt.executeQuery()){
        if(rs.next()){
          return Optional.of(mapRow(rs));
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка пошуку транзакції за id=" + id, e);
    }
  }

  @Override
  public List<Payment> findAll() {
    String sql = """
        SELECT id, client_id, session_id, amount, payment_date, method
        FROM payments
        ORDER BY payment_date
        """;
    List<Payment> payments = new ArrayList<>();

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){
      while(rs.next()){
        payments.add(mapRow(rs));
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка отримання списку транзакцій", e);
    }
    return payments;
  }

  @Override
  public boolean deleteById(UUID id) {
    String sql = "DELETE FROM payments WHERE id = ?";

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setObject(1, id);

      int rowsAffected = stmt.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      throw new DatabaseException("Помилка видалення транзакції з id=" + id, e);
    }
  }

  @Override
  public void update(Payment entity) {
  }

  private Payment mapRow(ResultSet rs) throws SQLException {
    return new Payment(
        rs.getObject("id", UUID.class),
        rs.getObject("client_id", UUID.class),
        rs.getObject("session_id", UUID.class),
        rs.getBigDecimal("amount"),
        rs.getObject("payment_date", LocalDate.class),
        rs.getString("method")
    );
  }
}
