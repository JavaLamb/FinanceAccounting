package main.dao;

import java.util.List;

public interface Dao<DOMAIN, ID> {
    List<DOMAIN> findByAll();

    DOMAIN findById(ID id);

    DOMAIN insert(DOMAIN domain);

    DOMAIN update(DOMAIN domain);

    boolean delete(ID id);

}
