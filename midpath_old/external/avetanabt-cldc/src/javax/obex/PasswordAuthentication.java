/*
 *  PasswordAuthentication.java
 *
 *  $Revision: 1.2 $ $Date: 2005/02/02 13:33:02 $ 
 * 
 *  (c) Copyright 2001, 2002 Motorola, Inc.  ALL RIGHTS RESERVED.
 */
package javax.obex;

/**
 * This class holds user name and password combinations.
 *
 * @version 1.0 February 11, 2002
 */
public class PasswordAuthentication {

	private byte[] userName;
	private byte[] password;
	
    /**
     * Creates a new <code>PasswordAuthentication</code> with the user name
     * and password provided.
     *
     * @param userName the user name to include; this may be <code>null</code>
     *
     * @param password the password to include in the response
     *
     * @exception NullPointerException if <code>password</code> is
     * <code>null</code>
     */
    public PasswordAuthentication(byte[] userName, byte[] password) {
    		this.userName = userName;
    		this.password = password;
    }

    /**
     * Retrieves the user name that was specified in the constructor.
     * The user name may be <code>null</code>.
     *
     * @return the user name
     */
    public byte[] getUserName() {
    		return userName;
    }

    /**
     * Retrieves the password.
     *
     * @return the password
     */
    public byte[] getPassword() {
    		return password;
    }
}
