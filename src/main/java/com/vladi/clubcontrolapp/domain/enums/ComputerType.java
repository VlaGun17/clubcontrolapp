package com.vladi.clubcontrolapp.domain.enums;

public enum ComputerType {
  Common("Звичайний"),
  VIP("ВІП-зона");

  private final String displayName;

  ComputerType(String displayName){
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
