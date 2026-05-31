package com.vladi.clubcontrolapp.domain.enums;

public enum ComputerStatus {
  Available("Вільний"),
  Busy("Зайнятий"),
  Maintenance("Обслуговування");

  private final String displayName;

  ComputerStatus(String displayName){
    this.displayName = displayName;
  }

  public String getDisplayName(){
    return displayName;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
