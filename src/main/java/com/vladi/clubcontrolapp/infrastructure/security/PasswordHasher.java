package com.vladi.clubcontrolapp.infrastructure.security;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordHasher {
  public static String hash(String password){
    return BCrypt.withDefaults().hashToString(12, password.toCharArray());
  }

  public static boolean verify(String password, String hashedPassword) {
    BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
    return result.verified;
  }
}
