package com.vladi.clubcontrolapp.infrastructure.persistance.impl;

import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.enums.ComputerStatus;
import com.vladi.clubcontrolapp.domain.enums.ComputerType;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.ComputerRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.ConnectionManager;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.exception.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ComputerRepositoryImpl implements ComputerRepository {

  private final ConnectionManager connectionManager;

  public ComputerRepositoryImpl(ConnectionManager connectionManager){
    this.connectionManager = connectionManager;
  }

  @Override
  public List<Computer> findByComputerType(ComputerType computerType) {
    String sql = """
        SELECT * FROM computers
        WHERE type = ?
        """;
    List<Computer> computers = new ArrayList<>();

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, computerType);

      try(ResultSet rs = stmt.executeQuery()){
        while(rs.next()){
          computers.add(mapRow(rs));
        }
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка пошуку комп'ютера за computerType=" + computerType, e);
    }
    return computers;
  }

  @Override
  public List<Computer> findByComputerStatus(ComputerStatus computerStatus) {
    String sql = """
        SELECT * FROM computers
        WHERE status = ?
        """;
    List<Computer> computers = new ArrayList<>();

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, computerStatus);

      try(ResultSet rs = stmt.executeQuery()){
        while(rs.next()){
          computers.add(mapRow(rs));
        }
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка пошуку комп'ютера за computerStatus=" + computerStatus, e);
    }
    return computers;
  }

  @Override
  public Optional<Computer> findByNumber(int number) {
    String sql = """
        SELECT * FROM computers
        WHERE comp_number = ?
        """;

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, number);

      try(ResultSet rs = stmt.executeQuery()){
        if(rs.next()){
          return Optional.of(mapRow(rs));
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка пошуку комп'ютера за number=" + number, e);
    }
  }

  @Override
  public void save(Computer entity) {
    String sql = """
        INSERT INTO computers (id, comp_number, type, status)
        VALUES (?, ?, ?, ?)
        """;

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setObject(1, entity.getId());
      stmt.setInt(2, entity.getComputerNumber());
      stmt.setObject(3, entity.getComputerType(), Types.OTHER);
      stmt.setObject(4, entity.getComputerStatus(), Types.OTHER);

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected != 1) {
        throw new DatabaseException("Очікувався 1 рядок, вставлено: " + rowsAffected, null);
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка збереження комп'ютера: " + entity.getId(), e);
    }
  }

  @Override
  public Optional<Computer> findById(UUID id) {
    String sql = """
        SELECT * FROM computers
        WHERE id = ?
        """;

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1, id);

      try(ResultSet rs = stmt.executeQuery()){
        if(rs.next()){
          return Optional.of(mapRow(rs));
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка пошуку комп'ютера за id=" + id, e);
    }
  }

  @Override
  public List<Computer> findAll() {
    String sql = """
        SELECT id, comp_number, type, status
        FROM computers
        ORDER BY comp_number
        """;
    List<Computer> computers = new ArrayList<>();

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while(rs.next()) {
        computers.add(mapRow(rs));
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка отримання списку комп'ютерів", e);
    }
    return computers;
  }

  @Override
  public boolean deleteById(UUID id) {
    String sql = """
        DELETE FROM computers WHERE id = ?
        """;

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setObject(1, id);

      int rowsAffected = stmt.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      throw new DatabaseException("Помилка видалення комп'ютера з id=" + id, e);
    }
  }

  @Override
  public void update(Computer entity) {
    String sql = """
        UPDATE computers
        SET comp_number = ?,
        type = ?,
        status = ?
        WHERE id = ?
        """;
    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setInt(1, entity.getComputerNumber());
      stmt.setObject(2, entity.getComputerType(), Types.OTHER);
      stmt.setObject(3, entity.getComputerStatus(), Types.OTHER);
      stmt.setObject(4, entity.getId());

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected == 0) {
        throw new DatabaseException(
            "Комп'ютер з id=" + entity.getId() + " не знайдено для оновлення", null
        );
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка оновлення комп'ютера: " + entity.getId(), e);
    }
  }

  private Computer mapRow(ResultSet rs) throws SQLException {
    return new Computer(
        rs.getObject("id", UUID.class),
        rs.getInt("comp_number"),
        rs.getString("type"),
        rs.getString("status")
    );
  }
}
