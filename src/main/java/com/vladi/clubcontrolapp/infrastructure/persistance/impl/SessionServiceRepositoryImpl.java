package com.vladi.clubcontrolapp.infrastructure.persistance.impl;

import com.vladi.clubcontrolapp.domain.entities.SessionService;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.SessionServiceRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.ConnectionManager;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.exception.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SessionServiceRepositoryImpl implements SessionServiceRepository {

  private final ConnectionManager connectionManager;

  public SessionServiceRepositoryImpl(ConnectionManager connectionManager){
    this.connectionManager = connectionManager;
  }
  @Override
  public void save(SessionService entity) {
    String sql = "INSERT INTO session_services (session_id, service_id, quantity) VALUES (?, ?, ?)";
    try (Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, entity.getSessionId());
      stmt.setObject(2, entity.getServiceId());
      stmt.setInt(3, entity.getQuantity());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new DatabaseException("Помилка додавання послуги до сесії", e);
    }
  }

  @Override
  public List<SessionService> findBySessionId(UUID sessionId) {
    List<SessionService> services = new ArrayList<>();
    String sql = "SELECT * FROM session_services WHERE session_id = ?";

    try (Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setObject(1, sessionId);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        SessionService ss = new SessionService(
            (UUID) rs.getObject("session_id"),
            (UUID) rs.getObject("service_id"),
            rs.getInt("quantity")
        );
        services.add(ss);
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка при отриманні послуг сесії", e);
    }
    return services;
  }
}
