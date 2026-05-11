package com.vladi.clubcontrolapp.domain.entities;

import com.vladi.clubcontrolapp.domain.enums.ComputerStatus;
import com.vladi.clubcontrolapp.domain.enums.ComputerType;
import java.util.Objects;
import java.util.UUID;

public class Computer {

  private final UUID id;
  private int computerNumber;
  private String computerType;
  private String computerStatus;

  public Computer(int computerNumber, String computerType, String computerStatus) {
    this.id = UUID.randomUUID();
    this.computerNumber = computerNumber;
    this.computerType = String.valueOf(ComputerType.Common);
    this.computerStatus = String.valueOf(ComputerStatus.Available);
  }

  public Computer(UUID id, int computerNumber, String computerType, String computerStatus) {
    this.id = id;
    this.computerNumber = computerNumber;
    this.computerType = computerType;
    this.computerStatus = computerStatus;
  }

  public UUID getId() {
    return id;
  }

  public int getComputerNumber() {
    return computerNumber;
  }

  public void setComputerNumber(int computerNumber) {
    this.computerNumber = computerNumber;
  }

  public String getComputerType() {
    return computerType;
  }

  public void setComputerType(String computerType) {
    this.computerType = computerType;
  }

  public String getComputerStatus() {
    return computerStatus;
  }

  public void setComputerStatus(String computerStatus) {
    this.computerStatus = computerStatus;
  }

  @Override
  public boolean equals(Object o){
    if(this == o) return true;
    if(!(o instanceof Computer other)) return true;
    return id != null && id.equals(other.id);
  }

  @Override
  public int hashCode(){
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "Computer{" +
        "id=" + id +
        ", computer_number=" + computerNumber +
        ", computer_type='" + computerType + '\'' +
        ", computer_status='" + computerStatus + '\'' +
        '}';
  }
}
