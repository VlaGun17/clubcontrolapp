package com.vladi.clubcontrolapp.infrastructure.persistance.util.uow;

import com.vladi.clubcontrolapp.domain.entities.Admin;
import com.vladi.clubcontrolapp.domain.entities.Client;
import com.vladi.clubcontrolapp.domain.entities.Computer;
import com.vladi.clubcontrolapp.domain.entities.Payment;
import com.vladi.clubcontrolapp.domain.entities.Service;
import com.vladi.clubcontrolapp.domain.entities.Session;
import com.vladi.clubcontrolapp.domain.entities.SessionService;
import com.vladi.clubcontrolapp.domain.entities.Tariff;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.AdminRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.ClientRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.ComputerRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.PaymentRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.ServiceRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.SessionRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.SessionServiceRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.contract.TariffRepository;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.ConnectionManager;
import com.vladi.clubcontrolapp.infrastructure.persistance.util.exception.DatabaseException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.SequencedSet;

public class JdbcUnitOfWork implements UnitOfWork{

  private final ConnectionManager connectionManager;

  private final ClientRepository clientRepository;
  private final AdminRepository adminRepository;
  private final ComputerRepository computerRepository;
  private final PaymentRepository paymentRepository;
  private final ServiceRepository serviceRepository;
  private final SessionRepository sessionRepository;
  private final TariffRepository tariffRepository;
  private final SessionServiceRepository sessionServiceRepository;

  private final SequencedSet<Object> newEntities = new LinkedHashSet<>();
  private final SequencedSet<Object> dirtyEntities = new LinkedHashSet<>();
  private final SequencedSet<Object> deletedEntities = new LinkedHashSet<>();

  public JdbcUnitOfWork(ConnectionManager connectionManager,
      ClientRepository clientRepository,
      AdminRepository adminRepository,
      ComputerRepository computerRepository,
      PaymentRepository paymentRepository,
      ServiceRepository serviceRepository,
      SessionRepository sessionRepository,
      TariffRepository tariffRepository,
      SessionServiceRepository sessionServiceRepository){
    this.connectionManager = connectionManager;
    this.clientRepository = clientRepository;
    this.adminRepository = adminRepository;
    this.computerRepository = computerRepository;
    this.paymentRepository = paymentRepository;
    this.serviceRepository = serviceRepository;
    this.sessionRepository = sessionRepository;
    this.tariffRepository = tariffRepository;
    this.sessionServiceRepository = sessionServiceRepository;
  }

  @Override
  public void registerNew(Object entity) {
    if(deletedEntities.contains(entity)){
      throw new IllegalStateException(
          "Неможливо зареєструвати як новий об'єкт, що вже позначений для видалення: "
              + entity);
    }
    if(dirtyEntities.contains(entity)){
      return;
    }
    newEntities.add(entity);
  }

  @Override
  public void registerDirty(Object entity) {
    if(newEntities.contains(entity)){
      return;
    }
    if(deletedEntities.contains(entity)){
      throw new IllegalStateException(
          "Неможливо оновити об'єкт, що вже позначений для видалення: " + entity);
    }
    dirtyEntities.add(entity);
  }

  @Override
  public void registerDeleted(Object entity) {
    if(newEntities.remove(entity)){
      return;
    }
    dirtyEntities.remove(entity);
    deletedEntities.add(entity);
  }

  @Override
  public void registerClean(Object entity) {

  }

  @Override
  public void commit() {
    Connection conn = null;
    try{
      conn = connectionManager.getConnection();
      conn.setAutoCommit(false);

      insertAll();
      updateAll();
      deleteAll();

      conn.commit();
      clearAll();
    } catch (SQLException e) {
      rollbackQuietly(conn);
      throw new DatabaseException("UnitOfWork.commit() failed: " + e.getMessage(), e);
    } catch (Exception e) {
      rollbackQuietly(conn);
      throw e;
    } finally {
      closeQuietly(conn);
    }
  }

  @Override
  public void rollback() {
    clearAll();
  }

  private void insertAll(){
    for(Object entity : newEntities){
      if(entity instanceof Client c) clientRepository.save(c);
    }
    for(Object entity : newEntities){
      if(entity instanceof Admin a) adminRepository.save(a);
    }
    for(Object entity : newEntities){
      if(entity instanceof Computer c) computerRepository.save(c);
    }
    for(Object entity : newEntities){
      if(entity instanceof Tariff t) tariffRepository.save(t);
    }
    for(Object entity : newEntities){
      if(entity instanceof Service s) serviceRepository.save(s);
    }
    for(Object entity : newEntities){
      if(entity instanceof Session s) sessionRepository.save(s);
    }
    for(Object entity : newEntities){
      if(entity instanceof Payment p) paymentRepository.save(p);
    }
    for(Object entity : newEntities){
      if(entity instanceof SessionService ss) sessionServiceRepository.save(ss);
    }
  }

  private void deleteAll(){
    for(Object entity : deletedEntities){
      if(entity instanceof Client c) clientRepository.deleteById(c.getId());
    }
    for(Object entity : deletedEntities){
      if(entity instanceof Admin a) adminRepository.deleteById(a.getId());
    }
    for(Object entity : deletedEntities){
      if(entity instanceof Computer c) computerRepository.deleteById(c.getId());
    }
    for(Object entity : deletedEntities){
      if(entity instanceof Tariff t) tariffRepository.deleteById(t.getId());
    }
    for(Object entity : deletedEntities){
      if(entity instanceof Service s) serviceRepository.deleteById(s.getId());
    }
    for(Object entity : deletedEntities){
      if(entity instanceof Session s) sessionRepository.deleteById(s.getId());
    }
    for(Object entity : deletedEntities){
      if(entity instanceof Payment p) paymentRepository.deleteById(p.getId());
    }
  }

  private void updateAll(){
    for(Object entity : dirtyEntities){
      if(entity instanceof Client c) clientRepository.update(c);
      else if(entity instanceof Admin a) adminRepository.update(a);
      else if(entity instanceof Computer c) computerRepository.update(c);
      else if(entity instanceof Tariff t) tariffRepository.update(t);
      else if(entity instanceof Service s) serviceRepository.update(s);
      else if(entity instanceof Session s) sessionRepository.update(s);
      else if(entity instanceof Payment p) paymentRepository.update(p );
    }
  }

  private void clearAll(){
    newEntities.clear();
    dirtyEntities.clear();
    deletedEntities.clear();
  }

  private void rollbackQuietly(Connection conn){
    if(conn != null){
      try {
        conn.rollback();
      } catch (SQLException ignored){

      }
    }
  }

  private void closeQuietly(Connection conn) {
    if (conn != null) {
      try {
        conn.setAutoCommit(true);
        conn.close();
      } catch (SQLException ignored) {}
    }
  }

  public int newCount()     { return newEntities.size(); }
  public int dirtyCount()   { return dirtyEntities.size(); }
  public int deletedCount() { return deletedEntities.size(); }
}
