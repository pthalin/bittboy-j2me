package com.lti.civil.test.utils.synchronization;

/**
 * A useful base class for a thread that wishes to simply respond to messages.
 * 
 * @author Ken Larson
 */
public class MessageDrivenThread extends CloseableThread
{
	
	private MessageDrivenThreadListener listener;
	
	public MessageDrivenThread(final ThreadGroup group, final String threadName, MessageDrivenThreadListener listener)
	{	super(group, threadName);
		this.listener = listener;
	}
	public MessageDrivenThread(final ThreadGroup group, final String threadName)
	{	super(group, threadName);
	}
	public void setListener(MessageDrivenThreadListener listener)
	{	this.listener = listener;
	}
	
	private ProducerConsumerQueue q = new ProducerConsumerQueue(); 
	public void post(Object msg) throws InterruptedException
	{
		q.put(msg);
	}
	public void run()
	{
		try
		{
			while (!isClosing())
			{
				Object o = q.get();
				
				doMessageReceived(o);
			}
		}
		catch (InterruptedException e)
		{
		}
		setClosed();
	}
	
	/**
	 * subclass should override to do message processing.
	 */
	protected void doMessageReceived(Object o)
	{
		if (listener != null)
			listener.onMessage(this, o);
	}
}
