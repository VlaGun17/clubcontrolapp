package com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.decorators;

import com.vladi.clubcontrolapp.domain.entities.Payment;
import com.vladi.clubcontrolapp.domain.enums.MethodPayment;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.PaymentRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.caching.CachedJdbcRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CachedPaymentRepository
    extends CachedJdbcRepository<Payment, UUID>
    implements PaymentRepository {

  private final PaymentRepository paymentDelegate;

  public CachedPaymentRepository(PaymentRepository delegate){
    super(delegate, Payment::getId);
    this.paymentDelegate = delegate;
  }

  @Override
  public Optional<Payment> findBySessionId(UUID sessionId) {
    Optional<Payment> fromDb = paymentDelegate.findBySessionId(sessionId);
    return fromDb.map(payment -> {
      Optional<Payment> cached = identityMap.get(payment.getId());
      if(cached.isPresent()) return cached.get();
      identityMap.put(payment.getId(), payment);
      return payment;
    });
  }

  @Override
  public BigDecimal getTotalRevenue(LocalDate date) {
    BigDecimal totalRevenue = paymentDelegate.getTotalRevenue(date);
    return totalRevenue;
  }

  @Override
  public List<Payment> findByMethod(MethodPayment method) {
    List<Payment> payments = paymentDelegate.findByMethod(method);
    payments.forEach(entity -> identityMap.put(entity.getId(), entity));
    return payments;
  }
}
