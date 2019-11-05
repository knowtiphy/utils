package org.knowtiphy.utils;

/*
 *
 * @author graham
 */
import java.util.HashSet;
import java.util.Set;

/**
 * A source of unique names -- each call to get() returns a unique new variable.
 *
 * Note: This code is not thread safe.
 *
 * @author graham
 */
public class NameSource
{
    private final String base;
    private final Set<String> avoid;
    private long count;

    public NameSource(String base, long count, Set<String> avoid)
    {
        this.base = base;
        this.count = count;
        this.avoid = avoid;
    }

    public NameSource(String base, long count)
    {
        this(base, count, new HashSet<>());
    }

    public NameSource(String base, Set<String> avoid)
    {
        this(base, 0, avoid);
    }

    public NameSource(String base)
    {
        this(base, 0);
    }

    public String get()
    {
        String choice;
        do
        {
            count++;
            choice = base + count;
        }
        while (avoid.contains(choice));

        return choice;
    }
}
