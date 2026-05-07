package com.vladi.clubcontrolapp.infrastructure.persistance.contract;

import com.vladi.clubcontrolapp.domain.entities.Admin;
import com.vladi.clubcontrolapp.infrastructure.persistance.Repository;
import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends Repository<Admin, UUID> {
  Optional<Admin> findByLogin(String login);

}
