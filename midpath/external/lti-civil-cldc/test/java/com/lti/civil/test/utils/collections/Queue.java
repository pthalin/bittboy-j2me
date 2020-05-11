package com.lti.civil.test.utils.collections;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a FIFO.
 * 
 * @author Ken Larson
 */
public class Queue/*<T>*/
{
	private List/*<T>*/ v = new ArrayList/*<T>*/();

	public int size()
	{	return v.size();
	}

	public Object /*T*/ dequeue()
	{
//		if (v.size() == 0)
//			throw new ArrayIndexOutOfBoundsException("Queue empty");
		final Object /*T*/ o = v.get(0);
		v.remove(0);
		return o;
	}
	public Object /*T*/ peek()
	{
//		if (v.size() == 0)
//			throw new ArrayIndexOutOfBoundsException("Queue empty");
		return v.get(0);
	}
	public void enqueue(Object /*T*/ o)
	{	v.add(o);
	}
	public void removeAllElements()
	{	v.clear();
	}
	public boolean isEmpty()
	{	return v.size() == 0;
	}
}
