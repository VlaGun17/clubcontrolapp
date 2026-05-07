package com.vladi.clubcontrolapp.infrastructure.persistance.util;

import java.sql.Connection;

public class ConnectionManager implements AutoCloseable {

  private final ConnectionPool pool;

  public ConnectionManager(PoolConfig config) {
    this.pool = new ConnectionPool(config);
  }

  public static ConnectionManager forH2(String path) {
    return new ConnectionManager(PoolConfig.forH2(path));
  }

  public Connection getConnection() {
    return pool.getConnection();
  }

  public String poolStats() {
    return String.format("Pool: %d available / %d total / %d max",
        pool.availableCount(), pool.totalCount(), pool.config().maxConnections());
  }

  @Override
  public void close() {
    pool.close();
  }
}
