package com.vladi.clubcontrolapp.infrastructure.persistance.impl;

import com.vladi.clubcontrolapp.domain.entities.Session;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.SessionRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.ConnectionManager;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.exception.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SessionRepositoryImpl implements SessionRepository {

  private final ConnectionManager connectionManager;

  public SessionRepositoryImpl(ConnectionManager connectionManager){
    this.connectionManager = connectionManager;
  }

  @Override
  public List<Session> findAllActive() {
    String sql = """
        SELECT id, client_id, computer_id, tariff_id, start_time, end_time, total_cost, is_active
        FROM sessions
        WHERE is_active = true
        """;
    List<Session> sessions = new ArrayList<>();

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){
      while(rs.next()){
        sessions.add(mapRow(rs));
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка отримання списку активних сесій", e);
    }
    return sessions;
  }

  @Override
  public List<Session> findByComputerId(UUID computerId) {
    String sql = """
        SELECT id, client_id, computer_id, tariff_id, start_time, end_time, total_cost, is_active
        FROM sessions
        WHERE computer_id = ?
        """;
    List<Session> sessions = new ArrayList<>();

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setObject(1, computerId);

      try(ResultSet rs = stmt.executeQuery()){
        while(rs.next()){
          sessions.add(mapRow(rs));
        }
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка отримання списку сесій з computerId: " + computerId, e);
    }
    return sessions;
  }

  @Override
  public List<Session> findByDate(LocalDateTime date) {
    String sql = """
        SELECT id, client_id, computer_id, tariff_id, start_time, end_time, total_cost, is_active
        FROM sessions
        WHERE start_time >= ? AND start_time < ?
        """;
    List<Session> sessions = new ArrayList<>();

    LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
    LocalDateTime endOfDay = startOfDay.plusDays(1);

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setObject(1, startOfDay);
      stmt.setObject(2, endOfDay);

      try(ResultSet rs = stmt.executeQuery()){
        while(rs.next()){
          sessions.add(mapRow(rs));
        }
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка отримання списку сесій за дату: " + date, e);
    }
    return sessions;
  }

  @Override
  public void save(Session entity) {
    String sql = """
        INSERT INTO sessions (id, client_id, computer_id, tariff_id, start_time, end_time, total_cost, is_active)
        VALUES(?, ? ,? ,? , ?, ?, ?, ?)
        """;

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, entity.getId());
      stmt.setObject(2, entity.getClientId());
      stmt.setObject(3, entity.getComputerId());
      stmt.setObject(4, entity.getTariffId());
      stmt.setObject(5, entity.getStartTime());
      stmt.setObject(6, entity.getEndTime());
      stmt.setBigDecimal(7, entity.getTotalCost());
      stmt.setBoolean(8, entity.isActive());

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected != 1) {
        throw new DatabaseException("Очікувався 1 рядок, вставлено: " + rowsAffected, null);
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка збереження сесії: " + entity.getId(), e);
    }
  }

  @Override
  public Optional<Session> findById(UUID id) {
    String sql = """
        SELECT id, client_id, computer_id, tariff_id, start_time, end_time, total_cost, is_active
        FROM sessions
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
      throw new DatabaseException("Помилка пошуку сесії за id=" + id, e);
    }
  }

  @Override
  public List<Session> findAll() {
    String sql = """
        SELECT id, client_id, computer_id, tariff_id, start_time, end_time, total_cost, is_active
        FROM sessions
        """;
    List<Session> sessions = new ArrayList<>();
    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while(rs.next()) {
        sessions.add(mapRow(rs));
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка отримання списку сесій", e);
    }
    return sessions;
  }

  @Override
  public boolean deleteById(UUID id) {
    String sql = "DELETE FROM sessions WHERE id = ?";

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setObject(1, id);

      int rowsAffected = stmt.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      throw new DatabaseException("Помилка видалення сесії з id=" + id, e);
    }
  }

  @Override
  public void update(Session entity) {
    String sql = """
        UPDATE sessions
        SET start_time = ?,
        end_time = ?,
        total_cost = ?,
        is_active = ?
        WHERE id = ?
        """;
    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setObject(1, entity.getStartTime());
      stmt.setObject(2, entity.getEndTime());
      stmt.setBigDecimal(3, entity.getTotalCost());
      stmt.setBoolean(4, entity.isActive());
      stmt.setObject(5, entity.getId());

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected == 0) {
        throw new DatabaseException(
            "Сесію з id=" + entity.getId() + " не знайдено для оновлення", null
        );
      }
      } catch (SQLException e) {
      throw new DatabaseException("Помилка оновлення сесії: " + entity.getId(), e);
    }
  }

  private Session mapRow(ResultSet rs) throws SQLException {
    return new Session(
        rs.getObject("id", UUID.class),
        rs.getObject("client_id", UUID.class),
        rs.getObject("computer_id", UUID.class),
        rs.getObject("tariff_id", UUID.class),
        rs.getObject("start_time", LocalDateTime.class),
        rs.getObject("end_time", LocalDateTime.class),
        rs.getBigDecimal("total_cost"),
        rs.getBoolean("is_active")
    );
  }
}
