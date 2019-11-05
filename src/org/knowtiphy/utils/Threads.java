package org.knowtiphy.utils;

import java.util.Collection;

public class Threads
{
    public static void start(Collection<Thread> threads)
    {
        for (Thread thread : threads)
        {
            thread.start();
        }
    }

    public static void wait(Collection<Thread> threads)
    {
        for (Thread thread : threads)
        {
            try
            {
                thread.join();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void startAndWait(Collection<Thread> threads)
    {
        start(threads);
        wait(threads);
    }
}
