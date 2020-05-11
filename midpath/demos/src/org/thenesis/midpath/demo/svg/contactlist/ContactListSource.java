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

import java.util.Random;

import org.thenesis.midpath.demo.svg.SVGList;

/**
 *
 */
/**
 * Encapsulates retrieving contact information. 
 */
public class ContactListSource implements SVGList.ListModel {    
    public static final int NAME = 0;
    public static final int EMAIL = 1;
    public static final int CELL_PHONE = 2;
    public static final int WORK_PHONE = 3;
    public static final int HOME_PHONE = 4;
    public static final int ADDRESS_1 = 5;
    public static final int ADDRESS_2 = 6;
            
    /**
     * Used for random phone number generation.
     */
    private static final Random random = new Random();
    
    /**
     * Fake list of data to put in the list.
     */
    private String[][] listItemsData;
    
    /**
     * Cache object used to return value in getElementAt
     */
    private ContactDetails contactDetails = new ContactDetails();
    
    /**
     * Fake list of cities.
     */
    private static final String[] SAMPLE_CITIES = {
        "Atlanta",
        "San Francisco",
        "Santa Clara",
        "New York",
        "Chicago",
        "Detroit",
        "Las Vegas"
    };
    
    /**
     * Fake list of streets
     */
    private static final String[] SAMPLE_STREETS = {
        "Network Circle",
        "El Camino Real",
        "Market Street",
        "De Anza Boulevard",
        "Stevens Creek",
        "Willow Road",
        "San Antonio Road",
        "Montague",
        "Lick Mill",
        "Cupertino Avenue"
    };
    
    public ContactListSource() {
        listItemsData = new String[][] {
            {"Alec Pietersen", "Alec.Pietersen@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Alfred Anderson", "Alfred.Anderson@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Allan Shaw", "Allan.Shaw@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Andrew Smith", "Andrew.Smith@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Archie Stoddart", "Archie.Stoddart@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Arthur Warner", "Arthur.Warner@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Aubrey Read", "Aubrey.Read@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Betty Archdale", "Betty.Archdale@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Betty Edwards", "Betty.Edwards@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Charlotte Carr", "Charlotte.Carr@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Claire Birch", "Claire.Birch@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Darren Trescoth", "Darren.Trescoth@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Jack Taylor", "Jack.Taylor@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Jim Collingwood", "Jim.Collingwood@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Kevin Stewart", "Kevin.Stewart@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Marcus Strauss", "Marcus.Strauss@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Mary Snowball", "Mary.Snowball@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Mike Gough", "Mike.Gough@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Paul Hendrick", "Paul.Hendrick@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Plum MacLaren", "Plum.MacLaren@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Robin Flintoff", "Robin.Flintoff@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},            
            {"Rosalie Johnson", "Rosalie.Johnson@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
            {"Walter Steel", "Walter.Steel@example.com", randomPhone(), randomPhone(), randomPhone(), randomAddress1(), randomAddress2()},
        };
    }
    
    /**
     * @param c the first character for the searched contact.
     * @return the index of the first contact entry with the given character
     */
    public int firstIndexFor(final char c) {
        for (int i = 0; i < listItemsData.length; i++) {
            if (Character.toLowerCase(listItemsData[i][0].charAt(0)) == c) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * @return a random street address.
     */
    private static final String randomAddress1() {
        return "" + randomDigit() + randomDigit() + randomDigit() + " " +
                randomStreetName();
    }
    
    /**
     * @return a random street name.
     */
    private static final String randomStreetName() {
        int i = (int) (random.nextFloat() * SAMPLE_STREETS.length);
        return SAMPLE_STREETS[i];
    }
    
    /**
     * @return a random city zip code and country.
     */
    private static final String randomAddress2() {
        return "" + randomDigit() + randomDigit() + randomDigit() + randomDigit() + randomDigit() + " " +
                randomCity() + ", " + "USA";
    }
    
    /**
     * @return a random city name
     */
    private static final String randomCity() {
        int i = (int) (random.nextFloat() * SAMPLE_CITIES.length);
        return SAMPLE_CITIES[i];
    }
    
    /**
     * @return a random phone number.
     */
    private static final String randomPhone() {
        return "" + randomDigit() + randomDigit() + randomDigit() + " " +
                    randomDigit() + randomDigit() + randomDigit() + " " +
                    randomDigit() + randomDigit() + " " +
                    randomDigit() + randomDigit();
    }
     
    /**
     * @return a random digit
     */
    private static final int randomDigit() {
        return (int) (9f * random.nextFloat());
    }
    
    /**
     * @return the number of contact entries.
     */
    public final int getSize() {
        return listItemsData.length;
    }
    
    /**
     * @param ci the requested contact index.
     * @return an object holding the contact details data.
     */
    public Object getElementAt(int ci) {
        contactDetails.name = listItemsData[ci][NAME];
        contactDetails.email = listItemsData[ci][EMAIL];
        contactDetails.cellPhone = listItemsData[ci][CELL_PHONE];
        contactDetails.workPhone = listItemsData[ci][WORK_PHONE];
        contactDetails.homePhone = listItemsData[ci][HOME_PHONE];
        contactDetails.address1 = listItemsData[ci][ADDRESS_1];
        contactDetails.address2 = listItemsData[ci][ADDRESS_2];
        
        return contactDetails;
    }
}
