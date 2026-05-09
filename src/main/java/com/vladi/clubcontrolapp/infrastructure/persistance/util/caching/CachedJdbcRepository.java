package com.vladi.clubcontrolapp.infrastructure.persistance.util.caching;

import com.vladi.clubcontrolapp.infrastructure.persistance.Repository;
import java.util.List;
import java.util.Optional;

public class CachedJdbcRepository<T, ID> implements Repository<T, ID> {
  protected final Repository<T, ID> delegate;
  protected final IdentityMap<ID, T> identityMap;
  protected final java.util.function.Function<T, ID> idExtractor;

  public CachedJdbcRepository(Repository<T, ID> delegate, java.util.function.Function<T, ID> idExtractor){
    this.delegate = delegate;
    this.idExtractor = idExtractor;
    this.identityMap = new IdentityMap<>();
  }


  @Override
  public void save(T entity) {
    delegate.save(entity);
    identityMap.put(idExtractor.apply(entity), entity);
  }

  @Override
  public Optional<T> findById(ID id) {
    Optional<T> cached = identityMap.get(id);
      if(cached.isPresent()){
        return cached;
      }
      Optional<T> fromDb = delegate.findById(id);
      fromDb.ifPresent(entity -> identityMap.put(id, entity));
      return fromDb;
  }

  @Override
  public List<T> findAll() {
    List<T> all = delegate.findAll();
    all.forEach(entity -> identityMap.put(idExtractor.apply(entity), entity));
    return all;
  }

  @Override
  public boolean deleteById(ID id) {
    boolean deleted = delegate.deleteById(id);
    if(deleted){
      identityMap.remove(id);
    }
    return deleted;
  }

  @Override
  public void update(T entity) {
    delegate.update(entity);
    identityMap.put(idExtractor.apply(entity), entity);
  }
}
