package com.vladi.clubcontrolapp.application.contract;

import com.vladi.clubcontrolapp.application.BaseService;
import com.vladi.clubcontrolapp.domain.entities.Admin;
import java.util.Optional;
import java.util.UUID;

public interface AdminService extends BaseService<Admin, UUID> {
  Optional<Admin> getAdminByLogin(String login);
}
