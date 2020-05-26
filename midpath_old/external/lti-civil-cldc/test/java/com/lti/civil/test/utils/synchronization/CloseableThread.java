package com.lti.civil.test.utils.synchronization;

/**
 * A base class for threads which need to be gracefully closed, since Thread.stop() is
 * deprecated.  Subclass should check isClosing() in their main loop in run(), and 
 * call setClosed() when run() completes.
 * 
 * @author Ken Larson
 */
public abstract class CloseableThread extends Thread
{
	protected final SynchronizedBoolean closing = new SynchronizedBoolean(false); 
	private final SynchronizedBoolean closed = new SynchronizedBoolean(false); 
	
	/** @deprecated */
	public CloseableThread()
	{
		super();
	}
	/** @deprecated */
	public CloseableThread(String threadName)
	{
		super(threadName);
	}
	public CloseableThread(final ThreadGroup group, final String threadName)
	{
		super(group, threadName);
	}
	public void close()	
	{	closing.setValue(true);
		interrupt();
	}
	protected void setClosing()
	{
		closing.setValue(true);
	}

	public boolean isClosed()
	{	return closed.getValue();
	}
	public void waitUntilClosed() throws InterruptedException
	{	closed.waitUntil(true);
	}
	
	/**
	 * intended to be checked by thread in its main loop.  break out of the main loop
	 * if true.
	 */
	protected boolean isClosing()
	{	return closing.getValue();
	}
	
	/**
	 * to be called by the thread upon exit.
	 */
	protected void setClosed()
	{	closed.setValue(true);
	}
}
