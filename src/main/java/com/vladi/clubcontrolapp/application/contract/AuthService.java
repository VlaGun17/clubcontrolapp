package com.vladi.clubcontrolapp.application.contract;

import com.vladi.clubcontrolapp.domain.entities.Admin;
import java.util.Optional;

public interface AuthService {
  Optional<Admin> login(String username, String password);
}
