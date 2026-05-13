package com.vladi.clubcontrolapp.infrastructure.persistance.util;

public record PoolConfig(
    String url,
    String user,
    String password,
    int minConnections,
    int maxConnections,
    long timeoutMs
) {

  public PoolConfig {
    if(minConnections < 1) throw new IllegalArgumentException("minConnections має бути > 1");
    if (maxConnections < minConnections) throw new IllegalArgumentException("maxConnections >= minConnections");
    if (timeoutMs < 0) throw new IllegalArgumentException("timeoutMs > 0");
  }

  public static PoolConfig forH2(String path){
    return new PoolConfig(
        "jdbc:h2:" + path + ";MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "sa",
        "",
        2,
        10,
        5000L
    );
  }
}
