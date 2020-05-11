/*
 * Created on May 4, 2005
 */
package com.lti.civil.test.utils.synchronization;

import com.lti.civil.test.utils.ObjUtils;

/**
 * Manages a singleton thread group.  This is useful because we often want to assign
 * all threads to a single thread group to allow centralized uncaught exception handling.
 * @author Ken Larson
 */
public final class ThreadGroupMgr
{
	private static ThreadGroup mainThreadGroup;
	private static ThreadGroup defaultThreadGroup;
	
	private ThreadGroupMgr()
	{	super();
	}
	
	/** Returns the current thread group if it differs from the main thread group, otherwise it returns the default thread group. */
	public static synchronized ThreadGroup getThreadGroup()
	{
		ThreadGroup currentThreadGroup = Thread.currentThread().getThreadGroup();
		if (mainThreadGroup != null && defaultThreadGroup != null && ObjUtils.equal(currentThreadGroup, mainThreadGroup))
			return getDefaultThreadGroup();
		else
			return currentThreadGroup;
	}
	
	public static synchronized ThreadGroup getMainThreadGroup()
	{
		return mainThreadGroup;
	}
	public static synchronized void setMainThreadGroup(ThreadGroup mainThreadGroup)
	{
		ThreadGroupMgr.mainThreadGroup = mainThreadGroup;
	}
	public static synchronized ThreadGroup getDefaultThreadGroup()
	{
		return defaultThreadGroup;
	}
	public static synchronized void setDefaultThreadGroup(ThreadGroup defaultThreadGroup)
	{
		ThreadGroupMgr.defaultThreadGroup = defaultThreadGroup;
	}
}
