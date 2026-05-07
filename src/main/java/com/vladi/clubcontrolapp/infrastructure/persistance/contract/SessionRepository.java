package com.vladi.clubcontrolapp.infrastructure.persistance.contract;

import com.vladi.clubcontrolapp.domain.entities.Session;
import com.vladi.clubcontrolapp.infrastructure.persistance.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SessionRepository extends Repository<Session, UUID> {
  List<Session> findAllActive();
  List<Session> findByComputerId(UUID computerId);
  List<Session> findByDate(LocalDateTime date);
}
