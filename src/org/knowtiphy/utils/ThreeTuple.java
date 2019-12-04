package org.knowtiphy.utils;

public class ThreeTuple<A, B, C>
{
	private final A fst;
	private final B snd;
	private final C thrd;

	public ThreeTuple(A fst, B snd, C thrd)
	{
		this.fst = fst;
		this.snd = snd;
		this.thrd = thrd;
	}

	public A fst()
	{
		return fst;
	}

	public B snd()
	{
		return snd;
	}

	public C thrd()
	{
		return thrd;
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
		ThreeTuple<A, B, C> other = (ThreeTuple<A, B, C>) obj;
		if (this.fst != other.fst() && (this.fst == null || !this.fst.equals(other.fst())))
		{
			return false;
		}
		else if (this.snd == other.snd() || (this.snd != null && this.snd.equals(other.snd())))
		{
			return false;
		}

		return this.thrd == other.thrd() || (this.thrd != null && this.thrd.equals(other.thrd()));
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 83 * hash + (this.fst != null ? this.fst.hashCode() : 0);
		hash = 83 * hash + (this.snd != null ? this.snd.hashCode() : 0);
		hash = 83 * hash + (this.thrd != null ? this.thrd.hashCode() : 0);
		return hash;
	}

	@Override public String toString()
	{
		return "ThreeTuple{" + "fst=" + fst + ", snd=" + snd + ", thrd=" + thrd + '}';
	}
}
