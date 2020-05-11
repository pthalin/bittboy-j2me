package com.lti.civil.test.utils.synchronization;

/**
 * Useful class for setting a flag between threads.  For simple uses, this class is 
 * not that useful, since boolean access is guaranteed to be atomic.
 * 
 * @author Ken Larson
 */ 
public class SynchronizedBoolean
{
	private boolean b = false;
	
	public SynchronizedBoolean()
	{	super();
	}
	public SynchronizedBoolean(boolean initValue)
	{
		this.b = initValue;
	}
	
	public synchronized boolean getValue()
	{
		return b;
	}
	public synchronized void setValue(boolean newValue)
	{
		if (b != newValue)
		{	b = newValue;
			notifyAll();
		}
		
	}
	public synchronized void waitUntil(boolean value) throws InterruptedException
	{	while (b != value)
		{
			wait();
		}
			
	}
	/**
	 * @return true if value matches value, false if timeout occurred
	 */
	public synchronized boolean waitUntil(boolean value, int timeout) throws InterruptedException
	{	long start = System.currentTimeMillis();
		while (b != value)
		{	long now = System.currentTimeMillis();
			long diff = now - start;
			long wait = timeout - diff;
			if (wait <= 0)
				return false;
			wait(wait);
		}
		return true;
	
	}	
	
	/**
	 * 
	 * If the value is oldValue, set to newValue and return true, otherwise return false.
	 */
	public synchronized boolean getAndSet(boolean oldValue, boolean newValue)
	{
		if (b != oldValue)
			return false;
		setValue(newValue);
		return true;
	}
	
}
