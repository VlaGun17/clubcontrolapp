package com.vladi.clubcontrolapp.application.impl;

import com.vladi.clubcontrolapp.application.contract.RegistrationService;
import com.vladi.clubcontrolapp.application.exception.ValidationResult;
import com.vladi.clubcontrolapp.domain.entities.Admin;
import com.vladi.clubcontrolapp.infrastructure.security.PasswordHasher;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import java.util.UUID;

public class RegistrationServiceImpl implements RegistrationService {

  private final PersistanceSession session;

  public RegistrationServiceImpl(PersistanceSession session){
    this.session = session;
  }

  @Override
  public ValidationResult registerAdmin(String username, String password) {
    ValidationResult validation = new ValidationResult();

    if (username == null || username.trim().isEmpty()) {
      validation.addError("username", "Логін не може бути порожнім");
    } else if (session.getAdminByLogin(username).isPresent()) {
      validation.addError("username", "Цей логін уже зайнятий");
    }

    if (password == null || password.length() < 6) {
      validation.addError("password", "Пароль має містити мінімум 6 символів");
    }

    if (validation.hasErrors()) {
      return validation;
    }

    Admin newAdmin = new Admin(
        UUID.randomUUID(),
        username,
        PasswordHasher.hash(password)
    );

    session.addAdmin(newAdmin);
    session.commit();
    return validation;
  }
}
