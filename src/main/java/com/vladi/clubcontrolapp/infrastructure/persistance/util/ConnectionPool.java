package com.vladi.clubcontrolapp.infrastructure.persistance.util;

import com.vladi.clubcontrolapp.infrastructure.persistance.util.exception.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPool implements AutoCloseable {
  private PoolConfig config;
  private final BlockingQueue<Connection> availableConnections;
  private final AtomicInteger totalConnections = new AtomicInteger(0);
  private volatile boolean closed = false;

  public ConnectionPool(PoolConfig config) {
    this.config = config;
    this.availableConnections = new ArrayBlockingQueue<>(config.maxConnections());

    try {
      for(int i = 0; i < config.minConnections(); i++){
        availableConnections.add(createRealConnection());
      }
    } catch (SQLException e) {
      close();
      throw new DatabaseException("Не вдалося ініціалізувати Connection Pool", e);
    }

    System.out.printf("[Pool] Ініціалізовано: %d з'єднань готові%n",
        config.minConnections());
  }

  public Connection getConnection() {
    if (closed) {
      throw new DatabaseException("Connection Pool закрито", null);
    }

    Connection conn = availableConnections.poll();

    if (conn == null) {
      int current = totalConnections.get();
      if (current < config.maxConnections()) {
        if (totalConnections.compareAndSet(current, current + 1)) {
          try {
            conn = createRealConnection();
            System.out.printf("[Pool] Нове з'єднання: всього %d/%d%n",
                totalConnections.get(), config.maxConnections());
          } catch (SQLException e) {
            totalConnections.decrementAndGet();
            throw new DatabaseException("Не вдалося створити з'єднання", e);
          }
        }
      }

      if (conn == null) {
        try {
          conn = availableConnections.poll(config.timeoutMs(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new DatabaseException("Очікування з'єднання перервано", e);
        }

        if (conn == null) {
          throw new DatabaseException(
              String.format("З'єднання недоступне впродовж %d мс. " +
                      "Пул вичерпано (%d/%d)",
                  config.timeoutMs(),
                  totalConnections.get(),
                  config.maxConnections()), null
          );
        }
      }
    }
    return new PooledConnection(conn, this);
  }

  void returnConnection(Connection realConnection){
    if(closed){
      closeQuietly(realConnection);
      totalConnections.decrementAndGet();
      return;
    }

    if(isConnectionValid(realConnection)) {
      availableConnections.offer(realConnection);
    }
    else{
      closeQuietly(realConnection);
      totalConnections.decrementAndGet();
      System.out.println("[Pool] З'єднання відхилено (невалідне), видалено з пулу");
    }
  }

  private Connection createRealConnection() throws SQLException {
    Connection conn = DriverManager.getConnection(config.url(), config.user(), config.password());
    totalConnections.incrementAndGet();
    return conn;
  }

  private boolean isConnectionValid(Connection conn) {
    try {
      // isValid(timeout) — стандартний JDBC-метод: надсилає ping до БД
      return !conn.isClosed() && conn.isValid(1);
    } catch (SQLException e) {
      return false;
    }
  }

  private void closeQuietly(Connection conn) {
    try { conn.close(); } catch (SQLException ignored) {}
  }

  public int availableCount() { return availableConnections.size(); }

  public int totalCount()     { return totalConnections.get(); }

  public PoolConfig config() { return config; }

  @Override
  public void close() {
    closed = true;
    List<Connection> toClose = new ArrayList<>();
    availableConnections.drainTo(toClose);
    toClose.forEach(this::closeQuietly);
    System.out.printf("[Pool] Закрито. Закрито %d з'єднань%n", toClose.size());
  }
}
