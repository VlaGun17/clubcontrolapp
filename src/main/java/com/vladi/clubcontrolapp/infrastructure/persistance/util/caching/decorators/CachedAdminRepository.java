package com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.decorators;

import com.vladi.clubcontrolapp.domain.entities.Admin;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.AdminRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.CachedJdbcRepository;
import java.util.Optional;
import java.util.UUID;

public class CachedAdminRepository
    extends CachedJdbcRepository<Admin, UUID>
    implements AdminRepository {

  private final AdminRepository adminDelegate;

  public CachedAdminRepository(AdminRepository delegate){
    super(delegate, Admin::getId);
    this.adminDelegate = delegate;
  }

  @Override
  public Optional<Admin> findByLogin(String login) {
    Optional<Admin> admin = adminDelegate.findByLogin(login);
    admin.ifPresent(entity -> identityMap.put(entity.getId(), entity));
    return admin;
  }
}
