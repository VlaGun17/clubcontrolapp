package com.vladi.clubcontrolapp.infrastructure.persistance.impl;

import com.vladi.clubcontrolapp.domain.entities.Admin;
import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.AdminRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.ConnectionManager;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.exception.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AdminRepositoryImpl implements AdminRepository {

  private final ConnectionManager connectionManager;

  public AdminRepositoryImpl(ConnectionManager connectionManager){
    this.connectionManager = connectionManager;
  }

  @Override
  public Optional<Admin> findByLogin(String login) {
    String sql = """
        SELECT id, username, password_hash
        FROM admins
        WHERE username = ?
        """;
    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setString(1, login);

      try(ResultSet rs = stmt.executeQuery()){
        if(rs.next()){
          return Optional.of(mapRow(rs));
        }
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка пошуку адміна за login=" + login, e);
    }
  }

  @Override
  public void save(Admin entity) {
    String sql = """
        INSERT INTO admins(id, username, password_hash)
        VALUES(?, ?, ?)
        """;
    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setObject(1,entity.getId());
      stmt.setString(2,entity.getUsername());
      stmt.setString(3, entity.getPassword());

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected != 1) {
        throw new DatabaseException("Очікувався 1 рядок, вставлено: " + rowsAffected, null);
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка збереження адміна: " + entity.getId(), e);
    }
  }

  @Override
  public Optional<Admin> findById(UUID id) {
    String sql = """
        SELECT id, username, password_hash
        FROM admins
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
      throw new DatabaseException("Помилка пошуку адміна за id=" + id, e);
    }
  }

  @Override
  public List<Admin> findAll() {
    String sql = """
        SELECT id, username, password_hash
        FROM admins
        ORDER BY username
        """;
    List<Admin> admins = new ArrayList<>();
    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
      while(rs.next()) {
        admins.add(mapRow(rs));
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка отримання списку адмінів", e);
    }
    return admins;
  }

  @Override
  public boolean deleteById(UUID id) {
    String sql = "DELETE FROM admins WHERE id = ?";

    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setObject(1, id);

      int rowsAffected = stmt.executeUpdate();
      return rowsAffected > 0;
    } catch (SQLException e) {
      throw new DatabaseException("Помилка видалення адміна з id=" + id, e);
    }
  }

  @Override
  public void update(Admin entity) {
    String sql = """
        UPDATE admins
        SET username = ?,
        password_hash = ?
        WHERE id = ?
        """;
    try(Connection conn = connectionManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
      stmt.setString(1, entity.getUsername());
      stmt.setString(2,entity.getPassword());
      stmt.setObject(3, entity.getId());

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected == 0) {
        throw new DatabaseException(
            "Адміна з id=" + entity.getId() + " не знайдено для оновлення", null
        );
      }
    } catch (SQLException e) {
      throw new DatabaseException("Помилка оновлення адміна: " + entity.getId(), e);
    }
  }

  private Admin mapRow(ResultSet rs) throws SQLException {
    return new Admin(
        rs.getObject("id", UUID.class),
        rs.getString("username"),
        rs.getString("password_hash")
    );
  }
}
