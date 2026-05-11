package com.vladi.clubcontrolapp.application.contract;

import com.vladi.clubcontrolapp.application.exception.ValidationResult;

public interface RegistrationService {
  ValidationResult registerAdmin(String username, String password);
}
