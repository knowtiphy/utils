package org.knowtiphy.utils;

/**
 * A pair of objects of type (A, B)
 *
 * @author graham
 * @param <A> the type of the first component
 * @param <B> the type of the second component
 */
public class Pair<A, B>
{
    private final A first;
    private final B second;

    public Pair(A first, B second)
    {
        this.first = first;
        this.second = second;
    }

    public A getFirst()
    {
        return first;
    }

    public B getSecond()
    {
        return second;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
		@SuppressWarnings("unchecked")
		Pair<A, B> other = (Pair<A, B>) obj;
        if (this.first != other.getFirst() && (this.first == null || !this.first.equals(other.getFirst())))
        {
            return false;
        }
        return this.second == other.getSecond() || (this.second != null && this.second.equals(other.getSecond()));
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 83 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 83 * hash + (this.second != null ? this.second.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString()
    {
		return "(" + first + ", " + second + ")";
    }
}