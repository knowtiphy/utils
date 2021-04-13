package org.knowtiphy.utils;

@FunctionalInterface
public interface IProcedure
{
	void call() throws Exception;

	static void doAndIgnore(IProcedure procedure)
	{
		try
		{
			procedure.call();
		}
		catch (Exception ex)
		{
			//	ignore
		}
	}

}
