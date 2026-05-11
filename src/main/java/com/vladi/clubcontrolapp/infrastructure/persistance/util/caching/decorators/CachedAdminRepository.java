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
    Optional<Admin> fromDb = adminDelegate.findByLogin(login);
    return fromDb.map(admin -> {
      Optional<Admin> cached = identityMap.get(admin.getId());
      if(cached.isPresent()){
        return cached.get();
      }
      identityMap.put(admin.getId(), admin);
      return admin;
    });
  }
}
