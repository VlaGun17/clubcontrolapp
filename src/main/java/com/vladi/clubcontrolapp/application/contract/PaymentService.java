package com.vladi.clubcontrolapp.application.contract;

import com.vladi.clubcontrolapp.application.BaseService;
import com.vladi.clubcontrolapp.domain.entities.Payment;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService extends BaseService<Payment, UUID> {
  BigDecimal getDailyRevenue();
  Optional<Payment> getPaymentForSession(UUID sessionId);
}
