package com.vladi.clubcontrolapp.infrastructure.persistance.contract;

import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.infrastructure.persistance.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends Repository<Client, UUID> {
  Optional<Client> findByEmail(String email);

  List<Client> findByNameContaining(String name);

  List<Client> findByRegistrationDate(LocalDate registrationDate);

  long count();
}
