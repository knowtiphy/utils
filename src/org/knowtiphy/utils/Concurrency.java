/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.knowtiphy.utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author graham
 */
public class Concurrency
{
    public static void fence()
    {
        Lock lock = new ReentrantLock();
        lock.lock();
        lock.unlock();
    }

    public static <T> Collection<T> wait(Collection<Future<T>> futures) throws ExecutionException, InterruptedException
    {
        var results = new LinkedList<T>();
        for (var t : futures)
        {
            results.add(t.get());
        }
        return results;
    }
}
