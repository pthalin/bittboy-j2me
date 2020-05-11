package com.lti.civil.test.utils;



/**
 * Utilities for performing equality tests, and doing deep copies and deep compares of objects.
 *
 * @author Ken Larson
 */
public final class ObjUtils
{
	
	private ObjUtils()
	{	super();
	}


	public static boolean equal(Object o1, Object o2)
	{
		if (o1 == null && o2 == null)
			return true;
		
		if (o1 == null || o2 == null)
			return false;
		
		//if (o1.getClass() != o2.getClass())
		//	return false;
		
		return o1.equals(o2);
		
	}

	
}
