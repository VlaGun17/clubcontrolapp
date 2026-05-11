package com.vladi.clubcontrolapp.application.impl;

import com.vladi.clubcontrolapp.application.contract.PaymentService;
import com.vladi.clubcontrolapp.domain.entities.Payment;
import com.vladi.clubcontrolapp.infrastructure.session.PersistanceSession;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class PaymentServiceImpl implements PaymentService {
  private final PersistanceSession session;

  public PaymentServiceImpl(PersistanceSession session) {
    this.session = session;
  }

  @Override
  public BigDecimal getDailyRevenue() {
    return session.getDailyRevenue();
  }

  @Override
  public Optional<Payment> getPaymentForSession(UUID sessionId) {
    return session.getPaymentBySession(sessionId);
  }

  @Override
  public Payment create(Payment entity) {
    session.addPayments(entity);
    session.commit();
    return entity;
  }

  @Override
  public Payment update(UUID id, Payment entity) {
    session.updatePayment(entity);
    session.commit();
    return entity;
  }

  @Override
  public void delete(UUID id) {
    session.getPayment(id).ifPresent(payment -> {
      session.removePayment(payment);
      session.commit();
    });
  }

  @Override
  public Optional<Payment> findById(UUID id) {
    return session.getPayment(id);
  }
}
