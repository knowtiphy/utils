package org.knowtiphy.utils;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

public class Wrap
{
	//	wrap a callable into a supplier that turns checked exceptions into runtime exceptions
	public static <T> Supplier<T> wrap(Callable<T> callable)
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

	public static <T, R> Function<T, R> wrap(Function<T, R> callable)
	{
		return (t) -> {
			try
			{
				return callable.apply(t);
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
	public static <T> Supplier<T> wrap(IProcedure procedure)
	{
		return wrap(() -> {
			procedure.call();
			return null;
		});
	}
}
