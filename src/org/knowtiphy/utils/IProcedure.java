package org.knowtiphy.utils;

public interface IProcedure<E extends Exception>
{
	void call() throws E;

	static <T extends Exception> void doAndIgnore(IProcedure<T> procedure)
	{
		try
		{
			procedure.call();
		} catch (Exception ex)
		{
			//	ignore
		}
	}
}
