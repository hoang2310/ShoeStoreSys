package pro1041.dao;

import java.util.List;

public interface DAO<E, K> {

    public void insert(E entity);

    public void update(E entity);

    public void delete(K id);

    public E selectByID(K id);

    public List<E> selectAll();

    public List<E> selectBySQL(String sql, Object... args);

}
