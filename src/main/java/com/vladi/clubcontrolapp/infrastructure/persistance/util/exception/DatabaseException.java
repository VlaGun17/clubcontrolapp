package com.vladi.clubcontrolapp.infrastructure.persistance.util.exception;

public class DatabaseException extends RuntimeException{
  public DatabaseException (String message, Throwable cause) {
    super(message, cause);
  }
}
