/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author singh
 */

//A generic blueprint for all Data Handlers in MotorPH.
//@param <T> The type of object (e.g., Employee)

public abstract class BaseRepository<T> implements IRepository<T> {
    
    protected abstract String getFilePath();
    
    public abstract List<T> findAll();
    
    public abstract Optional<T> findById(int id);
    
    public abstract void save(T item);
    
    public abstract void update(T item);
    
    public abstract void delete(int id);
    
}
