package org.knowtiphy.utils;

public interface IProcedure<E extends  Exception>
{
	void call() throws E;
}
