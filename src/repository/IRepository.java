
/**
 *
 * @author singh
 */
package repository;

import java.util.List;
import java.util.Optional;

public interface IRepository<T> {
    List<T> findAll();
    void save(T record);
    void update(T record);
    void delete(int id);
}