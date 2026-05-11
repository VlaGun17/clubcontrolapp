package com.vladi.clubcontrolapp.application.impl;

import com.vladi.clubcontrolapp.application.contract.AdminService;
import com.vladi.clubcontrolapp.application.contract.AuthService;
import com.vladi.clubcontrolapp.domain.entities.Admin;
import com.vladi.clubcontrolapp.infrastructure.security.PasswordHasher;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import java.util.Optional;
import java.util.UUID;

public class AuthServiceImpl implements AuthService {

  private final PersistanceSession persistanceSession;

  public AuthServiceImpl(PersistanceSession persistanceSession){
    this.persistanceSession = persistanceSession;
  }

  @Override
  public Optional<Admin> login(String username, String password) {
    return persistanceSession.getAdminByLogin(username)
        .filter(admin -> PasswordHasher.verify(password, admin.getPassword()));
  }

}
