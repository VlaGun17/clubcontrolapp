package com.vladi.clubcontrolapp.infrastructure.persistance.contract;

import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.infrastructure.persistance.Repository;
import java.util.UUID;

public interface ClientRepository extends Repository<Client, UUID> {
  
}
