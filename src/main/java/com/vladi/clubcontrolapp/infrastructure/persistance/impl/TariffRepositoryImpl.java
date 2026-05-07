package com.vladi.clubcontrolapp.infrastructure.persistance.impl;

import com.vladi.clubcontrolapp.domain.entities.Tariff;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.TariffRepository;
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

public class TariffRepositoryImpl implements TariffRepository {

  private final ConnectionManager connectionManager;

  public TariffRepositoryImpl(ConnectionManager connectionManager){
    this.connectionManager = connectionManager;
  }

  @Override
  public Optional<Tariff> findCurrentTariff(LocalDate now) {
    return Optional.empty();
  }

  @Override
  public List<Tariff> findNightTariffs() {
    String sql = """
        SELECT id, name, price_per_hour, is_night
        FROM tariffs
        WHERE is_night = true
        """;
    List<Tariff> tariffs = new ArrayList<>();

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){
      while(rs.next()){
        tariffs.add(mapRow(rs));
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка пошуку нічного тарифу", e);
    }
    return tariffs;
  }

  @Override
  public Optional<Tariff> findByName(String name) {
    String sql = """
        SELECT id, name, price_per_hour, is_night
        FROM tariffs
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
      throw new DatabaseException("Помилка пошуку тарифу за name=" + name, e);
    }
  }

  @Override
  public void save(Tariff entity) {
    String sql = """
        INSERT INTO tariffs (id, name, price_per_hour, is_night)
        VALUES (?, ?, ?, ?)
        """;
    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setObject(1, entity.getId());
      stmt.setString(2, entity.getName());
      stmt.setBigDecimal(3, entity.getPricePerHour());
      stmt.setBoolean(4, entity.isNight());

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected != 1) {
        throw new DatabaseException("Очікувався 1 рядок, вставлено: " + rowsAffected, null);
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка збереження тарифу: " + entity.getId(), e);
    }
  }

  @Override
  public Optional<Tariff> findById(UUID id) {
    String sql = """
        SELECT id, name, price_per_hour, is_night
        FROM tariffs
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
      throw new DatabaseException("Помилка пошуку тарифу за id=" + id, e);
    }
  }

  @Override
  public List<Tariff> findAll() {
    String sql = """
        SELECT id, name, price_per_hour, is_night
        FROM tariffs
        ORDER BY name
        """;
    List<Tariff> tariffs = new ArrayList<>();

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){
      while(rs.next()){
        tariffs.add(mapRow(rs));
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка отримання списку тарифів", e);
    }
    return tariffs;
  }

  @Override
  public boolean deleteById(UUID id) {
    String sql = " DELETE FROM tariffs WHERE id = ?";

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setObject(1, id);

      int rowsAffected = stmt.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      throw new DatabaseException("Помилка видалення тарифу з id=" + id, e);
    }
  }

  @Override
  public void update(Tariff entity) {
    String sql = """
        UPDATE tariffs
        SET name = ?,
        price_per_hour = ?,
        is_night = ?
        WHERE id = ?
        """;

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setString(1, entity.getName());
      stmt.setBigDecimal(2, entity.getPricePerHour());
      stmt.setBoolean(3, entity.isNight());
      stmt.setObject(4, entity.getId());

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected == 0) {
        throw new DatabaseException(
            "Тариф з id=" + entity.getId() + " не знайдено для оновлення", null
        );
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка оновлення тарифу: " + entity.getId(), e);
    }
  }

  private Tariff mapRow(ResultSet rs) throws SQLException{
    return new Tariff(
        rs.getObject("id", UUID.class),
        rs.getString("name"),
        rs.getBigDecimal("price_per_hour"),
        rs.getBoolean("is_night")
        );
  }
}
