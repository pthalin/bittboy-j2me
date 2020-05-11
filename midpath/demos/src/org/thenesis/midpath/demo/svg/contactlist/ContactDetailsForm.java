/*
 *
 * Copyright (c) 2007, Sun Microsystems, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.thenesis.midpath.demo.svg.contactlist;

import org.thenesis.midpath.demo.svg.SVGTextBinding;
import org.w3c.dom.Document;

/**
 * The <code>ContactDetailsForm</code> class simplifies binding a contact's 
 * information (name, phone number, etc...) to the various 
 * SVG elements in the current skin.
 */
public class ContactDetailsForm {
    /**
     * Identifier conventions used for contact details information.
     */
    public static final String CONTACT_DETAILS_PREFIX = "contactDetails.";
    public static final String CONTACT_DETAILS_NAME = "selectedItem." + "text";
    public static final String CONTACT_DETAILS_EMAIL = CONTACT_DETAILS_PREFIX + "email";
    public static final String CONTACT_DETAILS_CELL = CONTACT_DETAILS_PREFIX + "cell";
    public static final String CONTACT_DETAILS_WORK = CONTACT_DETAILS_PREFIX + "work";
    public static final String CONTACT_DETAILS_HOME = CONTACT_DETAILS_PREFIX + "home";
    public static final String CONTACT_DETAILS_ADDRESS1 = CONTACT_DETAILS_PREFIX + "address1";
    public static final String CONTACT_DETAILS_ADDRESS2 = CONTACT_DETAILS_PREFIX + "address2";

    protected SVGTextBinding name;
    protected SVGTextBinding email;
    protected SVGTextBinding cellPhone;
    protected SVGTextBinding workPhone;
    protected SVGTextBinding homePhone;
    protected SVGTextBinding address1;
    protected SVGTextBinding address2;
    
    /**
     * Default constructor.
     */
    public ContactDetailsForm() {
        name      = new SVGTextBinding(CONTACT_DETAILS_NAME);
        email     = new SVGTextBinding(CONTACT_DETAILS_EMAIL);
        cellPhone = new SVGTextBinding(CONTACT_DETAILS_CELL);
        workPhone = new SVGTextBinding(CONTACT_DETAILS_WORK);
        homePhone = new SVGTextBinding(CONTACT_DETAILS_HOME);
        address1  = new SVGTextBinding(CONTACT_DETAILS_ADDRESS1);
        address2  = new SVGTextBinding(CONTACT_DETAILS_ADDRESS2);
    }
    
    /**
     * Establishes new bindings between to the new skin.
     *
     * @param doc - the new skin to hook into.
     */
    public void hookSkin(final Document doc) {
        name.hookSkin(doc);
        email.hookSkin(doc);
        cellPhone.hookSkin(doc);
        workPhone.hookSkin(doc);
        homePhone.hookSkin(doc);
        address1.hookSkin(doc);
        address2.hookSkin(doc);
    }

    /**
     * Applies the input contact details to the XML UI markup.
     *
     * @param contactDetailsData - the new data to display.
     */
    void setContactDetails(ContactDetails contactDetailsData) {
        name.set(contactDetailsData.getName());
        email.set(contactDetailsData.getEmail());
        cellPhone.set(contactDetailsData.getCellPhone());
        workPhone.set(contactDetailsData.getWorkPhone());
        homePhone.set(contactDetailsData.getHomePhone());
        address1.set(contactDetailsData.getAddress1());
        address2.set(contactDetailsData.getAddress2());
    }
}
