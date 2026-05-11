package com.vladi.clubcontrolapp.application.impl;

import com.vladi.clubcontrolapp.application.contract.ClientService;
import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientServiceImpl implements ClientService {
  private final PersistanceSession session;

  public ClientServiceImpl(PersistanceSession session) {
    this.session = session;
  }

  @Override
  public Client create(Client entity) {
    session.addClient(entity);
    session.commit();
    return entity;
  }

  @Override
  public Client update(UUID id, Client entity) {
    session.updateClient(entity);
    session.commit();
    return entity;
  }

  @Override
  public void delete(UUID id) {
    session.getClient(id).ifPresent(client -> {
      session.removeClient(client);
      session.commit();
    });
  }

  @Override
  public Optional<Client> findById(UUID id) {
    return session.getClient(id);
  }

  @Override
  public Optional<Client> findByEmail(String email) {
    return session.getClientByEmail(email);
  }

  @Override
  public List<Client> findByName(String name) {
    return session.findClientsByName(name);
  }
}
