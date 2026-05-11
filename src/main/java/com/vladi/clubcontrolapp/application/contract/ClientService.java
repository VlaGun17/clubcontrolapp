package com.vladi.clubcontrolapp.application.contract;

import com.vladi.clubcontrolapp.application.BaseService;
import com.vladi.clubcontrolapp.domain.entities.Client;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientService extends BaseService<Client, UUID> {
  Optional<Client> findByEmail(String email);
  List<Client> findByName(String name);
}
