package com.vladi.clubcontrolapp.application.exception;

import java.util.HashMap;
import java.util.Map;

public class ValidationResult {
  private final Map<String, String> errors = new HashMap<>();

  public void addError(String field, String message) {
    errors.put(field, message);
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  public Map<String, String> getErrors() {
    return errors;
  }

  public String getFieldError(String field) {
    return errors.get(field);
  }
}
