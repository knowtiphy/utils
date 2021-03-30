package org.knowtiphy.utils;

import java.util.Objects;

public class Triple<A, B, C> extends Pair<A, B>
{
	private final C third;

	public Triple(A first, B second, C third)
	{
		super(first, second);
		this.third = third;
	}

	public C getThird()
	{
		return third;
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
		return getFirst().equals(triple.getFirst()) && getSecond().equals(triple.getSecond())
				&& getThird().equals(triple.getThird());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getFirst(), getSecond(), getThird());
	}

	@Override
	public String toString()
	{
		return "(" + getFirst() + ", " + getSecond() + ", " + getThird() + ")";
	}
}
