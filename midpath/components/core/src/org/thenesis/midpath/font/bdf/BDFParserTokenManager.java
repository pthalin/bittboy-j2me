/*
 * MIDPath - Copyright (C) 2006-2007 Guillaume Legris, Mathieu Legris
 * 
 * Odonata - Copyright (C) 2002-2006 Stephane Meslin-Weber <steph@tangency.co.uk>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt). 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package org.thenesis.midpath.font.bdf;

public class BDFParserTokenManager implements BDFParserConstants
{
  public  java.io.PrintStream debugStream = System.out;
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjMoveStringLiteralDfa0_7()
{
   return jjMoveNfa_7(4, 0);
}
private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private final int jjMoveNfa_7(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 4;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 4:
                  if ((0xffffffffffffdbffL & l) != 0L)
                     jjCheckNAddStates(0, 2);
                  else if ((0x2400L & l) != 0L)
                  {
                     if (kind > 30)
                        kind = 30;
                  }
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 0:
                  if ((0xffffffffffffdbffL & l) != 0L)
                     jjCheckNAddStates(0, 2);
                  break;
               case 1:
                  if ((0x2400L & l) != 0L && kind > 30)
                     kind = 30;
                  break;
               case 2:
                  if (curChar == 10 && kind > 30)
                     kind = 30;
                  break;
               case 3:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 4:
               case 0:
                  jjCheckNAddStates(0, 2);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 4:
               case 0:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddStates(0, 2);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 4 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjStopStringLiteralDfa_5(int pos, long active0)
{
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_5(int pos, long active0)
{
   return jjMoveNfa_5(jjStopStringLiteralDfa_5(pos, active0), pos + 1);
}
private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private final int jjStartNfaWithStates_5(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_5(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_5()
{
   switch(curChar)
   {
      default :
         return jjMoveNfa_5(0, 0);
   }
}
private final int jjMoveNfa_5(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 1;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ff200000000000L & l) == 0L)
                     break;
                  kind = 25;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 1 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjMoveStringLiteralDfa0_3()
{
   return jjMoveNfa_3(0, 0);
}
private final int jjMoveNfa_3(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 3;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x2400L & l) != 0L)
                  {
                     if (kind > 14)
                        kind = 14;
                  }
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 1:
                  if (curChar == 10 && kind > 14)
                     kind = 14;
                  break;
               case 2:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 1;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 3 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjMoveStringLiteralDfa0_2()
{
   return jjMoveNfa_2(0, 0);
}
private final int jjMoveNfa_2(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 1;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0xffffffffffffdbffL & l) == 0L)
                     break;
                  kind = 13;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  kind = 13;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 13)
                     kind = 13;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 1 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 46:
         return jjStopAtPos(0, 1);
      case 66:
         return jjMoveStringLiteralDfa1_0(0xc0000L);
      case 67:
         return jjMoveStringLiteralDfa1_0(0x20000440L);
      case 68:
         return jjMoveStringLiteralDfa1_0(0x20000L);
      case 69:
         return jjMoveStringLiteralDfa1_0(0x8020L);
      case 70:
         return jjMoveStringLiteralDfa1_0(0x80000080L);
      case 77:
         return jjMoveStringLiteralDfa1_0(0x100L);
      case 83:
         return jjMoveStringLiteralDfa1_0(0x810a10L);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private final int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 66:
         return jjMoveStringLiteralDfa2_0(active0, 0x40000L);
      case 69:
         return jjMoveStringLiteralDfa2_0(active0, 0x100L);
      case 72:
         return jjMoveStringLiteralDfa2_0(active0, 0x400L);
      case 73:
         return jjMoveStringLiteralDfa2_0(active0, 0x80200L);
      case 78:
         return jjMoveStringLiteralDfa2_0(active0, 0x8020L);
      case 79:
         return jjMoveStringLiteralDfa2_0(active0, 0xa00000c0L);
      case 84:
         return jjMoveStringLiteralDfa2_0(active0, 0x800810L);
      case 87:
         return jjMoveStringLiteralDfa2_0(active0, 0x30000L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private final int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 65:
         return jjMoveStringLiteralDfa3_0(active0, 0x800c10L);
      case 67:
         return jjMoveStringLiteralDfa3_0(active0, 0x8000L);
      case 68:
         return jjMoveStringLiteralDfa3_0(active0, 0x20L);
      case 73:
         return jjMoveStringLiteralDfa3_0(active0, 0x30000L);
      case 77:
         return jjMoveStringLiteralDfa3_0(active0, 0x20000000L);
      case 78:
         return jjMoveStringLiteralDfa3_0(active0, 0x800000c0L);
      case 84:
         return jjMoveStringLiteralDfa3_0(active0, 0x80100L);
      case 88:
         if ((active0 & 0x40000L) != 0L)
            return jjStopAtPos(2, 18);
         break;
      case 90:
         return jjMoveStringLiteralDfa3_0(active0, 0x200L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private final int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 68:
         return jjMoveStringLiteralDfa4_0(active0, 0x30000L);
      case 69:
         if ((active0 & 0x200L) != 0L)
            return jjStopAtPos(3, 9);
         break;
      case 70:
         return jjMoveStringLiteralDfa4_0(active0, 0x20L);
      case 77:
         return jjMoveStringLiteralDfa4_0(active0, 0x20080000L);
      case 79:
         return jjMoveStringLiteralDfa4_0(active0, 0x8000L);
      case 82:
         return jjMoveStringLiteralDfa4_0(active0, 0x800d10L);
      case 84:
         if ((active0 & 0x80000000L) != 0L)
         {
            jjmatchedKind = 31;
            jjmatchedPos = 3;
         }
         return jjMoveStringLiteralDfa4_0(active0, 0xc0L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private final int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 65:
         return jjMoveStringLiteralDfa5_0(active0, 0x80000L);
      case 66:
         return jjMoveStringLiteralDfa5_0(active0, 0x80L);
      case 68:
         return jjMoveStringLiteralDfa5_0(active0, 0x8000L);
      case 69:
         return jjMoveStringLiteralDfa5_0(active0, 0x20000040L);
      case 73:
         return jjMoveStringLiteralDfa5_0(active0, 0x100L);
      case 79:
         return jjMoveStringLiteralDfa5_0(active0, 0x20L);
      case 83:
         if ((active0 & 0x400L) != 0L)
            return jjStopAtPos(4, 10);
         break;
      case 84:
         return jjMoveStringLiteralDfa5_0(active0, 0x830810L);
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private final int jjMoveStringLiteralDfa5_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 67:
         return jjMoveStringLiteralDfa6_0(active0, 0x900L);
      case 70:
         return jjMoveStringLiteralDfa6_0(active0, 0x10L);
      case 72:
         if ((active0 & 0x10000L) != 0L)
            return jjStopAtPos(5, 16);
         else if ((active0 & 0x20000L) != 0L)
            return jjStopAtPos(5, 17);
         break;
      case 73:
         return jjMoveStringLiteralDfa6_0(active0, 0x8000L);
      case 78:
         return jjMoveStringLiteralDfa6_0(active0, 0x20000060L);
      case 79:
         return jjMoveStringLiteralDfa6_0(active0, 0x80L);
      case 80:
         if ((active0 & 0x80000L) != 0L)
            return jjStopAtPos(5, 19);
         return jjMoveStringLiteralDfa6_0(active0, 0x800000L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
private final int jjMoveStringLiteralDfa6_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 72:
         return jjMoveStringLiteralDfa7_0(active0, 0x800L);
      case 78:
         return jjMoveStringLiteralDfa7_0(active0, 0x8000L);
      case 79:
         return jjMoveStringLiteralDfa7_0(active0, 0x10L);
      case 82:
         return jjMoveStringLiteralDfa7_0(active0, 0x800000L);
      case 83:
         return jjMoveStringLiteralDfa7_0(active0, 0x100L);
      case 84:
         if ((active0 & 0x20L) != 0L)
            return jjStopAtPos(6, 5);
         else if ((active0 & 0x20000000L) != 0L)
            return jjStopAtPos(6, 29);
         return jjMoveStringLiteralDfa7_0(active0, 0x40L);
      case 85:
         return jjMoveStringLiteralDfa7_0(active0, 0x80L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
private final int jjMoveStringLiteralDfa7_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 65:
         return jjMoveStringLiteralDfa8_0(active0, 0x800L);
      case 71:
         if ((active0 & 0x8000L) != 0L)
            return jjStopAtPos(7, 15);
         break;
      case 78:
         return jjMoveStringLiteralDfa8_0(active0, 0x90L);
      case 79:
         return jjMoveStringLiteralDfa8_0(active0, 0x800000L);
      case 83:
         return jjMoveStringLiteralDfa8_0(active0, 0x100L);
      case 86:
         return jjMoveStringLiteralDfa8_0(active0, 0x40L);
      default :
         break;
   }
   return jjStartNfa_0(6, active0);
}
private final int jjMoveStringLiteralDfa8_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(6, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0);
      return 8;
   }
   switch(curChar)
   {
      case 68:
         return jjMoveStringLiteralDfa9_0(active0, 0x80L);
      case 69:
         return jjMoveStringLiteralDfa9_0(active0, 0x140L);
      case 80:
         return jjMoveStringLiteralDfa9_0(active0, 0x800000L);
      case 82:
         if ((active0 & 0x800L) != 0L)
            return jjStopAtPos(8, 11);
         break;
      case 84:
         if ((active0 & 0x10L) != 0L)
            return jjStopAtPos(8, 4);
         break;
      default :
         break;
   }
   return jjStartNfa_0(7, active0);
}
private final int jjMoveStringLiteralDfa9_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(7, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(8, active0);
      return 9;
   }
   switch(curChar)
   {
      case 69:
         return jjMoveStringLiteralDfa10_0(active0, 0x800000L);
      case 73:
         return jjMoveStringLiteralDfa10_0(active0, 0x80L);
      case 82:
         return jjMoveStringLiteralDfa10_0(active0, 0x40L);
      case 84:
         if ((active0 & 0x100L) != 0L)
            return jjStopAtPos(9, 8);
         break;
      default :
         break;
   }
   return jjStartNfa_0(8, active0);
}
private final int jjMoveStringLiteralDfa10_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(8, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(9, active0);
      return 10;
   }
   switch(curChar)
   {
      case 78:
         return jjMoveStringLiteralDfa11_0(active0, 0x80L);
      case 82:
         return jjMoveStringLiteralDfa11_0(active0, 0x800000L);
      case 83:
         return jjMoveStringLiteralDfa11_0(active0, 0x40L);
      default :
         break;
   }
   return jjStartNfa_0(9, active0);
}
private final int jjMoveStringLiteralDfa11_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(9, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(10, active0);
      return 11;
   }
   switch(curChar)
   {
      case 71:
         return jjMoveStringLiteralDfa12_0(active0, 0x80L);
      case 73:
         return jjMoveStringLiteralDfa12_0(active0, 0x40L);
      case 84:
         return jjMoveStringLiteralDfa12_0(active0, 0x800000L);
      default :
         break;
   }
   return jjStartNfa_0(10, active0);
}
private final int jjMoveStringLiteralDfa12_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(10, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(11, active0);
      return 12;
   }
   switch(curChar)
   {
      case 66:
         return jjMoveStringLiteralDfa13_0(active0, 0x80L);
      case 73:
         return jjMoveStringLiteralDfa13_0(active0, 0x800000L);
      case 79:
         return jjMoveStringLiteralDfa13_0(active0, 0x40L);
      default :
         break;
   }
   return jjStartNfa_0(11, active0);
}
private final int jjMoveStringLiteralDfa13_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(11, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(12, active0);
      return 13;
   }
   switch(curChar)
   {
      case 69:
         return jjMoveStringLiteralDfa14_0(active0, 0x800000L);
      case 78:
         if ((active0 & 0x40L) != 0L)
            return jjStopAtPos(13, 6);
         break;
      case 79:
         return jjMoveStringLiteralDfa14_0(active0, 0x80L);
      default :
         break;
   }
   return jjStartNfa_0(12, active0);
}
private final int jjMoveStringLiteralDfa14_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(12, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(13, active0);
      return 14;
   }
   switch(curChar)
   {
      case 83:
         if ((active0 & 0x800000L) != 0L)
            return jjStopAtPos(14, 23);
         break;
      case 88:
         if ((active0 & 0x80L) != 0L)
            return jjStopAtPos(14, 7);
         break;
      default :
         break;
   }
   return jjStartNfa_0(13, active0);
}
private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 4;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ff200000000000L & l) != 0L)
                  {
                     if (kind > 3)
                        kind = 3;
                     jjCheckNAdd(3);
                  }
                  else if ((0x100002600L & l) != 0L)
                  {
                     if (kind > 2)
                        kind = 2;
                  }
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 1:
                  if (curChar == 10 && kind > 2)
                     kind = 2;
                  break;
               case 2:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 3:
                  if ((0x3ff200000000000L & l) == 0L)
                     break;
                  kind = 3;
                  jjCheckNAdd(3);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 4 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjStopStringLiteralDfa_4(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x200000L) != 0L)
            return 1;
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_4(int pos, long active0)
{
   return jjMoveNfa_4(jjStopStringLiteralDfa_4(pos, active0), pos + 1);
}
private final int jjStartNfaWithStates_4(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_4(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_4()
{
   switch(curChar)
   {
      case 69:
         return jjMoveStringLiteralDfa1_4(0x200000L);
      default :
         return jjMoveNfa_4(0, 0);
   }
}
private final int jjMoveStringLiteralDfa1_4(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_4(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 78:
         return jjMoveStringLiteralDfa2_4(active0, 0x200000L);
      default :
         break;
   }
   return jjStartNfa_4(0, active0);
}
private final int jjMoveStringLiteralDfa2_4(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_4(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_4(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 68:
         return jjMoveStringLiteralDfa3_4(active0, 0x200000L);
      default :
         break;
   }
   return jjStartNfa_4(1, active0);
}
private final int jjMoveStringLiteralDfa3_4(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_4(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_4(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 67:
         return jjMoveStringLiteralDfa4_4(active0, 0x200000L);
      default :
         break;
   }
   return jjStartNfa_4(2, active0);
}
private final int jjMoveStringLiteralDfa4_4(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_4(2, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_4(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 72:
         return jjMoveStringLiteralDfa5_4(active0, 0x200000L);
      default :
         break;
   }
   return jjStartNfa_4(3, active0);
}
private final int jjMoveStringLiteralDfa5_4(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_4(3, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_4(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 65:
         return jjMoveStringLiteralDfa6_4(active0, 0x200000L);
      default :
         break;
   }
   return jjStartNfa_4(4, active0);
}
private final int jjMoveStringLiteralDfa6_4(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_4(4, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_4(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 82:
         if ((active0 & 0x200000L) != 0L)
            return jjStopAtPos(6, 21);
         break;
      default :
         break;
   }
   return jjStartNfa_4(5, active0);
}
private final int jjMoveNfa_4(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 5;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 1;
                  else if ((0x100002600L & l) != 0L)
                  {
                     if (kind > 22)
                        kind = 22;
                  }
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 1:
                  if ((0x3ff000000000000L & l) != 0L && kind > 20)
                     kind = 20;
                  break;
               case 2:
                  if ((0x100002600L & l) != 0L && kind > 22)
                     kind = 22;
                  break;
               case 3:
                  if (curChar == 10 && kind > 22)
                     kind = 22;
                  break;
               case 4:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 1;
                  break;
               case 1:
                  if ((0x7e0000007eL & l) != 0L && kind > 20)
                     kind = 20;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 5 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjMoveStringLiteralDfa0_1()
{
   switch(curChar)
   {
      case 32:
         return jjStopAtPos(0, 12);
      default :
         return 1;
   }
}
private final int jjStopStringLiteralDfa_6(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 27;
            return 0;
         }
         return -1;
      case 1:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 1;
            return 0;
         }
         return -1;
      case 2:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 2;
            return 0;
         }
         return -1;
      case 3:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 3;
            return 0;
         }
         return -1;
      case 4:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 4;
            return 0;
         }
         return -1;
      case 5:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 5;
            return 0;
         }
         return -1;
      case 6:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 6;
            return 0;
         }
         return -1;
      case 7:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 7;
            return 0;
         }
         return -1;
      case 8:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 8;
            return 0;
         }
         return -1;
      case 9:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 9;
            return 0;
         }
         return -1;
      case 10:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 10;
            return 0;
         }
         return -1;
      case 11:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 27;
            jjmatchedPos = 11;
            return 0;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_6(int pos, long active0)
{
   return jjMoveNfa_6(jjStopStringLiteralDfa_6(pos, active0), pos + 1);
}
private final int jjStartNfaWithStates_6(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_6(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_6()
{
   switch(curChar)
   {
      case 69:
         return jjMoveStringLiteralDfa1_6(0x4000000L);
      default :
         return jjMoveNfa_6(1, 0);
   }
}
private final int jjMoveStringLiteralDfa1_6(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_6(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 78:
         return jjMoveStringLiteralDfa2_6(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_6(0, active0);
}
private final int jjMoveStringLiteralDfa2_6(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_6(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_6(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 68:
         return jjMoveStringLiteralDfa3_6(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_6(1, active0);
}
private final int jjMoveStringLiteralDfa3_6(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_6(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_6(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 80:
         return jjMoveStringLiteralDfa4_6(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_6(2, active0);
}
private final int jjMoveStringLiteralDfa4_6(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_6(2, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_6(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 82:
         return jjMoveStringLiteralDfa5_6(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_6(3, active0);
}
private final int jjMoveStringLiteralDfa5_6(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_6(3, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_6(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 79:
         return jjMoveStringLiteralDfa6_6(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_6(4, active0);
}
private final int jjMoveStringLiteralDfa6_6(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_6(4, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_6(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 80:
         return jjMoveStringLiteralDfa7_6(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_6(5, active0);
}
private final int jjMoveStringLiteralDfa7_6(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_6(5, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_6(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 69:
         return jjMoveStringLiteralDfa8_6(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_6(6, active0);
}
private final int jjMoveStringLiteralDfa8_6(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_6(6, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_6(7, active0);
      return 8;
   }
   switch(curChar)
   {
      case 82:
         return jjMoveStringLiteralDfa9_6(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_6(7, active0);
}
private final int jjMoveStringLiteralDfa9_6(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_6(7, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_6(8, active0);
      return 9;
   }
   switch(curChar)
   {
      case 84:
         return jjMoveStringLiteralDfa10_6(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_6(8, active0);
}
private final int jjMoveStringLiteralDfa10_6(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_6(8, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_6(9, active0);
      return 10;
   }
   switch(curChar)
   {
      case 73:
         return jjMoveStringLiteralDfa11_6(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_6(9, active0);
}
private final int jjMoveStringLiteralDfa11_6(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_6(9, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_6(10, active0);
      return 11;
   }
   switch(curChar)
   {
      case 69:
         return jjMoveStringLiteralDfa12_6(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_6(10, active0);
}
private final int jjMoveStringLiteralDfa12_6(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_6(10, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_6(11, active0);
      return 12;
   }
   switch(curChar)
   {
      case 83:
         if ((active0 & 0x4000000L) != 0L)
            return jjStartNfaWithStates_6(12, 26, 0);
         break;
      default :
         break;
   }
   return jjStartNfa_6(11, active0);
}
private final int jjMoveNfa_6(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 4;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  if ((0xffffffffffffdbffL & l) != 0L)
                  {
                     if (kind > 27)
                        kind = 27;
                     jjCheckNAdd(0);
                  }
                  else if ((0x2400L & l) != 0L)
                  {
                     if (kind > 28)
                        kind = 28;
                  }
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 0:
                  if ((0xffffffffffffdbffL & l) == 0L)
                     break;
                  kind = 27;
                  jjCheckNAdd(0);
                  break;
               case 2:
                  if (curChar == 10 && kind > 28)
                     kind = 28;
                  break;
               case 3:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 1:
               case 0:
                  kind = 27;
                  jjCheckNAdd(0);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 1:
               case 0:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 27)
                     kind = 27;
                  jjCheckNAdd(0);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 4 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjMoveStringLiteralDfa0_8()
{
   return jjMoveNfa_8(4, 0);
}
private final int jjMoveNfa_8(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 4;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 4:
                  if ((0xffffffffffffdbffL & l) != 0L)
                     jjCheckNAddStates(0, 2);
                  else if ((0x2400L & l) != 0L)
                  {
                     if (kind > 32)
                        kind = 32;
                  }
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 0:
                  if ((0xffffffffffffdbffL & l) != 0L)
                     jjCheckNAddStates(0, 2);
                  break;
               case 1:
                  if ((0x2400L & l) != 0L && kind > 32)
                     kind = 32;
                  break;
               case 2:
                  if (curChar == 10 && kind > 32)
                     kind = 32;
                  break;
               case 3:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 4:
               case 0:
                  jjCheckNAddStates(0, 2);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 4:
               case 0:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddStates(0, 2);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 4 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   0, 1, 3, 
};
public static final String[] jjstrLiteralImages = {
"", "\56", null, null, "\123\124\101\122\124\106\117\116\124", 
"\105\116\104\106\117\116\124", "\103\117\116\124\105\116\124\126\105\122\123\111\117\116", 
"\106\117\116\124\102\117\125\116\104\111\116\107\102\117\130", "\115\105\124\122\111\103\123\123\105\124", "\123\111\132\105", 
"\103\110\101\122\123", "\123\124\101\122\124\103\110\101\122", null, null, null, 
"\105\116\103\117\104\111\116\107", "\123\127\111\104\124\110", "\104\127\111\104\124\110", "\102\102\130", 
"\102\111\124\115\101\120", null, "\105\116\104\103\110\101\122", null, 
"\123\124\101\122\124\120\122\117\120\105\122\124\111\105\123", null, null, "\105\116\104\120\122\117\120\105\122\124\111\105\123", null, null, 
"\103\117\115\115\105\116\124", null, "\106\117\116\124", null, };
public static final String[] lexStateNames = {
   "DEFAULT", 
   "STARTCHARSTATE", 
   "CHARTEXTSTATE", 
   "CHARSTATE", 
   "BITMAPSTATE", 
   "PROPERTYCOUNTSTATE", 
   "PROPERTYSTATE", 
   "COMMENTSTATE", 
   "FONTSTATE", 
};
public static final int[] jjnewLexState = {
   -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 2, 3, 0, -1, -1, -1, -1, 4, -1, 0, -1, 5, -1, 
   6, 0, -1, -1, 7, 0, 8, 0, 
};
static final long[] jjtoToken = {
   0x1eebfaffbL, 
};
static final long[] jjtoSkip = {
   0x11405004L, 
};
protected SimpleCharStream input_stream;
private final int[] jjrounds = new int[5];
private final int[] jjstateSet = new int[10];
protected char curChar;
public BDFParserTokenManager(SimpleCharStream stream)
{
   if (SimpleCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}
public BDFParserTokenManager(SimpleCharStream stream, int lexState)
{
   this(stream);
   SwitchTo(lexState);
}
public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 5; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
public void SwitchTo(int lexState)
{
   if (lexState >= 9 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

public Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   switch(curLexState)
   {
     case 0:
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_0();
       break;
     case 1:
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_1();
       break;
     case 2:
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_2();
       break;
     case 3:
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_3();
       break;
     case 4:
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_4();
       break;
     case 5:
       try { input_stream.backup(0);
          while (curChar <= 32 && (0x100000000L & (1L << curChar)) != 0L)
             curChar = input_stream.BeginToken();
       }
       catch (java.io.IOException e1) { continue EOFLoop; }
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_5();
       break;
     case 6:
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_6();
       break;
     case 7:
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_7();
       break;
     case 8:
       jjmatchedKind = 0x7fffffff;
       jjmatchedPos = 0;
       curPos = jjMoveStringLiteralDfa0_8();
       break;
   }
     if (jjmatchedKind != 0x7fffffff)
     {
        if (jjmatchedPos + 1 < curPos)
           input_stream.backup(curPos - jjmatchedPos - 1);
        if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
        {
           matchedToken = jjFillToken();
       if (jjnewLexState[jjmatchedKind] != -1)
         curLexState = jjnewLexState[jjmatchedKind];
           return matchedToken;
        }
        else
        {
         if (jjnewLexState[jjmatchedKind] != -1)
           curLexState = jjnewLexState[jjmatchedKind];
           continue EOFLoop;
        }
     }
     int error_line = input_stream.getEndLine();
     int error_column = input_stream.getEndColumn();
     String error_after = null;
     boolean EOFSeen = false;
     try { input_stream.readChar(); input_stream.backup(1); }
     catch (java.io.IOException e1) {
        EOFSeen = true;
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
        if (curChar == '\n' || curChar == '\r') {
           error_line++;
           error_column = 0;
        }
        else
           error_column++;
     }
     if (!EOFSeen) {
        input_stream.backup(1);
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
     }
     throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

}
