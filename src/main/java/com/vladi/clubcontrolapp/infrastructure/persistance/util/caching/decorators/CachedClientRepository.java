package com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.decorators;

import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.infrastructure.persistance.Repository;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.ClientRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.CachedJdbcRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class CachedClientRepository
    extends CachedJdbcRepository<Client, UUID>
    implements ClientRepository {

  private final ClientRepository clientDelegate;

  public CachedClientRepository(ClientRepository delegate) {
    super(delegate, Client::getId);
    this.clientDelegate = delegate;
  }

  @Override
  public Optional<Client> findByEmail(String email) {
    Optional<Client> fromDb = clientDelegate.findByEmail(email);
    return fromDb.map(client -> {
      Optional<Client> cached = identityMap.get(client.getId());
      if(cached.isPresent()) return cached.get();
      identityMap.put(client.getId(), client);
      return client;
    });
  }

  @Override
  public Optional<Client> findByLogin(String login) {
    Optional<Client> fromDb = clientDelegate.findByLogin(login);
    return fromDb.map(client -> {
      Optional<Client> cached = identityMap.get(client.getId());
      if(cached.isPresent()) return cached.get();
      identityMap.put(client.getId(), client);
      return client;
    });
  }

  @Override
  public List<Client> findByNameContaining(String name) {
    List<Client> clients = clientDelegate.findByNameContaining(name);
    clients.forEach(client -> identityMap.put(client.getId(), client));
    return clients;
  }

  @Override
  public List<Client> findByRegistrationDate(LocalDate registrationDate) {
    List<Client> clients = clientDelegate.findByRegistrationDate(registrationDate);
    clients.forEach(client -> identityMap.put(client.getId(), client));
    return clients;
  }

  @Override
  public long count() {
    long count = clientDelegate.count();
    return count;
  }
}
