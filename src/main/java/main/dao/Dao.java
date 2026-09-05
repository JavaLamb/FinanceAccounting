package main.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<ENTITY, ID> {
    List<ENTITY> findAll();

    Optional<ENTITY> findById(ID id);

    ENTITY insert(ENTITY entity);

    ENTITY update(ENTITY entity);

    boolean delete(ID id);

}
