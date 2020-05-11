package com.sun.midp.main;

import java.io.InputStream;

public interface MIDletClassLoader {
	
	/**
     * Get the class of a MIDlet.
     *
     * @param className classname of a MIDlet in the suite
     *
     * @return name the class for the given the className
     * @throws ClassNotFoundException 
     * @throws InstantiationException 
     */
    public Class getMIDletClass(String className) throws ClassNotFoundException, InstantiationException;
    
    public InputStream getResourceAsStream(String name);

}
