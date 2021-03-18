package org.knowtiphy.utils;

import java.util.Objects;

public class Triple<A, B, C>
{
	private final A fst;
	private final B snd;
	private final C thd;

	public Triple(A fst, B snd, C thd)
	{
		this.fst = fst;
		this.snd = snd;
		this.thd = thd;
	}

	public A fst()
	{
		return fst;
	}

	public B snd()
	{
		return snd;
	}

	public C thd()
	{
		return thd;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
		return fst.equals(triple.fst) && snd.equals(triple.snd) && thd.equals(triple.thd);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(fst, snd, thd);
	}

	@Override
	public String toString()
	{
		return "(" + fst + ", " + snd + ", " + thd + ")";
	}
}
