package org.knowtiphy.utils;

//	higher priorty numbers = higher priority so done first
//	no ordering within priorities

import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PriorityExecutor extends ThreadPoolExecutor
{
	private static final int INITIAL_QUEUE_CAPACITY = 10;

	public PriorityExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new PriorityBlockingQueue<>(INITIAL_QUEUE_CAPACITY, new PriorityTaskComparator()));
	}

	public PriorityExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new PriorityBlockingQueue<>(INITIAL_QUEUE_CAPACITY, new PriorityTaskComparator()), threadFactory);
	}

	public PriorityExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, RejectedExecutionHandler handler)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new PriorityBlockingQueue<>(INITIAL_QUEUE_CAPACITY, new PriorityTaskComparator()), handler);
	}

	public PriorityExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, RejectedExecutionHandler handler)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new PriorityBlockingQueue<>(INITIAL_QUEUE_CAPACITY, new PriorityTaskComparator()), threadFactory, handler);
	}

	public PriorityExecutor()
	{
		this(1, 1, Integer.MAX_VALUE, TimeUnit.SECONDS);
	}

	public PriorityExecutor(final ThreadFactory threadFactory)
	{
		this(1, 1, Integer.MAX_VALUE, TimeUnit.SECONDS, threadFactory);
	}

	public PriorityExecutor(final RejectedExecutionHandler handler)
	{
		this(1, 1, Integer.MAX_VALUE, TimeUnit.SECONDS, handler);
	}

	public PriorityExecutor(final ThreadFactory threadFactory, final RejectedExecutionHandler handler)
	{
		this(1, 1, Integer.MAX_VALUE, TimeUnit.SECONDS, threadFactory, handler);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable)
	{
		if (callable instanceof HasPriority)
		{
			return new PriorityTask<>(((HasPriority) callable).getPriority(), callable);
		}
		else
		{
			return new PriorityTask<>(0, callable);
		}
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T value)
	{
		if (runnable instanceof HasPriority)
		{
			return new PriorityTask<>(((HasPriority) runnable).getPriority(), runnable, value);
		}
		else
		{
			return new PriorityTask<>(0, runnable, value);
		}
	}

	private static final class PriorityTask<T> extends FutureTask<T> implements Comparable<PriorityTask<T>>
	{
		private final int priority;

		public PriorityTask(int priority, final Callable<T> callable)
		{
			super(callable);
			this.priority = priority;
		}

		public PriorityTask(final int priority, final Runnable runnable, final T result)
		{
			super(runnable, result);
			this.priority = priority;
		}

		@Override
		public int compareTo(final PriorityTask<T> o)
		{
			return Long.signum(o.priority - priority);
		}
	}

	private static class PriorityTaskComparator implements Comparator<Runnable>
	{
		@Override
		public int compare(final Runnable left, final Runnable right)
		{
			//noinspection unchecked,rawtypes
			return ((PriorityTask) left).compareTo((PriorityTask) right);
		}
	}
}