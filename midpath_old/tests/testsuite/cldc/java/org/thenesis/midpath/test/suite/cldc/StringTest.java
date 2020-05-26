/* 
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 *
 * Copyright (C) 1999 Hewlett-Packard Company
 * 
 * This file is part of Mauve.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 */
package org.thenesis.midpath.test.suite.cldc;

import org.thenesis.midpath.test.suite.TestHarness;
import org.thenesis.midpath.test.suite.Testlet;

public class StringTest implements Testlet {

    protected static TestHarness harness;

    public void test_Basics() {
        String str1 = new String();
        harness.check(!(str1.length() != 0), "String: Basics - 1");
        harness.check(!(!str1.toString().equals("")), "test_Basics - 2");

        String str2 = new String("testing");
        harness.check(!(str2.length() != 7), "String: Basics - 3");
        harness.check(!(!str2.toString().equals("testing")), "String: Basics - 4");

        try {
            String str = null;
            String str3 = new String(str);
            harness.fail("String: Basics - 5");
        } catch (NullPointerException e) {
        }

        String str4 = new String(new StringBuffer("hi there"));
        harness.check(!(str4.length() != 8), "String: Basics - 6");
        harness.check(!(!str4.toString().equals("hi there")), "String: Basics - 7");

        char cdata[] = { 'h', 'e', 'l', 'l', 'o' };
        String str5 = new String(cdata);
        harness.check(!(str5.length() != 5), "String: Basics - 8");
        harness.check(!(!str5.toString().equals("hello")), "String: Basics - 9");

        try {
            String str6 = new String(cdata, 0, 10);
            harness.fail("String: Basics - 10");

        } catch (IndexOutOfBoundsException e) {
        }

        try {
            byte[] barr = null;
            String str7 = new String(barr, 0, 10);
            harness.fail("String: Basics - 11");

        } catch (NullPointerException e) {
        }

        String str8 = new String(cdata, 0, 4);
        harness.check(!(!str8.equals("hell")), "String: Basics - 12");

        byte bdata[] = { (byte) 'd', (byte) 'a', (byte) 'n', (byte) 'c', (byte) 'i', (byte) 'n', (byte) 'g' };

        String str14 = new String(bdata);
        harness.check(!(!str14.equals("dancing")), "String: Basics - 18");

        // EJWcr00462
        char carr[] = { 'h', 'e', 'l', 'l', 'o' };
        try {
            String str16 = new String(carr, Integer.MAX_VALUE, 1);
            harness.fail("String: Basics - 20");
        } catch (IndexOutOfBoundsException e) {
        }
        byte arr2[] = { (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e' };

        // this used to cause the vm to core dump (cr543)
        String s = "\u0d3e";

    }

    public void test_toString() {
        String str1 = "218943289";

        harness.check(!(!str1.toString().equals("218943289")), "String.toString: - 1");

        harness.check(!(str1 != "218943289"), "String.toString: - 2");

        harness.check(!(!str1.equals(str1.toString())), "String.toString: - 3");
    }

    public void test_equals() {
        String str2 = new String("Nectar");

        harness.check(!(str2.equals(null)), "String.equals: - 1");

        harness.check(!(!str2.equals("Nectar")), "String.equals: - 2");

        harness.check(!(str2.equals("")), "String.equals: - 3");

        harness.check(!(str2.equals("nectar")), "String.equals: - 4");

        harness.check(!(!"".equals("")), "String.equals: - 5");

    }

    public void test_hashCode() {
        String str1 = "hp";
        String str2 = "Hewlett Packard Company";

        int hash1 = 'h' * 31 + 'p';
        int acthash1 = str1.hashCode();

        harness.check(!(hash1 != acthash1), "String.hashCode: - 1");
    }

    public void test_length() {
        harness.check(!("".length() != 0), "String.length: - 1");

        harness.check(!("pentium".length() != 7), "String.length: - 2");
    }

    public void test_charAt() {
        harness.check(!("abcd".charAt(0) != 'a' || "abcd".charAt(1) != 'b' || "abcd".charAt(2) != 'c' || "abcd".charAt(3) != 'd'),
                "String.charAt: - 1");

        try {
            char ch = "abcd".charAt(4);
            harness.fail("String.charAt: - 2");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            char ch = "abcd".charAt(-1);
            harness.fail("String.charAt: - 3");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    public void test_getChars() {
        String str = "abcdefghijklmn";

        try {
            str.getChars(0, 3, null, 1);
            harness.fail("String.getChars: - 1");
        } catch (NullPointerException e) {
        }

        char dst[] = new char[5];

        try {
            str.getChars(-1, 3, dst, 1);
            harness.fail("String.getChars: - 2");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            str.getChars(4, 3, dst, 1);
            harness.fail("String.getChars: - 3");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            str.getChars(1, 15, dst, 1);
            harness.fail("String.getChars: - 4");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            str.getChars(1, 5, dst, -1);
            harness.fail("String.getChars: - 5");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            str.getChars(1, 10, dst, 1);
            harness.fail("String.getChars: - 6");
        } catch (IndexOutOfBoundsException e) {
        }

        str.getChars(0, 5, dst, 0);
        harness.check(!(dst[0] != 'a' || dst[1] != 'b' || dst[2] != 'c' || dst[3] != 'd' || dst[4] != 'e'), "String.getChars: - 7");

        dst[0] = dst[1] = dst[2] = dst[3] = dst[4] = ' ';
        str.getChars(0, 0, dst, 0);
        harness.check(!(dst[0] != ' ' || dst[1] != ' ' || dst[2] != ' ' || dst[3] != ' ' || dst[4] != ' '), "String.getChars: - 9");

        dst[0] = dst[1] = dst[2] = dst[3] = dst[4] = ' ';
        str.getChars(0, 1, dst, 0);
        harness.check(!(dst[0] != 'a' || dst[1] != ' ' || dst[2] != ' ' || dst[3] != ' ' || dst[4] != ' '), "String.getChars: - 10");
    }

    public void test_getBytes() {
        String str = "abcdefghijklmn";

        byte dst[] = new byte[5];

        byte[] dst1 = new byte[40];
        dst1 = str.getBytes();
        harness.check(!(dst1[0] != 'a' || dst1[1] != 'b' || dst1[2] != 'c' || dst1[3] != 'd' || dst1[4] != 'e'), "String.getBytes");
    }

    public void test_toCharArray() {
        char[] charr = "abcde".toCharArray();

        harness.check(!(charr[0] != 'a' || charr[1] != 'b' || charr[2] != 'c' || charr[3] != 'd' || charr[4] != 'e'),
                "String.toCharArray: - 1");

        char[] charr1 = "".toCharArray();

        harness.check(!(charr1.length > 0), "String.toCharArray: - 2");
    }

    public void test_equalsIgnoreCase() {
        harness.check(!("hi".equalsIgnoreCase(null)), "String.equalsIgnoreCase: - 1");

        harness.check(!(!"hi".equalsIgnoreCase("HI")), "String.equalsIgnoreCase: - 2");

        harness.check(!("hi".equalsIgnoreCase("pq")), "String.equalsIgnoreCase: - 3");

        harness.check(!("hi".equalsIgnoreCase("HI ")), "String.equalsIgnoreCase: - 4");

    }

    public void test_compareTo() {
        try {
            int res = "abc".compareTo(null);
            harness.fail("String.compareTo: - 1");
        } catch (NullPointerException e) {
        }

        harness.check(!("abc".compareTo("bcdef") >= 0), "String.compareTo: - 2");

        harness.check(!("abc".compareTo("abc") != 0), "String.compareTo: - 3");

        harness.check(!("abc".compareTo("aabc") <= 0), "String.compareTo: - 4");

        harness.check(!("abcd".compareTo("abc") <= 0), "String.compareTo: - 5");

        harness.check(!("".compareTo("abc") >= 0), "String.compareTo: - 6");
    }

    public void test_regionMatches() {

        try {
            boolean res = "abc".regionMatches(true, 0, null, 0, 2);
            harness.fail("String.regionMatches: - 11");
        } catch (NullPointerException e) {
        }

        harness.check(!("abcd".regionMatches(true, -1, "abcd", 0, 2)), "String.regionMatches: - 12");
        harness.check(!("abcd".regionMatches(true, 0, "abcd", -1, 2)), "String.regionMatches: - 13");
        harness.check(!("abcd".regionMatches(true, 0, "abcd", 0, 10)), "String.regionMatches: - 14");
        harness.check(!("abcd".regionMatches(true, 0, "ab", 0, 3)), "String.regionMatches: - 15");

        harness.check(!(!"abcd".regionMatches(true, 1, "abc", 1, 2)), "String.regionMatches: - 16");

        harness.check(!(!"abcd".regionMatches(true, 1, "abc", 1, 0)), "String.regionMatches: - 17");

        harness.check(!(!"abcd".regionMatches(true, 1, "ABC", 1, 2)), "String.regionMatches: - 18");
        harness.check(!("abcd".regionMatches(false, 1, "ABC", 1, 2)), "String.regionMatches: - 19");
    }

    public void test_startsWith() {
        harness.check(!(!"abcdef".startsWith("abc")), "String.startsWith: - 1");

        try {
            boolean b = "abcdef".startsWith(null);
            harness.fail("String.startsWith: - 2");
        } catch (NullPointerException e) {
        }

        harness.check(!("abcdef".startsWith("ABC")), "String.startsWith: - 3");

        harness.check(!(!"abcdef".startsWith("")), "String.startsWith: - 4");

        harness.check(!("abc".startsWith("abcd")), "String.startsWith: - 5");

        harness.check(!(!"abcdef".startsWith("abc", 0)), "String.startsWith: - 6");

        try {
            boolean b = "abcdef".startsWith(null, 0);
            harness.fail("String.startsWith: - 7");
        } catch (NullPointerException e) {
        }

        harness.check(!("abcdef".startsWith("ABC", 2)), "String.startsWith: - 8");

        harness.check(!(!"abcdef".startsWith("", 0)), "String.startsWith: - 9");

        harness.check(!("abc".startsWith("abcd", 3)), "String.startsWith: - 10");

        harness.check(!("abc".startsWith("abc", 10)), "String.startsWith: - 11");
    }

    public void test_endsWith() {
        harness.check(!(!"abcdef".endsWith("def")), "String.endsWith: - 1");

        try {
            boolean b = "abcdef".endsWith(null);
            harness.fail("String.endsWith: - 2");
        } catch (NullPointerException e) {
        }

        harness.check(!("abcdef".endsWith("DEF")), "String.endsWith: - 3");

        harness.check(!(!"abcdef".endsWith("")), "String.endsWith: - 4");

        harness.check(!("bcde".endsWith("abcd")), "String.endsWith: - 5");

    }

    public void test_indexOf() {
        harness.check(!("a".indexOf('a') != 0), "String.indexOf: - 1");

        harness.check(!("aabc".indexOf('c') != 3), "String.indexOf: - 2");

        harness.check(!("a".indexOf('c') != -1), "String.indexOf: - 3");

        harness.check(!("".indexOf('a') != -1), "String.indexOf: - 4");

        harness.check(!("abcde".indexOf('b', 3) != -1), "String.indexOf: - 5");
        harness.check(!("abcde".indexOf('b', 0) != 1), "String.indexOf: - 6");
        harness.check(!("abcdee".indexOf('e', 3) != 4), "String.indexOf: - 7");
        harness.check(!("abcdee".indexOf('e', 5) != 5), "String.indexOf: - 8");

        harness.check(!("abcdee".indexOf('e', -5) != 4), "String.indexOf: - 9");
        harness.check(!("abcdee".indexOf('e', 15) != -1), "String.indexOf: - 10");

        harness.check(!("abcdee".indexOf("babu") != -1), "String.indexOf: - 11");
        try {
            int x = "abcdee".indexOf(null);
            harness.fail("String.indexOf: - 12");
        } catch (NullPointerException e) {
        }

        harness.check(!("abcdee".indexOf("") != 0), "String.indexOf: - 13");
        harness.check(!("abcdee".indexOf("ee") != 4), "String.indexOf: - 14");
        harness.check(!("abcbcbc".indexOf("cbc") != 2), "String.indexOf: - 15");

        harness.check(!("abcdee".indexOf("babu", 3) != -1), "String.indexOf: - 16");
        try {
            int x = "abcdee".indexOf(null, 0);
            harness.fail("String.indexOf: - 17");
        } catch (NullPointerException e) {
        }

        harness.check(!("abcdee".indexOf("", 0) != 0), "String.indexOf: - 18");
        harness.check(!("abcdee".indexOf("ee", 4) != 4), "String.indexOf: - 19");
        harness.check(!("abcbcbc".indexOf("cbc", 4) != 4), "String.indexOf: - 20");
        // EJWcr00463
        if ("hello \u5236 world".indexOf('\u5236') != 6) {
            harness.fail("String.indexOf: - 21");
        }
        if ("hello \u0645 world".indexOf('\u0645') != 6) {
            harness.fail("String.indexOf: - 22");
        }
        if ("hello \u07ff world".indexOf('\u07ff') != 6) {
            harness.fail("String.indexOf: - 23");
        }
    }

    public void test_lastIndexOf() {
        harness.check(!("a".lastIndexOf('a') != 0), "String.lastIndexOf: - 1");

        harness.check(!("aabc".lastIndexOf('c') != 3), "String.lastIndexOf: - 2");

        harness.check(!("a".lastIndexOf('c') != -1), "String.lastIndexOf: - 3");

        harness.check(!("".lastIndexOf('a') != -1), "String.lastIndexOf: - 4");

        harness.check(!("abcde".lastIndexOf('b', 0) != -1), "String.lastIndexOf: - 5");
        harness.check(!("abcde".lastIndexOf('b', 4) != 1), "String.lastIndexOf: - 6");
        harness.check(!("abcdee".lastIndexOf('e', 7) != 5), "String.lastIndexOf: - 7");
        harness.check(!("abcdee".lastIndexOf('e', 5) != 5), "String.lastIndexOf: - 8");

        harness.check(!("abcdee".lastIndexOf('e', -5) != -1), "String.lastIndexOf: - 9");
        harness.check(!("abcdee".lastIndexOf('e', 15) != 5), "String.lastIndexOf: - 10");
    }

    public void test_substring() {
        harness.check(!(!"unhappy".substring(2).equals("happy")), "String.substring: - 1");
        harness.check(!(!"Harbison".substring(3).equals("bison")), "String.substring: - 2");
        harness.check(!(!"emptiness".substring(9).equals("")), "String.substring: - 3");

        try {
            String str = "hi there".substring(-1);
            harness.fail("String.substring: - 4");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            String str = "hi there".substring(10);
            harness.fail("String.substring: - 5");
        } catch (IndexOutOfBoundsException e) {
        }

        harness.check(!(!"hamburger".substring(4, 8).equals("urge")), "String.substring: - 6");
        harness.check(!(!"smiles".substring(1, 5).equals("mile")), "String.substring: - 7");
        harness.check(!(!"emptiness".substring(2, 2).equals("")), "String.substring: - 8");

        try {
            String str = "hi there".substring(-1, 3);
            harness.fail("String.substring: - 9");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            String str = "hi there".substring(0, 10);
            harness.fail("String.substring: - 10");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            String str = "hi there".substring(7, 6);
            harness.fail("String.substring: - 11");
        } catch (IndexOutOfBoundsException e) {
        }

    }

    public void test_concat() {
        try {
            String str = "help".concat(null);
            harness.fail("String.concat: - 1");
        } catch (NullPointerException e) {
        }

        harness.check(!(!"help".concat("me").equals("helpme")), "String.concat: - 2");

        harness.check(!(!"to".concat("get").concat("her").equals("together")), "String.concat: - 3");

        harness.check(!("hi".concat("") != "hi"), "String.concat: - 4");

        String str1 = "".concat("there");
        harness.check(!(!str1.equals("there")), "String.concat: - 5");

        // EJWcr00467
        String str2 = new String();
        try {
            str2 = str2.concat("hello");
            if (!str2.equals("hello")) {
                harness.fail("String.concat: - 7");
            }
        } catch (Exception e) {
            harness.fail("String.concat: - 6");
        }
    }

    public void test_replace() {
        harness.check(!(!"mesquite in your cellar".replace('e', 'o').equals("mosquito in your collar")), "String.replace: - 1");

        harness.check(!(!"the war of baronets".replace('r', 'y').equals("the way of bayonets")), "String.replace: - 2");

        harness
                .check(!(!"sparring with a purple porpoise".replace('p', 't').equals("starring with a turtle tortoise")),
                        "String.replace: - 3");

        harness.check(!(!"JonL".replace('q', 'x').equals("JonL")), "String.replace: - 4");

        harness.check(!(!"ppppppppppppp".replace('p', 'p').equals("ppppppppppppp")), "String.replace: - 5");

        harness.check(!(!"ppppppppppppp".replace('p', '1').equals("1111111111111")), "String.replace: - 6");
        harness.check(!(!"hp".replace('c', 'd').equals("hp")), "String.replace: - 7");
        harness.check(!(!"vmhere".replace('a', 'd').equals("vmhere")), "String.replace: - 8");

    }

    public void test_toLowerCase() {
        harness.check(!(!"".toLowerCase().equals("")), "String.toLowerCase: - 1");

        harness.check(!(!"French Fries".toLowerCase().equals("french fries")), "String.toLowerCase: - 2");

        harness.check(!(!"SMALL-VM".toLowerCase().equals("small-vm")), "String.toLowerCase: - 3");
    }

    public void test_toUpperCase() {
        harness.check(!(!"".toUpperCase().equals("")), "String.toUpperCase: - 1");

        harness.check(!(!"French Fries".toUpperCase().equals("FRENCH FRIES")), "String.toUpperCase: - 2");

        harness.check(!(!"SMALL-VM".toUpperCase().equals("SMALL-VM")), "String.toUpperCase: - 3");

        harness.check(!(!"small-jvm".toUpperCase().equals("SMALL-JVM")), "String.toUpperCase: - 4");
    }

    public void test_valueOf() {
        harness.check(!(!String.valueOf((Object) null).equals("null")), "String.valueOf: - 1");

        Object obj = new Object();
        harness.check(!(!String.valueOf(obj).equals(obj.toString())), "String.valueOf: - 2");

        try {
            char[] data = null;
            String str = String.valueOf(data);
        } catch (NullPointerException e) {
        }

        char[] data = { 'h', 'e', 'l', 'l', 'o' };
        harness.check(!(!String.valueOf(data).equals("hello")), "String.valueOf: - 3");

        try {
            String str = String.valueOf(data, -1, 4);
            harness.fail("String.valueOf: - 4");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            String str = String.valueOf(data, 1, 5);
            harness.fail("String.valueOf: - 5");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            String str = String.valueOf(data, 1, -5);
            harness.fail("String.valueOf: - 6");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            String str = String.valueOf(null, 1, 3);
            harness.fail("String.valueOf: - 7");
        } catch (NullPointerException e) {
        }

        harness.check(!(!String.valueOf(data, 2, 2).equals("ll")), "String.valueOf: - 8");

        harness.check(!(!String.valueOf(true).equals("true")), "String.valueOf: - 9");

        harness.check(!(!String.valueOf(false).equals("false")), "String.valueOf: - 10");

        harness.check(!(!String.valueOf('c').equals("c")), "String.valueOf: - 11");

        harness.check(!(!String.valueOf(' ').equals(" ")), "String.valueOf: - 12");

        harness.check(!(!String.valueOf(234).equals("234")), "String.valueOf: - 13");

        harness.check(!(!String.valueOf(234L).equals("234")), "String.valueOf: - 14");

        harness.check(!(!String.valueOf(23.45f).equals("23.45")), "String.valueOf: - 15");

        harness.check(!(!String.valueOf(23.4).equals("23.4")), "String.valueOf: - 16");
    }

    public void test_intern() {
        String hp = "hp";
        String nullstr = "";
        harness.check(!("hp".intern() != hp.intern()), "String.intern: - 1");
        harness.check(!("pqr".intern() == hp.intern()), "String.intern: - 2");
        harness.check(!("".intern() != nullstr.intern()), "String.intern: - 3");
        harness.check(!("".intern() == hp.intern()), "String.intern: - 4");
        hp = "";
        harness.check(!("".intern() != hp.intern()), "String.intern: - 5");
        StringBuffer buff = new StringBuffer();
        buff.append('a');
        buff.append('b');
        harness.check(!("ab".intern() != buff.toString().intern()), "String.intern: - 6");
        StringBuffer buff1 = new StringBuffer();
        harness.check(!("".intern() != buff1.toString().intern()), "String.intern: - 7");

    }

    public void test_trim() {
        
        harness.checkPoint("String.trim");
        
        String source = "   laura";
        String dest;

        dest = source.trim();
        if (!dest.equals("laura")) {
            harness.fail("Error - test_trim - 1");
            System.out.println("expected 'laura', got '" + dest + "'");
        }

        source = "                        laura";
        dest = source.trim();
        if (!dest.equals("laura")) {
            harness.fail("Error - test_trim - 2");
            System.out.println("expected 'laura', got '" + dest + "'");
        }

        source = "              ";
        dest = source.trim();
        if (!dest.equals("")) {
            harness.fail("Error - test_trim - 3");
            System.out.println("expected '', got '" + dest + "'");
        }
        source = "laura";
        dest = source.trim();
        if (dest != source) {
            harness.fail("Error - test_trim - 4");
            System.out.println("Expected strings to be equal");
        }
        source = "l        ";
        dest = source.trim();
        if (!dest.equals("l")) {
            harness.fail("Error - test_trim - 5");
            System.out.println("expected 'l', got '" + dest + "'");
        }
        source = "           l";
        dest = source.trim();
        if (!dest.equals("l")) {
            harness.fail("Error - test_trim - 6");
            System.out.println("expected 'l', got '" + dest + "'");
        }
        source = "           l            ";
        dest = source.trim();
        if (!dest.equals("l")) {
            harness.fail("Error - test_trim - 7");
            System.out.println("expected 'l', got '" + dest + "'");
        }
        source = "           l a u r a             ";
        dest = source.trim();
        if (!dest.equals("l a u r a")) {
            harness.fail("Error - test_trim - 8");
            System.out.println("expected 'l a u r a', got '" + dest + "'");
        }
    }

    public void testall() {
        test_Basics();
        test_toString();
        test_equals();
        test_hashCode();
        test_length();
        test_charAt();
        test_getChars();
        test_getBytes();
        test_toCharArray();
        test_equalsIgnoreCase();
        test_compareTo();
        test_regionMatches();
        test_startsWith();
        test_endsWith();
        test_indexOf();
        test_lastIndexOf();
        test_substring();
        test_concat();
        test_replace();
        test_toLowerCase();
        test_toUpperCase();
        test_valueOf();
        test_intern();
        test_trim();
    }

    public void test(TestHarness the_harness) {
        harness = the_harness;
        testall();
    }

}
