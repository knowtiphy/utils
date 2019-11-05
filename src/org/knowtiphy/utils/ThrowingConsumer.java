package org.knowtiphy.utils;

/**
 *
 * @author graham
 */
public interface ThrowingConsumer<T, E extends Exception>
{
    void apply(T t) throws E;
}
