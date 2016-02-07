package ru.shadam.ferry.api;

import ru.shadam.ferry.annotations.PathVariable;
import ru.shadam.ferry.annotations.RequestMethod;
import ru.shadam.ferry.annotations.Url;

import java.util.List;

/**
 * @author sala
 */
public interface CrudRestApi<T, ID> {
    @RequestMethod("GET")
    @Url("/")
    List<T> getAll();

    @RequestMethod("GET")
    @Url("/:id")
    T getById(@PathVariable("id") ID id);

    @RequestMethod("POST")
    @Url("/:id")
    T update(@PathVariable("id") ID id, T value);

    @RequestMethod("PUT")
    @Url("/")
    T create(T value);

    @RequestMethod("DELETE")
    @Url("/:id")
    void deleteById(@PathVariable("id") ID id);
}
