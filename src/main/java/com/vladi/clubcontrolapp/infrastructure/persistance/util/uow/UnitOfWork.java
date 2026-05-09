package com.vladi.clubcontrolapp.infrastructure.persistance.util.uow;

public interface UnitOfWork {
  void registerNew(Object entity);
  void registerDirty(Object entity);
  void registerDeleted(Object entity);
  void registerClean(Object entity);
  void commit();
  void rollback();
}
