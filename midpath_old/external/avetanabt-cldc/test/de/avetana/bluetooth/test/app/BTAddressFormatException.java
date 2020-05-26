package de.avetana.bluetooth.test.app;



/*
   Copyright (c) 2002 The Appliance Studio Limited.
   Written by Edward Kay <ed.kay@appliancestudio.com>
   http://www.appliancestudio.com

   This program is free software; you can redistribute it and/or modify it under
   the terms of the GNU General Public License version 2 as published by the
   Free Software Foundation.

   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS.

   IN NO EVENT SHALL THE COPYRIGHT HOLDER(S) AND AUTHOR(S) BE LIABLE FOR ANY
   CLAIM, OR ANY SPECIAL INDIRECT OR CONSEQUENTIAL DAMAGES, OR ANY DAMAGES
   WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION
   OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
   CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

   ALL LIABILITY, INCLUDING LIABILITY FOR INFRINGEMENT OF ANY PATENTS,
   COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS, RELATING TO USE OF THIS SOFTWARE IS
   DISCLAIMED.

   $Id: BTAddressFormatException.java,v 1.7 2005/12/21 20:37:49 moritzg Exp $
*/

/**
 * Exception thrown when problems occur when parsing a String to a BTAddress object.
 *
 * @author Edward Kay, ed.kay@appliancestudio.com
 * @see BTAddress
 * @version 1.0
 */
public class BTAddressFormatException extends Exception
{
        /**
         * Constructs a BTAddressException with no detail message.
         */
        public BTAddressFormatException () {}

        /**
         * Constructs a BTAddressException including a detail message.
         *
         * @param s Message detailing why the exception occurred.
         */
        public BTAddressFormatException(String s)
        {
                super(s);
        }
}
