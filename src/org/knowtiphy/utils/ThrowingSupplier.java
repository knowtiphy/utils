package org.knowtiphy.utils;

/**
 *
 * @author graham
 */
public interface ThrowingSupplier<T, E extends Exception>
{
    T get() throws E;
}
