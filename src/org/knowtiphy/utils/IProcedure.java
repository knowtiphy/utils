package org.knowtiphy.utils;

public interface IProcedure<E extends Exception>
{
	void call() throws E;

	static void doAndIgnore(IProcedure<?> procedure)
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
