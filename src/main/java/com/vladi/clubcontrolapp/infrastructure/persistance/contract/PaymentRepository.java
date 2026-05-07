package com.vladi.clubcontrolapp.infrastructure.persistance.contract;

import com.vladi.clubcontrolapp.domain.entities.Admin;
import com.vladi.clubcontrolapp.domain.entities.Payment;
import com.vladi.clubcontrolapp.domain.enums.MethodPayment;
import com.vladi.clubcontrolapp.infrastructure.persistance.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends Repository<Payment, UUID> {
  Optional<Payment> findBySessionId(UUID sessionId);

  BigDecimal getTotalRevenue(LocalDate date);

  List<Payment> findByMethod(MethodPayment method);
}
