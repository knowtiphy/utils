package org.knowtiphy.utils;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

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

	//	wrap a callable into a supplier that turns checked exceptions into runtime exceptions
	static <T> Supplier<T> wrap(Callable<T> callable)
	{
		return () -> {
			try
			{
				return callable.call();
			}
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		};
	}

	//	wrap a procedure into a callable which returns null
	static <T> Supplier<T> wrap(IProcedure procedure)
	{
		return wrap(() -> {
			procedure.call();
			return null;
		});
	}
}
