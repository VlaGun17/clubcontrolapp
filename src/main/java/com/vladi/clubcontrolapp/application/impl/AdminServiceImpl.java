package com.vladi.clubcontrolapp.application.impl;

import com.vladi.clubcontrolapp.application.contract.AdminService;
import com.vladi.clubcontrolapp.domain.entities.Admin;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import java.util.Optional;
import java.util.UUID;

public class AdminServiceImpl implements AdminService {

  private final PersistanceSession session;

  public AdminServiceImpl(PersistanceSession session){
    this.session = session;
  }

  @Override
  public Optional<Admin> getAdminByLogin(String login) {
    return session.getAdminByLogin(login);
  }

  @Override
  public Admin create(Admin entity) {
    session.addAdmin(entity);
    session.commit();
    return entity;
  }

  @Override
  public Admin update(UUID uuid, Admin entity) {
    session.updateAdmin(entity);
    session.commit();
    return entity;
  }

  @Override
  public void delete(UUID uuid) {
    session.getAdmin(uuid).ifPresent(admin -> {
      session.removeAdmin(admin);
      session.commit();
    });
  }

  @Override
  public Optional<Admin> findById(UUID uuid) {
    return session.findById(uuid);
  }
}
