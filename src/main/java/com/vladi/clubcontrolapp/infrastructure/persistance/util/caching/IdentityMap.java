package com.vladi.clubcontrolapp.infrastructure.persistance.util.caching;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class IdentityMap<ID, T> {
  private final Map<ID, T> store = new HashMap<>();

  public Optional<T> get(ID id){
    return Optional.ofNullable(store.get(id));
  }

  public void put (ID id, T entity){
    store.put(id,entity);
  }

  public void remove(ID id){
    store.remove(id);
  }

  public boolean contains(ID id){
    return store.containsKey(id);
  }

  public void clear(){
    store.clear();
  }

  public int size(){
    return store.size();
  }
}
