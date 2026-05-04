package com.vladi.clubcontrolapp.domain.entities;

import java.util.Objects;
import java.util.UUID;

public class Admin {
  private final UUID id;
  private String username;
  private String password;

  public Admin(String username, String password) {
    this.id = UUID.randomUUID();
    this.username = username;
    this.password = password;
  }

  public Admin(UUID id, String username, String password) {
    this.id = id;
    this.username = username;
    this.password = password;
  }

  public UUID getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public boolean equals(Object o){
    if(this == o) return true;
    if(!(o instanceof Admin other)) return true;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode(){
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "Admin{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", password='" + password + '\'' +
        '}';
  }
}
