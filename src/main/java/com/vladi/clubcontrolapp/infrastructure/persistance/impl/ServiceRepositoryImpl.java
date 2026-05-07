package com.vladi.clubcontrolapp.infrastructure.persistance.impl;

import com.vladi.clubcontrolapp.domain.entities.Service;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.ServiceRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.ConnectionManager;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.exception.DatabaseException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ServiceRepositoryImpl implements ServiceRepository {

  private final ConnectionManager connectionManager;

  public ServiceRepositoryImpl(ConnectionManager connectionManager){
    this.connectionManager = connectionManager;
  }

  @Override
  public Optional<Service> findByName(String name) {
    String sql = """
        SELECT id, name, price
        FROM services
        WHERE name = ?
        """;
    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setString(1, name);
      try(ResultSet rs = stmt.executeQuery()){
        if(rs.next()){
          return Optional.of(mapRow(rs));
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка пошуку послуги за name=" + name, e);
    }
  }

  @Override
  public List<Service> findByPriceRange(BigDecimal min, BigDecimal max) {
    String sql = """
        SELECT id, name, price
        FROM services
        WHERE price > ? AND price < ?
        """;
    List<Service> services = new ArrayList<>();

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setBigDecimal(1, min);
      stmt.setBigDecimal(2, max);

      try(ResultSet rs = stmt.executeQuery()){
        while(rs.next()){
          services.add(mapRow(rs));
        }
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка отримання списку сервісів в діапазоні від " + min + " до " + max + ":", e);
    }
    return services;
  }

  @Override
  public void save(Service entity) {
    String sql = """
        INSERT INTO services (id, name, price)
        VALUES (?, ?, ?)
        """;

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, entity.getId());
      stmt.setString(2, entity.getName());
      stmt.setBigDecimal(3, entity.getPrice());

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected != 1) {
        throw new DatabaseException("Очікувався 1 рядок, вставлено: " + rowsAffected, null);
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка збереження послуги: " + entity.getId(), e);
    }
  }

  @Override
  public Optional<Service> findById(UUID id) {
    String sql = """
        SELECT id, name, price
        FROM services
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
      throw new DatabaseException("Помилка пошуку послуги за id=" + id, e);
    }
  }

  @Override
  public List<Service> findAll() {
    String sql = """
        SELECT id, name, price
        FROM services
        ORDER BY name
        """;
    List<Service> services = new ArrayList<>();

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){

      while(rs.next()){
        services.add(mapRow(rs));
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка отримання списку сервісів", e);
    }
    return services;
  }

  @Override
  public boolean deleteById(UUID id) {
    String sql = "DELETE FROM services WHERE id = ?";

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setObject(1, id);

      int rowsAffected = stmt.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      throw new DatabaseException("Помилка видалення послуги з id=" + id, e);
    }
  }

  @Override
  public void update(Service entity) {
    String sql = """
        UPDATE services
        SET name = ?,
        price = ?
        WHERE id = ?
        """;
    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setString(1, entity.getName());
      stmt.setBigDecimal(2, entity.getPrice());
      stmt.setObject(3, entity.getId());

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected == 0) {
        throw new DatabaseException(
            "Послугу з id=" + entity.getId() + " не знайдено для оновлення", null
        );
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка оновлення послуги: " + entity.getId(), e);
    }
  }

  private Service mapRow(ResultSet rs) throws SQLException {
    return new Service(
        rs.getObject("id", UUID.class),
        rs.getString("name"),
        rs.getBigDecimal("price")
    );
  }
}
