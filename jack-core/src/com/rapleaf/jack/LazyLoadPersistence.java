package com.rapleaf.jack;

import java.io.Serializable;

public abstract class LazyLoadPersistence<T extends IModelPersistence, D extends GenericDatabases> implements Serializable {

  private final BaseDatabaseConnection conn;
  private final D databases;

  private volatile T persistence;

  private volatile boolean disableCaching;

  public LazyLoadPersistence(BaseDatabaseConnection conn, D databases) {
    this.conn = conn;
    this.databases = databases;

    this.persistence = null;
    this.disableCaching = false;
  }

  public T get() {
    if (persistence == null) {
      synchronized (this) {
        if (persistence == null) {
          this.persistence = build(conn, databases);

          if (disableCaching) {
            persistence.disableCaching();
          }
        }
      }
    }

    return persistence;
  }

  public void disableCaching() {
    disableCaching = true;

    if (persistence != null) {
      persistence.disableCaching();
    }
  }

  protected abstract T build(BaseDatabaseConnection conn, D databases);
}
