package main.dao;

import java.util.List;

public interface Dao<ENTITY, ID> {
    List<ENTITY> findAll();

    ENTITY findById(ID id);

    ENTITY insert(ENTITY entity);

    ENTITY update(ENTITY entity);

    boolean delete(ID id);

}
