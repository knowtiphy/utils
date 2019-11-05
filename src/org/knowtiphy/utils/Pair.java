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
    private final A fst;
    private final B snd;

    public Pair(A fst, B snd)
    {
        this.fst = fst;
        this.snd = snd;
    }

    public A fst()
    {
        return fst;
    }

    public B snd()
    {
        return snd;
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
        if (this.fst != other.fst() && (this.fst == null || !this.fst.equals(other.fst())))
        {
            return false;
        }
        return this.snd == other.snd() || (this.snd != null && this.snd.equals(other.snd()));
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 83 * hash + (this.fst != null ? this.fst.hashCode() : 0);
        hash = 83 * hash + (this.snd != null ? this.snd.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString()
    {
		return "(" + fst + ", " + snd + ")";
    }
}