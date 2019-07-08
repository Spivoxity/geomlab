/*
 * Boot.java
 * 
 * This file is part of GeomLab
 * Copyright (c) 2005 J. M. Spivey
 * All rights reserved
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.      
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 *    
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static funbase.FunCode.Opcode.*;

public class Boot extends geomlab.Bootstrap {
  @Override
  public void boot() {
    D("++", C(F("++", 2, (() -> {
      I(ARG, 0); I(TRAP, 8); I(MNIL); I(ARG, 1);
      I(RETURN); I(ARG, 0); I(TRAP, 44); I(MCONS);
      I(BIND, 0); I(BIND, 1); I(GLOBAL, 0); I(PREP, 2);
      I(LOCAL, 0); I(PUTARG, 0); I(FVAR, 0); I(PREP, 2);
      I(LOCAL, 1); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
      I(CALL, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN);
      I(FAIL); }),
      N(":"))));
    D("__top", C(F("__top", 0, (() -> {
      I(QUOTE, 0); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 0);
      I(QUOTE, 1); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 1);
      I(QUOTE, 2); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 2);
      I(QUOTE, 3); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 3);
      I(QUOTE, 4); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 4);
      I(QUOTE, 5); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 5);
      I(GLOBAL, 6); I(PREP, 1); I(PUSH, 0); I(PUTARG, 0);
      I(CALL, 1); I(BIND, 6); I(GLOBAL, 6); I(PREP, 1);
      I(PUSH, 0); I(PUTARG, 0); I(CALL, 1); I(BIND, 7);
      I(QUOTE, 7); I(FRAME, 5); I(LOCAL, 0); I(PUTARG, 1);
      I(LOCAL, 4); I(PUTARG, 2); I(LOCAL, 6); I(PUTARG, 3);
      I(LOCAL, 7); I(PUTARG, 4); I(CLOSURE, 5); I(BIND, 8);
      I(QUOTE, 8); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 9);
      I(QUOTE, 9); I(QUOTE, 10); I(QUOTE, 11); I(QUOTE, 12);
      I(QUOTE, 13); I(NIL); I(CONS); I(CONS);
      I(CONS); I(CONS); I(CONS); I(BIND, 10);
      I(QUOTE, 14); I(FRAME, 3); I(LOCAL, 1); I(PUTARG, 1);
      I(LOCAL, 10); I(PUTARG, 2); I(CLOSURE, 3); I(BIND, 11);
      I(QUOTE, 15); I(FRAME, 2); I(LOCAL, 6); I(PUTARG, 1);
      I(CLOSURE, 2); I(BIND, 12); I(QUOTE, 16); I(FRAME, 4);
      I(LOCAL, 12); I(PUTARG, 1); I(LOCAL, 8); I(PUTARG, 2);
      I(LOCAL, 5); I(PUTARG, 3); I(CLOSURE, 4); I(BIND, 13);
      I(QUOTE, 17); I(FRAME, 3); I(LOCAL, 12); I(PUTARG, 1);
      I(LOCAL, 8); I(PUTARG, 2); I(CLOSURE, 3); I(BIND, 14);
      I(QUOTE, 18); I(FRAME, 2); I(LOCAL, 6); I(PUTARG, 1);
      I(CLOSURE, 2); I(BIND, 15); I(QUOTE, 19); I(FRAME, 3);
      I(LOCAL, 7); I(PUTARG, 1); I(LOCAL, 13); I(PUTARG, 2);
      I(CLOSURE, 3); I(BIND, 16); I(QUOTE, 20); I(FRAME, 2);
      I(LOCAL, 13); I(PUTARG, 1); I(CLOSURE, 2); I(BIND, 17);
      I(QUOTE, 21); I(FRAME, 2); I(LOCAL, 13); I(PUTARG, 1);
      I(CLOSURE, 2); I(BIND, 18); I(QUOTE, 22); I(FRAME, 2);
      I(LOCAL, 14); I(PUTARG, 1); I(CLOSURE, 2); I(BIND, 19);
      I(QUOTE, 23); I(FRAME, 2); I(LOCAL, 19); I(PUTARG, 1);
      I(CLOSURE, 2); I(BIND, 20); I(QUOTE, 24); I(FRAME, 3);
      I(LOCAL, 12); I(PUTARG, 1); I(LOCAL, 20); I(PUTARG, 2);
      I(CLOSURE, 3); I(BIND, 21); I(QUOTE, 25); I(FRAME, 1);
      I(CLOSURE, 1); I(BIND, 22); I(QUOTE, 26); I(FRAME, 1);
      I(CLOSURE, 1); I(BIND, 23); I(QUOTE, 27); I(FRAME, 1);
      I(CLOSURE, 1); I(BIND, 24); I(GLOBAL, 6); I(PREP, 1);
      I(PUSH, 0); I(PUTARG, 0); I(CALL, 1); I(BIND, 25);
      I(QUOTE, 28); I(FRAME, 2); I(LOCAL, 25); I(PUTARG, 1);
      I(CLOSURE, 2); I(BIND, 26); I(QUOTE, 29); I(FRAME, 2);
      I(LOCAL, 26); I(PUTARG, 1); I(CLOSURE, 2); I(BIND, 27);
      I(GLOBAL, 6); I(PREP, 1); I(PUSH, 0); I(PUTARG, 0);
      I(CALL, 1); I(BIND, 28); I(QUOTE, 30); I(FRAME, 2);
      I(LOCAL, 28); I(PUTARG, 1); I(CLOSURE, 2); I(BIND, 29);
      I(GLOBAL, 6); I(PREP, 1); I(PUSH, 0); I(PUTARG, 0);
      I(CALL, 1); I(BIND, 30); I(QUOTE, 31); I(FRAME, 2);
      I(LOCAL, 30); I(PUTARG, 1); I(CLOSURE, 2); I(BIND, 31);
      I(GLOBAL, 6); I(PREP, 1); I(PUSH, 0); I(PUTARG, 0);
      I(CALL, 1); I(BIND, 32); I(QUOTE, 32); I(FRAME, 2);
      I(LOCAL, 32); I(PUTARG, 1); I(CLOSURE, 2); I(BIND, 33);
      I(QUOTE, 33); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 34);
      I(QUOTE, 34); I(FRAME, 3); I(LOCAL, 21); I(PUTARG, 1);
      I(LOCAL, 31); I(PUTARG, 2); I(CLOSURE, 3); I(BIND, 35);
      I(QUOTE, 35); I(FRAME, 10); I(LOCAL, 4); I(PUTARG, 1);
      I(LOCAL, 31); I(PUTARG, 2); I(LOCAL, 17); I(PUTARG, 3);
      I(LOCAL, 13); I(PUTARG, 4); I(LOCAL, 35); I(PUTARG, 5);
      I(LOCAL, 18); I(PUTARG, 6); I(LOCAL, 12); I(PUTARG, 7);
      I(LOCAL, 16); I(PUTARG, 8); I(LOCAL, 15); I(PUTARG, 9);
      I(CLOSURE, 10); I(BIND, 36); I(QUOTE, 36); I(FRAME, 3);
      I(LOCAL, 36); I(PUTARG, 1); I(LOCAL, 14); I(PUTARG, 2);
      I(CLOSURE, 3); I(BIND, 37); I(QUOTE, 37); I(FRAME, 4);
      I(LOCAL, 16); I(PUTARG, 1); I(LOCAL, 14); I(PUTARG, 2);
      I(LOCAL, 37); I(PUTARG, 3); I(CLOSURE, 4); I(BIND, 38);
      I(QUOTE, 38); I(FRAME, 3); I(LOCAL, 18); I(PUTARG, 1);
      I(LOCAL, 35); I(PUTARG, 2); I(CLOSURE, 3); I(BIND, 39);
      I(QUOTE, 39); I(FRAME, 3); I(LOCAL, 21); I(PUTARG, 1);
      I(LOCAL, 27); I(PUTARG, 2); I(CLOSURE, 3); I(BIND, 40);
      I(QUOTE, 40); I(FRAME, 2); I(LOCAL, 24); I(PUTARG, 1);
      I(CLOSURE, 2); I(BIND, 41); I(QUOTE, 41); I(FRAME, 2);
      I(LOCAL, 41); I(PUTARG, 1); I(CLOSURE, 2); I(BIND, 42);
      I(QUOTE, 42); I(FRAME, 4); I(LOCAL, 31); I(PUTARG, 1);
      I(LOCAL, 13); I(PUTARG, 2); I(LOCAL, 27); I(PUTARG, 3);
      I(CLOSURE, 4); I(BIND, 43); I(QUOTE, 43); I(FRAME, 5);
      I(LOCAL, 15); I(PUTARG, 1); I(LOCAL, 13); I(PUTARG, 2);
      I(LOCAL, 27); I(PUTARG, 3); I(LOCAL, 43); I(PUTARG, 4);
      I(CLOSURE, 5); I(BIND, 44); I(QUOTE, 44); I(FRAME, 9);
      I(LOCAL, 12); I(PUTARG, 1); I(LOCAL, 27); I(PUTARG, 2);
      I(LOCAL, 44); I(PUTARG, 3); I(LOCAL, 42); I(PUTARG, 4);
      I(LOCAL, 23); I(PUTARG, 5); I(LOCAL, 13); I(PUTARG, 6);
      I(LOCAL, 19); I(PUTARG, 7); I(LOCAL, 15); I(PUTARG, 8);
      I(CLOSURE, 9); I(BIND, 45); I(QUOTE, 45); I(FRAME, 9);
      I(LOCAL, 6); I(PUTARG, 1); I(LOCAL, 11); I(PUTARG, 2);
      I(LOCAL, 26); I(PUTARG, 3); I(LOCAL, 16); I(PUTARG, 4);
      I(LOCAL, 9); I(PUTARG, 5); I(LOCAL, 12); I(PUTARG, 6);
      I(LOCAL, 23); I(PUTARG, 7); I(LOCAL, 29); I(PUTARG, 8);
      I(CLOSURE, 9); I(BIND, 46); I(QUOTE, 46); I(FRAME, 4);
      I(LOCAL, 12); I(PUTARG, 1); I(LOCAL, 18); I(PUTARG, 2);
      I(LOCAL, 40); I(PUTARG, 3); I(CLOSURE, 4); I(BIND, 47);
      I(QUOTE, 47); I(FRAME, 8); I(LOCAL, 4); I(PUTARG, 1);
      I(LOCAL, 45); I(PUTARG, 2); I(LOCAL, 46); I(PUTARG, 3);
      I(LOCAL, 17); I(PUTARG, 4); I(LOCAL, 47); I(PUTARG, 5);
      I(LOCAL, 16); I(PUTARG, 6); I(LOCAL, 15); I(PUTARG, 7);
      I(CLOSURE, 8); I(BIND, 48); I(QUOTE, 48); I(FRAME, 7);
      I(LOCAL, 48); I(PUTARG, 1); I(LOCAL, 13); I(PUTARG, 2);
      I(LOCAL, 22); I(PUTARG, 3); I(LOCAL, 12); I(PUTARG, 4);
      I(LOCAL, 16); I(PUTARG, 5); I(LOCAL, 15); I(PUTARG, 6);
      I(CLOSURE, 7); I(BIND, 49); I(QUOTE, 49); I(FRAME, 2);
      I(LOCAL, 23); I(PUTARG, 1); I(CLOSURE, 2); I(BIND, 50);
      I(QUOTE, 50); I(FRAME, 11); I(LOCAL, 50); I(PUTARG, 1);
      I(LOCAL, 29); I(PUTARG, 2); I(LOCAL, 23); I(PUTARG, 3);
      I(LOCAL, 12); I(PUTARG, 4); I(LOCAL, 13); I(PUTARG, 5);
      I(LOCAL, 9); I(PUTARG, 6); I(LOCAL, 7); I(PUTARG, 7);
      I(LOCAL, 11); I(PUTARG, 8); I(LOCAL, 6); I(PUTARG, 9);
      I(LOCAL, 49); I(PUTARG, 10); I(CLOSURE, 11); I(BIND, 51);
      I(QUOTE, 51); I(FRAME, 4); I(LOCAL, 14); I(PUTARG, 1);
      I(LOCAL, 13); I(PUTARG, 2); I(LOCAL, 29); I(PUTARG, 3);
      I(CLOSURE, 4); I(BIND, 52); I(QUOTE, 52); I(FRAME, 8);
      I(LOCAL, 14); I(PUTARG, 1); I(LOCAL, 52); I(PUTARG, 2);
      I(LOCAL, 39); I(PUTARG, 3); I(LOCAL, 27); I(PUTARG, 4);
      I(LOCAL, 33); I(PUTARG, 5); I(LOCAL, 13); I(PUTARG, 6);
      I(LOCAL, 15); I(PUTARG, 7); I(CLOSURE, 8); I(BIND, 53);
      I(QUOTE, 53); I(FRAME, 5); I(LOCAL, 11); I(PUTARG, 1);
      I(LOCAL, 6); I(PUTARG, 2); I(LOCAL, 12); I(PUTARG, 3);
      I(LOCAL, 16); I(PUTARG, 4); I(CLOSURE, 5); I(BIND, 54);
      I(QUOTE, 54); I(FRAME, 4); I(LOCAL, 13); I(PUTARG, 1);
      I(LOCAL, 27); I(PUTARG, 2); I(LOCAL, 14); I(PUTARG, 3);
      I(CLOSURE, 4); I(BIND, 55); I(QUOTE, 55); I(FRAME, 9);
      I(LOCAL, 7); I(PUTARG, 1); I(LOCAL, 11); I(PUTARG, 2);
      I(LOCAL, 6); I(PUTARG, 3); I(LOCAL, 12); I(PUTARG, 4);
      I(LOCAL, 4); I(PUTARG, 5); I(LOCAL, 13); I(PUTARG, 6);
      I(LOCAL, 39); I(PUTARG, 7); I(LOCAL, 55); I(PUTARG, 8);
      I(CLOSURE, 9); I(BIND, 56); I(QUOTE, 56); I(FRAME, 9);
      I(LOCAL, 54); I(PUTARG, 1); I(LOCAL, 12); I(PUTARG, 2);
      I(LOCAL, 13); I(PUTARG, 3); I(LOCAL, 27); I(PUTARG, 4);
      I(LOCAL, 39); I(PUTARG, 5); I(LOCAL, 55); I(PUTARG, 6);
      I(LOCAL, 19); I(PUTARG, 7); I(LOCAL, 56); I(PUTARG, 8);
      I(CLOSURE, 9); I(BIND, 57); I(QUOTE, 57); I(FRAME, 6);
      I(LOCAL, 12); I(PUTARG, 1); I(LOCAL, 14); I(PUTARG, 2);
      I(LOCAL, 33); I(PUTARG, 3); I(LOCAL, 27); I(PUTARG, 4);
      I(LOCAL, 4); I(PUTARG, 5); I(CLOSURE, 6); I(BIND, 58);
      I(GLOBAL, 58); I(PREP, 2); I(LOCAL, 25); I(PUTARG, 0);
      I(LOCAL, 53); I(PUTARG, 1); I(CALL, 2); I(POP);
      I(GLOBAL, 58); I(PREP, 2); I(LOCAL, 28); I(PUTARG, 0);
      I(LOCAL, 51); I(PUTARG, 1); I(CALL, 2); I(POP);
      I(GLOBAL, 58); I(PREP, 2); I(LOCAL, 30); I(PUTARG, 0);
      I(LOCAL, 38); I(PUTARG, 1); I(CALL, 2); I(POP);
      I(GLOBAL, 58); I(PREP, 2); I(LOCAL, 32); I(PUTARG, 0);
      I(LOCAL, 57); I(PUTARG, 1); I(CALL, 2); I(POP);
      I(QUOTE, 59); I(FRAME, 3); I(LOCAL, 8); I(PUTARG, 1);
      I(LOCAL, 58); I(PUTARG, 2); I(CLOSURE, 3); I(BIND, 59);
      I(QUOTE, 60); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 60);
      I(QUOTE, 61); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 61);
      I(QUOTE, 62); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 62);
      I(QUOTE, 63); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 63);
      I(QUOTE, 64); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 64);
      I(QUOTE, 65); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 65);
      I(QUOTE, 66); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 66);
      I(QUOTE, 67); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 67);
      I(QUOTE, 68); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 68);
      I(QUOTE, 69); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 69);
      I(QUOTE, 70); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 70);
      I(QUOTE, 71); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 71);
      I(QUOTE, 72); I(FRAME, 4); I(LOCAL, 68); I(PUTARG, 1);
      I(LOCAL, 70); I(PUTARG, 2); I(LOCAL, 69); I(PUTARG, 3);
      I(CLOSURE, 4); I(BIND, 72); I(QUOTE, 73); I(FRAME, 3);
      I(LOCAL, 71); I(PUTARG, 1); I(LOCAL, 69); I(PUTARG, 2);
      I(CLOSURE, 3); I(BIND, 73); I(QUOTE, 74); I(FRAME, 1);
      I(CLOSURE, 1); I(BIND, 74); I(QUOTE, 75); I(FRAME, 3);
      I(LOCAL, 65); I(PUTARG, 1); I(LOCAL, 62); I(PUTARG, 2);
      I(CLOSURE, 3); I(BIND, 75); I(QUOTE, 76); I(FRAME, 3);
      I(LOCAL, 65); I(PUTARG, 1); I(LOCAL, 62); I(PUTARG, 2);
      I(CLOSURE, 3); I(BIND, 76); I(QUOTE, 77); I(FRAME, 1);
      I(CLOSURE, 1); I(BIND, 77); I(GLOBAL, 6); I(PREP, 1);
      I(PUSH, 0); I(PUTARG, 0); I(CALL, 1); I(BIND, 78);
      I(QUOTE, 78); I(FRAME, 2); I(LOCAL, 78); I(PUTARG, 1);
      I(CLOSURE, 2); I(BIND, 79); I(QUOTE, 79); I(FRAME, 5);
      I(LOCAL, 70); I(PUTARG, 1); I(LOCAL, 74); I(PUTARG, 2);
      I(LOCAL, 65); I(PUTARG, 3); I(LOCAL, 62); I(PUTARG, 4);
      I(CLOSURE, 5); I(BIND, 80); I(QUOTE, 80); I(FRAME, 1);
      I(CLOSURE, 1); I(BIND, 81); I(GLOBAL, 6); I(PREP, 1);
      I(PUSH, 0); I(PUTARG, 0); I(CALL, 1); I(BIND, 82);
      I(QUOTE, 81); I(FRAME, 2); I(LOCAL, 82); I(PUTARG, 1);
      I(CLOSURE, 2); I(BIND, 83); I(QUOTE, 82); I(FRAME, 1);
      I(CLOSURE, 1); I(BIND, 84); I(QUOTE, 83); I(FRAME, 1);
      I(CLOSURE, 1); I(BIND, 85); I(QUOTE, 84); I(FRAME, 2);
      I(LOCAL, 79); I(PUTARG, 1); I(CLOSURE, 2); I(BIND, 86);
      I(QUOTE, 85); I(FRAME, 11); I(LOCAL, 79); I(PUTARG, 1);
      I(LOCAL, 86); I(PUTARG, 2); I(LOCAL, 85); I(PUTARG, 3);
      I(LOCAL, 75); I(PUTARG, 4); I(LOCAL, 80); I(PUTARG, 5);
      I(LOCAL, 72); I(PUTARG, 6); I(LOCAL, 84); I(PUTARG, 7);
      I(LOCAL, 2); I(PUTARG, 8); I(LOCAL, 83); I(PUTARG, 9);
      I(LOCAL, 34); I(PUTARG, 10); I(CLOSURE, 11); I(BIND, 87);
      I(QUOTE, 86); I(FRAME, 6); I(LOCAL, 75); I(PUTARG, 1);
      I(LOCAL, 70); I(PUTARG, 2); I(LOCAL, 85); I(PUTARG, 3);
      I(LOCAL, 84); I(PUTARG, 4); I(LOCAL, 87); I(PUTARG, 5);
      I(CLOSURE, 6); I(BIND, 88); I(QUOTE, 87); I(FRAME, 2);
      I(LOCAL, 88); I(PUTARG, 1); I(CLOSURE, 2); I(BIND, 89);
      I(QUOTE, 88); I(FRAME, 5); I(LOCAL, 89); I(PUTARG, 1);
      I(LOCAL, 83); I(PUTARG, 2); I(LOCAL, 77); I(PUTARG, 3);
      I(LOCAL, 79); I(PUTARG, 4); I(CLOSURE, 5); I(BIND, 90);
      I(QUOTE, 89); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 91);
      I(QUOTE, 90); I(FRAME, 4); I(LOCAL, 90); I(PUTARG, 1);
      I(LOCAL, 81); I(PUTARG, 2); I(LOCAL, 91); I(PUTARG, 3);
      I(CLOSURE, 4); I(BIND, 92); I(QUOTE, 91); I(FRAME, 9);
      I(LOCAL, 64); I(PUTARG, 1); I(LOCAL, 60); I(PUTARG, 2);
      I(LOCAL, 92); I(PUTARG, 3); I(LOCAL, 67); I(PUTARG, 4);
      I(LOCAL, 0); I(PUTARG, 5); I(LOCAL, 61); I(PUTARG, 6);
      I(LOCAL, 2); I(PUTARG, 7); I(LOCAL, 80); I(PUTARG, 8);
      I(CLOSURE, 9); I(BIND, 93); I(QUOTE, 92); I(FRAME, 1);
      I(CLOSURE, 1); I(BIND, 94); I(QUOTE, 93); I(FRAME, 13);
      I(LOCAL, 94); I(PUTARG, 1); I(LOCAL, 80); I(PUTARG, 2);
      I(LOCAL, 76); I(PUTARG, 3); I(LOCAL, 66); I(PUTARG, 4);
      I(LOCAL, 2); I(PUTARG, 5); I(LOCAL, 79); I(PUTARG, 6);
      I(LOCAL, 70); I(PUTARG, 7); I(LOCAL, 71); I(PUTARG, 8);
      I(LOCAL, 72); I(PUTARG, 9); I(LOCAL, 73); I(PUTARG, 10);
      I(LOCAL, 93); I(PUTARG, 11); I(LOCAL, 34); I(PUTARG, 12);
      I(CLOSURE, 13); I(BIND, 95); I(GLOBAL, 58); I(PREP, 2);
      I(LOCAL, 82); I(PUTARG, 0); I(LOCAL, 95); I(PUTARG, 1);
      I(CALL, 2); I(POP); I(QUOTE, 94); I(FRAME, 6);
      I(LOCAL, 60); I(PUTARG, 1); I(LOCAL, 92); I(PUTARG, 2);
      I(LOCAL, 64); I(PUTARG, 3); I(LOCAL, 0); I(PUTARG, 4);
      I(LOCAL, 61); I(PUTARG, 5); I(CLOSURE, 6); I(BIND, 96);
      I(QUOTE, 95); I(FRAME, 5); I(LOCAL, 62); I(PUTARG, 1);
      I(LOCAL, 70); I(PUTARG, 2); I(LOCAL, 71); I(PUTARG, 3);
      I(LOCAL, 96); I(PUTARG, 4); I(CLOSURE, 5); I(BIND, 97);
      I(QUOTE, 96); I(FRAME, 4); I(LOCAL, 97); I(PUTARG, 1);
      I(LOCAL, 63); I(PUTARG, 2); I(LOCAL, 96); I(PUTARG, 3);
      I(CLOSURE, 4); I(BIND, 98); I(GLOBAL, 97); I(PREP, 1);
      I(QUOTE, 98); I(PUTARG, 0); I(CALL, 1); I(JFALSE, 1760);
      I(NIL); I(JUMP, 1778); I(GLOBAL, 99); I(PREP, 2);
      I(QUOTE, 98); I(PUTARG, 0); I(GLOBAL, 100); I(PREP, 0);
      I(CALL, 0); I(PUTARG, 1); I(CALL, 2); I(POP);
      I(QUOTE, 101); I(FRAME, 6); I(LOCAL, 59); I(PUTARG, 1);
      I(LOCAL, 0); I(PUTARG, 2); I(LOCAL, 78); I(PUTARG, 3);
      I(LOCAL, 97); I(PUTARG, 4); I(LOCAL, 98); I(PUTARG, 5);
      I(CLOSURE, 6); I(RETURN); }),
      F("debug", 2, (() -> {
        I(GLOBAL, 0); I(PREP, 2); I(GLOBAL, 1); I(PREP, 0);
        I(CALL, 0); I(PUTARG, 0); I(ARG, 0); I(PUTARG, 1);
        I(CALL, 2); I(JFALSE, 31); I(GLOBAL, 2); I(PREP, 1);
        I(ARG, 1); I(PUTARG, 0); I(CALL, 1); I(RETURN);
        I(NIL); I(RETURN); }),
        N(">"), N("_debug"), N("_print")),
      F("member", 2, (() -> {
        I(ARG, 1); I(TRAP, 8); I(MNIL); I(GLOBAL, 0);
        I(RETURN); I(ARG, 1); I(TRAP, 51); I(MCONS);
        I(BIND, 0); I(BIND, 1); I(GLOBAL, 1); I(PREP, 2);
        I(ARG, 0); I(PUTARG, 0); I(LOCAL, 0); I(PUTARG, 1);
        I(CALL, 2); I(JFALSE, 36); I(QUOTE, 2); I(RETURN);
        I(FVAR, 0); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
        I(LOCAL, 1); I(PUTARG, 1); I(CALL, 2); I(RETURN);
        I(FAIL); }),
        N("false"), N("="), B(true)),
      F("number", 2, (() -> {
        I(ARG, 1); I(TRAP, 7); I(MNIL); I(NIL);
        I(RETURN); I(ARG, 1); I(TRAP, 60); I(MCONS);
        I(BIND, 0); I(BIND, 1); I(GLOBAL, 0); I(PREP, 2);
        I(ARG, 0); I(LOCAL, 0); I(NIL); I(CONS);
        I(CONS); I(PUTARG, 0); I(FVAR, 0); I(PREP, 2);
        I(GLOBAL, 1); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
        I(PUSH, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0);
        I(LOCAL, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1);
        I(CALL, 2); I(RETURN); I(FAIL); }),
        N(":"), N("+")),
      F("max", 2, (() -> {
        I(GLOBAL, 0); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
        I(ARG, 1); I(PUTARG, 1); I(CALL, 2); I(JFALSE, 19);
        I(ARG, 0); I(RETURN); I(ARG, 1); I(RETURN); }),
        N(">")),
      F("synerror", 1, (() -> {
        I(GLOBAL, 0); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
        I(NIL); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
        N("_synerror")),
      F("describe", 1, (() -> {
        I(ARG, 0); I(TRAP, 10); I(QUOTE, 0); I(MEQ);
        I(QUOTE, 1); I(RETURN); I(ARG, 0); I(TRAP, 20);
        I(QUOTE, 2); I(MEQ); I(QUOTE, 3); I(RETURN);
        I(ARG, 0); I(TRAP, 30); I(QUOTE, 4); I(MEQ);
        I(QUOTE, 5); I(RETURN); I(ARG, 0); I(TRAP, 40);
        I(QUOTE, 6); I(MEQ); I(QUOTE, 7); I(RETURN);
        I(ARG, 0); I(TRAP, 50); I(QUOTE, 8); I(MEQ);
        I(QUOTE, 9); I(RETURN); I(ARG, 0); I(TRAP, 60);
        I(QUOTE, 10); I(MEQ); I(QUOTE, 11); I(RETURN);
        I(ARG, 0); I(TRAP, 70); I(QUOTE, 12); I(MEQ);
        I(QUOTE, 13); I(RETURN); I(ARG, 0); I(TRAP, 80);
        I(QUOTE, 14); I(MEQ); I(QUOTE, 15); I(RETURN);
        I(ARG, 0); I(TRAP, 90); I(QUOTE, 16); I(MEQ);
        I(QUOTE, 17); I(RETURN); I(ARG, 0); I(TRAP, 100);
        I(QUOTE, 18); I(MEQ); I(QUOTE, 19); I(RETURN);
        I(ARG, 0); I(TRAP, 110); I(QUOTE, 20); I(MEQ);
        I(QUOTE, 21); I(RETURN); I(ARG, 0); I(TRAP, 120);
        I(QUOTE, 22); I(MEQ); I(QUOTE, 23); I(RETURN);
        I(ARG, 0); I(TRAP, 130); I(QUOTE, 24); I(MEQ);
        I(QUOTE, 25); I(RETURN); I(ARG, 0); I(TRAP, 140);
        I(QUOTE, 26); I(MEQ); I(QUOTE, 27); I(RETURN);
        I(ARG, 0); I(TRAP, 150); I(QUOTE, 28); I(MEQ);
        I(QUOTE, 29); I(RETURN); I(ARG, 0); I(TRAP, 160);
        I(QUOTE, 30); I(MEQ); I(QUOTE, 31); I(RETURN);
        I(ARG, 0); I(TRAP, 170); I(QUOTE, 32); I(MEQ);
        I(QUOTE, 33); I(RETURN); I(ARG, 0); I(TRAP, 180);
        I(QUOTE, 34); I(MEQ); I(QUOTE, 35); I(RETURN);
        I(ARG, 0); I(TRAP, 190); I(QUOTE, 36); I(MEQ);
        I(QUOTE, 37); I(RETURN); I(GLOBAL, 38); I(PREP, 2);
        I(GLOBAL, 38); I(PREP, 2); I(QUOTE, 39); I(PUTARG, 0);
        I(GLOBAL, 40); I(PREP, 1); I(ARG, 0); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0);
        I(QUOTE, 39); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
        N("ident"), S("an identifier"), N("number"), S("a number"),
        N("atom"), S("an atom"), N("lpar"), S("'('"),
        N("rpar"), S("')'"), N("comma"), S("','"),
        N("semi"), S("';'"), N("bra"), S("'['"),
        N("ket"), S("']'"), N("vbar"), S("'|'"),
        N(">>"), S("'>>'"), N(".."), S("'..'"),
        N("string"), S("a string constant"), N("binop"), S("a binary operator"),
        N("monop"), S("a unary operator"), N("lbrace"), S("'{'"),
        N("rbrace"), S("'}'"), N("eol"), S("end of line"),
        N("eof"), S("end of input"), N("^"), S("'"),
        N("_spelling")),
      N("_new"), 
      F("scan", 0, (() -> {
        I(GLOBAL, 0); I(PREP, 0); I(CALL, 0); I(BIND, 0);
        I(FVAR, 1); I(PREP, 2); I(PUSH, 5); I(PUTARG, 0);
        I(LOCAL, 0); I(PUTARG, 1); I(CALL, 2); I(POP);
        I(QUOTE, 1); I(FRAME, 4); I(LOCAL, 0); I(PUTARG, 1);
        I(FVAR, 3); I(PUTARG, 2); I(FVAR, 2); I(PUTARG, 3);
        I(CLOSURE, 4); I(BIND, 1); I(LOCAL, 1); I(PREP, 1);
        I(GLOBAL, 2); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 0); I(CALL, 1); I(POP);
        I(GLOBAL, 3); I(PREP, 2); I(FVAR, 4); I(PUTARG, 0);
        I(GLOBAL, 4); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
        N("_scan"), 
        F("case", 1, (() -> {
          I(ARG, 0); I(TRAP, 73); I(QUOTE, 0); I(MEQ);
          I(GLOBAL, 1); I(PREP, 2); I(GLOBAL, 2); I(PUTARG, 0);
          I(GLOBAL, 3); I(PREP, 1); I(FVAR, 1); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 1); I(CALL, 2); I(BIND, 0);
          I(GLOBAL, 4); I(PREP, 2); I(FVAR, 2); I(PUTARG, 0);
          I(GLOBAL, 5); I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0);
          I(NIL); I(PUTARG, 1); I(CALL, 2); I(JFALSE, 66);
          I(GLOBAL, 6); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
          I(CALL, 1); I(JUMP, 68); I(QUOTE, 0); I(PUTARG, 1);
          I(CALL, 2); I(RETURN); I(ARG, 0); I(TRAP, 153);
          I(QUOTE, 7); I(MEQ); I(GLOBAL, 1); I(PREP, 2);
          I(GLOBAL, 2); I(PUTARG, 0); I(GLOBAL, 3); I(PREP, 1);
          I(FVAR, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1);
          I(CALL, 2); I(BIND, 0); I(GLOBAL, 5); I(PREP, 2);
          I(LOCAL, 0); I(PUTARG, 0); I(NIL); I(PUTARG, 1);
          I(CALL, 2); I(JFALSE, 142); I(GLOBAL, 4); I(PREP, 2);
          I(FVAR, 2); I(PUTARG, 0); I(GLOBAL, 6); I(PREP, 1);
          I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1);
          I(CALL, 2); I(RETURN); I(FVAR, 3); I(PREP, 1);
          I(QUOTE, 8); I(PUTARG, 0); I(CALL, 1); I(RETURN);
          I(GLOBAL, 4); I(PREP, 2); I(FVAR, 2); I(PUTARG, 0);
          I(ARG, 0); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
          N("ident"), N("_lookup"), N("_syntax"), N("_snd"),
          N("_set"), N("<>"), N("_fst"), N("op"),
          S("#badtok")),
        N("_fst"), N("_set"), N("_snd")),
      F("_priority", 1, (() -> {
        I(GLOBAL, 0); I(PREP, 1); I(GLOBAL, 1); I(PREP, 2);
        I(GLOBAL, 2); I(PUTARG, 0); I(ARG, 0); I(PUTARG, 1);
        I(CALL, 2); I(PUTARG, 0); I(CALL, 1); I(RETURN); }),
        N("_snd"), N("_lookup"), N("_syntax")),
      N("binop"), N("="), N("-"), N("+"),
      N(":"), 
      F("isbinop", 1, (() -> {
        I(FVAR, 1); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
        I(FVAR, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN); })),
      F("see", 1, (() -> {
        I(GLOBAL, 0); I(PREP, 2); I(GLOBAL, 1); I(PREP, 1);
        I(FVAR, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
        I(ARG, 0); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
        N("="), N("_get")),
      F("eat", 1, (() -> {
        I(FVAR, 1); I(PREP, 1); I(ARG, 0); I(PUTARG, 0);
        I(CALL, 1); I(JFALSE, 19); I(FVAR, 2); I(PREP, 0);
        I(CALL, 0); I(RETURN); I(GLOBAL, 0); I(PREP, 2);
        I(QUOTE, 1); I(PUTARG, 0); I(FVAR, 3); I(PREP, 1);
        I(ARG, 0); I(PUTARG, 0); I(CALL, 1); I(NIL);
        I(CONS); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
        N("_synerror"), S("#eat")),
      F("can_eat", 1, (() -> {
        I(FVAR, 1); I(PREP, 1); I(ARG, 0); I(PUTARG, 0);
        I(CALL, 1); I(JFALSE, 22); I(FVAR, 2); I(PREP, 0);
        I(CALL, 0); I(POP); I(GLOBAL, 0); I(RETURN);
        I(GLOBAL, 1); I(RETURN); }),
        N("true"), N("false")),
      F("whichever", 1, (() -> {
        I(ARG, 0); I(PREP, 1); I(GLOBAL, 0); I(PREP, 1);
        I(FVAR, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
        I(CALL, 1); I(RETURN); }),
        N("_get")),
      F("p_sym", 1, (() -> {
        I(GLOBAL, 0); I(PREP, 1); I(FVAR, 1); I(PUTARG, 0);
        I(CALL, 1); I(BIND, 0); I(FVAR, 2); I(PREP, 1);
        I(ARG, 0); I(PUTARG, 0); I(CALL, 1); I(POP);
        I(LOCAL, 0); I(RETURN); }),
        N("_get")),
      F("brack", 3, (() -> {
        I(FVAR, 1); I(PREP, 1); I(ARG, 0); I(PUTARG, 0);
        I(CALL, 1); I(POP); I(ARG, 1); I(PREP, 0);
        I(CALL, 0); I(BIND, 0); I(FVAR, 1); I(PREP, 1);
        I(ARG, 2); I(PUTARG, 0); I(CALL, 1); I(POP);
        I(LOCAL, 0); I(RETURN); })),
      F("brack1", 3, (() -> {
        I(FVAR, 1); I(PREP, 1); I(ARG, 0); I(PUTARG, 0);
        I(CALL, 1); I(POP); I(ARG, 1); I(PREP, 1);
        I(ARG, 2); I(PUTARG, 0); I(CALL, 1); I(BIND, 0);
        I(FVAR, 1); I(PREP, 1); I(ARG, 2); I(PUTARG, 0);
        I(CALL, 1); I(POP); I(LOCAL, 0); I(RETURN); })),
      F("p_tail", 2, (() -> {
        I(FVAR, 1); I(PREP, 1); I(ARG, 1); I(PUTARG, 0);
        I(CALL, 1); I(JFALSE, 47); I(ARG, 0); I(PREP, 0);
        I(CALL, 0); I(BIND, 0); I(GLOBAL, 0); I(PREP, 2);
        I(LOCAL, 0); I(PUTARG, 0); I(FVAR, 0); I(PREP, 2);
        I(ARG, 0); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(CALL, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN);
        I(NIL); I(RETURN); }),
        N(":")),
      F("p_list1", 1, (() -> {
        I(ARG, 0); I(PREP, 0); I(CALL, 0); I(BIND, 0);
        I(GLOBAL, 0); I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0);
        I(FVAR, 1); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
        I(QUOTE, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1);
        I(CALL, 2); I(RETURN); }),
        N(":"), N("comma")),
      F("p_list", 2, (() -> {
        I(FVAR, 1); I(PREP, 1); I(ARG, 1); I(PUTARG, 0);
        I(CALL, 1); I(JFALSE, 16); I(QUOTE, 0); I(JUMP, 26);
        I(FVAR, 1); I(PREP, 1); I(QUOTE, 1); I(PUTARG, 0);
        I(CALL, 1); I(JFALSE, 30); I(NIL); I(RETURN);
        I(FVAR, 2); I(PREP, 1); I(ARG, 0); I(PUTARG, 0);
        I(CALL, 1); I(RETURN); }),
        B(true), N("eof")),
      F("apply1", 2, (() -> {
        I(QUOTE, 0); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
        I(ARG, 1); I(NIL); I(CONS); I(PUTARG, 1);
        I(CALL, 2); I(RETURN); }),
        N("apply")),
      F("apply2", 3, (() -> {
        I(QUOTE, 0); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
        I(ARG, 1); I(ARG, 2); I(NIL); I(CONS);
        I(CONS); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
        N("apply")),
      F("apply3", 4, (() -> {
        I(QUOTE, 0); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
        I(ARG, 1); I(ARG, 2); I(ARG, 3); I(NIL);
        I(CONS); I(CONS); I(CONS); I(PUTARG, 1);
        I(CALL, 2); I(RETURN); }),
        N("apply")),
      F("p_expr0", 1, (() -> {
        I(GLOBAL, 0); I(PREP, 1); I(FVAR, 1); I(PUTARG, 0);
        I(CALL, 1); I(BIND, 0); I(LOCAL, 0); I(PREP, 1);
        I(ARG, 0); I(PUTARG, 0); I(CALL, 1); I(RETURN); }),
        N("_get")),
      F("p_expr", 0, (() -> {
        I(FVAR, 1); I(PREP, 1); I(GLOBAL, 0); I(PUTARG, 0);
        I(CALL, 1); I(RETURN); }),
        N("false")),
      F("p_term", 2, (() -> {
        I(GLOBAL, 0); I(PREP, 1); I(FVAR, 1); I(PUTARG, 0);
        I(CALL, 1); I(BIND, 0); I(LOCAL, 0); I(PREP, 2);
        I(ARG, 0); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(CALL, 2); I(RETURN); }),
        N("_get")),
      F("p_patt", 0, (() -> {
        I(GLOBAL, 0); I(PREP, 1); I(FVAR, 1); I(PUTARG, 0);
        I(CALL, 1); I(BIND, 0); I(LOCAL, 0); I(PREP, 0);
        I(CALL, 0); I(RETURN); }),
        N("_get")),
      F("p_defn", 0, (() -> {
        I(GLOBAL, 0); I(PREP, 1); I(FVAR, 1); I(PUTARG, 0);
        I(CALL, 1); I(BIND, 0); I(LOCAL, 0); I(PREP, 0);
        I(CALL, 0); I(RETURN); }),
        N("_get")),
      F("listify", 1, (() -> {
        I(GLOBAL, 0); I(PREP, 3); I(QUOTE, 1); I(PUTARG, 0);
        I(QUOTE, 2); I(PUTARG, 1); I(ARG, 0); I(PUTARG, 2);
        I(CALL, 3); I(RETURN); }),
        N("foldr"), N("cons"), N("nil")),
      F("p_patts", 1, (() -> {
        I(FVAR, 1); I(PREP, 2); I(FVAR, 2); I(PUTARG, 0);
        I(ARG, 0); I(PUTARG, 1); I(CALL, 2); I(RETURN); })),
      F("p_patprim", 0, (() -> {
        I(QUOTE, 0); I(FRAME, 9); I(FVAR, 8); I(PUTARG, 1);
        I(FVAR, 7); I(PUTARG, 2); I(FVAR, 6); I(PUTARG, 3);
        I(FVAR, 5); I(PUTARG, 4); I(FVAR, 4); I(PUTARG, 5);
        I(FVAR, 3); I(PUTARG, 6); I(FVAR, 2); I(PUTARG, 7);
        I(FVAR, 1); I(PUTARG, 8); I(CLOSURE, 9); I(BIND, 0);
        I(FVAR, 9); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
        I(CALL, 1); I(RETURN); }),
        F("case", 1, (() -> {
          I(ARG, 0); I(TRAP, 118); I(QUOTE, 0); I(MEQ);
          I(FVAR, 1); I(PREP, 1); I(QUOTE, 0); I(PUTARG, 0);
          I(CALL, 1); I(BIND, 0); I(GLOBAL, 1); I(PREP, 1);
          I(FVAR, 2); I(PREP, 1); I(QUOTE, 2); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 0); I(CALL, 1); I(JFALSE, 50);
          I(QUOTE, 3); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
          I(CALL, 1); I(RETURN); I(GLOBAL, 4); I(PREP, 1);
          I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(JFALSE, 101);
          I(QUOTE, 5); I(PREP, 2); I(QUOTE, 3); I(PREP, 1);
          I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
          I(FVAR, 3); I(PREP, 3); I(QUOTE, 2); I(PUTARG, 0);
          I(FVAR, 4); I(PUTARG, 1); I(QUOTE, 6); I(PUTARG, 2);
          I(CALL, 3); I(PUTARG, 1); I(CALL, 2); I(RETURN);
          I(GLOBAL, 7); I(PREP, 2); I(QUOTE, 8); I(PUTARG, 0);
          I(LOCAL, 0); I(NIL); I(CONS); I(PUTARG, 1);
          I(CALL, 2); I(RETURN); I(ARG, 0); I(TRAP, 207);
          I(QUOTE, 9); I(MEQ); I(FVAR, 1); I(PREP, 1);
          I(QUOTE, 9); I(PUTARG, 0); I(CALL, 1); I(BIND, 0);
          I(GLOBAL, 1); I(PREP, 1); I(FVAR, 2); I(PREP, 1);
          I(QUOTE, 2); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
          I(CALL, 1); I(JFALSE, 168); I(QUOTE, 10); I(PREP, 1);
          I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(RETURN);
          I(QUOTE, 5); I(PREP, 2); I(QUOTE, 10); I(PREP, 1);
          I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
          I(FVAR, 3); I(PREP, 3); I(QUOTE, 2); I(PUTARG, 0);
          I(FVAR, 4); I(PUTARG, 1); I(QUOTE, 6); I(PUTARG, 2);
          I(CALL, 3); I(PUTARG, 1); I(CALL, 2); I(RETURN);
          I(ARG, 0); I(TRAP, 228); I(QUOTE, 11); I(MEQ);
          I(FVAR, 5); I(PREP, 1); I(QUOTE, 11); I(PUTARG, 0);
          I(CALL, 1); I(POP); I(QUOTE, 12); I(RETURN);
          I(ARG, 0); I(TRAP, 254); I(QUOTE, 13); I(MEQ);
          I(QUOTE, 10); I(PREP, 1); I(FVAR, 1); I(PREP, 1);
          I(QUOTE, 13); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
          I(CALL, 1); I(RETURN); I(ARG, 0); I(TRAP, 299);
          I(QUOTE, 14); I(MEQ); I(FVAR, 5); I(PREP, 1);
          I(QUOTE, 14); I(PUTARG, 0); I(CALL, 1); I(POP);
          I(QUOTE, 10); I(PREP, 1); I(GLOBAL, 15); I(PREP, 1);
          I(FVAR, 1); I(PREP, 1); I(QUOTE, 13); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
          I(CALL, 1); I(RETURN); I(ARG, 0); I(TRAP, 325);
          I(QUOTE, 16); I(MEQ); I(QUOTE, 10); I(PREP, 1);
          I(FVAR, 1); I(PREP, 1); I(QUOTE, 16); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 0); I(CALL, 1); I(RETURN);
          I(ARG, 0); I(TRAP, 351); I(QUOTE, 2); I(MEQ);
          I(FVAR, 6); I(PREP, 3); I(QUOTE, 2); I(PUTARG, 0);
          I(FVAR, 7); I(PUTARG, 1); I(QUOTE, 6); I(PUTARG, 2);
          I(CALL, 3); I(RETURN); I(ARG, 0); I(TRAP, 385);
          I(QUOTE, 17); I(MEQ); I(QUOTE, 18); I(PREP, 1);
          I(FVAR, 3); I(PREP, 3); I(QUOTE, 17); I(PUTARG, 0);
          I(FVAR, 4); I(PUTARG, 1); I(QUOTE, 19); I(PUTARG, 2);
          I(CALL, 3); I(PUTARG, 0); I(CALL, 1); I(RETURN);
          I(FVAR, 8); I(PREP, 1); I(QUOTE, 20); I(PUTARG, 0);
          I(CALL, 1); I(RETURN); }),
          N("ident"), N("not"), N("lpar"), N("var"),
          N("_iscons"), N("prim"), N("rpar"), N("_synerror"),
          S("#constructor"), N("atom"), N("const"), N("_"),
          N("anon"), N("number"), N("-"), N("~"),
          N("string"), N("bra"), N("list"), N("ket"),
          S("#pattern"))),
      F("p_patfactor", 0, (() -> {
        I(FVAR, 1); I(PREP, 0); I(CALL, 0); I(BIND, 0);
        I(FVAR, 2); I(PREP, 1); I(QUOTE, 0); I(PUTARG, 0);
        I(CALL, 1); I(JFALSE, 39); I(QUOTE, 1); I(PREP, 2);
        I(LOCAL, 0); I(PUTARG, 0); I(FVAR, 0); I(PREP, 0);
        I(CALL, 0); I(PUTARG, 1); I(CALL, 2); I(RETURN);
        I(LOCAL, 0); I(RETURN); }),
        N(":"), N("cons")),
      F("_p_patt", 0, (() -> {
        I(QUOTE, 0); I(FRAME, 3); I(FVAR, 2); I(PUTARG, 1);
        I(FVAR, 1); I(PUTARG, 2); I(CLOSURE, 3); I(BIND, 0);
        I(LOCAL, 0); I(PREP, 1); I(FVAR, 3); I(PREP, 0);
        I(CALL, 0); I(PUTARG, 0); I(CALL, 1); I(RETURN); }),
        F("chain", 1, (() -> {
          I(FVAR, 1); I(PREP, 1); I(QUOTE, 0); I(PUTARG, 0);
          I(CALL, 1); I(JFALSE, 43); I(FVAR, 0); I(PREP, 1);
          I(QUOTE, 1); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
          I(FVAR, 2); I(PREP, 1); I(QUOTE, 2); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0);
          I(CALL, 1); I(RETURN); I(ARG, 0); I(RETURN); }),
          N("+"), N("plus"), N("number"))),
      F("p_formals", 0, (() -> {
        I(FVAR, 1); I(PREP, 3); I(QUOTE, 0); I(PUTARG, 0);
        I(FVAR, 2); I(PUTARG, 1); I(QUOTE, 1); I(PUTARG, 2);
        I(CALL, 3); I(RETURN); }),
        N("lpar"), N("rpar")),
      F("p_exprs", 1, (() -> {
        I(FVAR, 1); I(PREP, 2); I(FVAR, 2); I(PUTARG, 0);
        I(ARG, 0); I(PUTARG, 1); I(CALL, 2); I(RETURN); })),
      F("mapa", 3, (() -> {
        I(FVAR, 1); I(PREP, 4); I(QUOTE, 0); I(PREP, 1);
        I(QUOTE, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
        I(QUOTE, 2); I(PREP, 2); I(PUSH, 2); I(PUTARG, 0);
        I(ARG, 0); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1);
        I(ARG, 1); I(PUTARG, 2); I(ARG, 2); I(PUTARG, 3);
        I(CALL, 4); I(RETURN); }),
        N("var"), N("_mapa"), N("function")),
      F("expand", 3, (() -> {
        I(ARG, 1); I(TRAP, 20); I(MNIL); I(QUOTE, 0);
        I(PREP, 2); I(ARG, 0); I(PUTARG, 0); I(ARG, 2);
        I(PUTARG, 1); I(CALL, 2); I(RETURN); I(ARG, 1);
        I(TRAP, 122); I(MCONS); I(TRAP, 121); I(QUOTE, 1);
        I(MPRIM, 2); I(BIND, 0); I(TRAP, 121); I(QUOTE, 2);
        I(MPRIM, 1); I(BIND, 1); I(BIND, 2); I(QUOTE, 2);
        I(PREP, 1); I(GLOBAL, 3); I(PREP, 0); I(CALL, 0);
        I(PUTARG, 0); I(CALL, 1); I(BIND, 3); I(FVAR, 1);
        I(PREP, 3); I(QUOTE, 4); I(PREP, 2); I(QUOTE, 2);
        I(PREP, 1); I(LOCAL, 1); I(PUTARG, 0); I(CALL, 1);
        I(LOCAL, 3); I(NIL); I(CONS); I(CONS);
        I(PUTARG, 0); I(FVAR, 0); I(PREP, 3); I(ARG, 0);
        I(PUTARG, 0); I(LOCAL, 2); I(PUTARG, 1); I(LOCAL, 3);
        I(PUTARG, 2); I(CALL, 3); I(PUTARG, 1); I(CALL, 2);
        I(NIL); I(CONS); I(PUTARG, 0); I(LOCAL, 0);
        I(PUTARG, 1); I(ARG, 2); I(PUTARG, 2); I(CALL, 3);
        I(RETURN); I(POP); I(ARG, 1); I(TRAP, 230);
        I(MCONS); I(TRAP, 229); I(QUOTE, 1); I(MPRIM, 2);
        I(BIND, 0); I(BIND, 1); I(BIND, 2); I(QUOTE, 2);
        I(PREP, 1); I(GLOBAL, 3); I(PREP, 0); I(CALL, 0);
        I(PUTARG, 0); I(CALL, 1); I(BIND, 3); I(FVAR, 1);
        I(PREP, 3); I(QUOTE, 4); I(PREP, 2); I(LOCAL, 1);
        I(LOCAL, 3); I(NIL); I(CONS); I(CONS);
        I(PUTARG, 0); I(FVAR, 0); I(PREP, 3); I(ARG, 0);
        I(PUTARG, 0); I(LOCAL, 2); I(PUTARG, 1); I(LOCAL, 3);
        I(PUTARG, 2); I(CALL, 3); I(PUTARG, 1); I(CALL, 2);
        I(QUOTE, 4); I(PREP, 2); I(QUOTE, 5); I(LOCAL, 3);
        I(NIL); I(CONS); I(CONS); I(PUTARG, 0);
        I(LOCAL, 3); I(PUTARG, 1); I(CALL, 2); I(NIL);
        I(CONS); I(CONS); I(PUTARG, 0); I(LOCAL, 0);
        I(PUTARG, 1); I(ARG, 2); I(PUTARG, 2); I(CALL, 3);
        I(RETURN); I(POP); I(ARG, 1); I(TRAP, 281);
        I(MCONS); I(TRAP, 280); I(QUOTE, 6); I(MPRIM, 1);
        I(BIND, 0); I(BIND, 1); I(QUOTE, 7); I(PREP, 3);
        I(LOCAL, 0); I(PUTARG, 0); I(FVAR, 0); I(PREP, 3);
        I(ARG, 0); I(PUTARG, 0); I(LOCAL, 1); I(PUTARG, 1);
        I(ARG, 2); I(PUTARG, 2); I(CALL, 3); I(PUTARG, 1);
        I(ARG, 2); I(PUTARG, 2); I(CALL, 3); I(RETURN);
        I(POP); I(FAIL); }),
        N("cons"), N("gen"), N("var"), N("_gensym"),
        N("rule"), N("anon"), N("when"), N("if")),
      F("p_gen", 0, (() -> {
        I(FVAR, 1); I(PREP, 0); I(CALL, 0); I(BIND, 0);
        I(FVAR, 2); I(PREP, 1); I(QUOTE, 0); I(PUTARG, 0);
        I(CALL, 1); I(POP); I(QUOTE, 1); I(PREP, 2);
        I(LOCAL, 0); I(PUTARG, 0); I(FVAR, 3); I(PREP, 0);
        I(CALL, 0); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
        N("<-"), N("gen")),
      F("p_gens", 0, (() -> {
        I(QUOTE, 0); I(FRAME, 5); I(FVAR, 4); I(PUTARG, 1);
        I(FVAR, 3); I(PUTARG, 2); I(FVAR, 2); I(PUTARG, 3);
        I(FVAR, 1); I(PUTARG, 4); I(CLOSURE, 5); I(BIND, 0);
        I(FVAR, 4); I(PREP, 0); I(CALL, 0); I(BIND, 1);
        I(GLOBAL, 1); I(PREP, 2); I(LOCAL, 1); I(PUTARG, 0);
        I(LOCAL, 0); I(PREP, 0); I(CALL, 0); I(PUTARG, 1);
        I(CALL, 2); I(RETURN); }),
        F("p_tail", 0, (() -> {
          I(QUOTE, 0); I(FRAME, 5); I(FVAR, 3); I(PUTARG, 1);
          I(FVAR, 2); I(PUTARG, 2); I(FVAR, 0); I(PUTARG, 3);
          I(FVAR, 1); I(PUTARG, 4); I(CLOSURE, 5); I(BIND, 0);
          I(FVAR, 4); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
          I(CALL, 1); I(RETURN); }),
          F("case", 1, (() -> {
            I(ARG, 0); I(TRAP, 53); I(QUOTE, 0); I(MEQ);
            I(FVAR, 1); I(PREP, 1); I(QUOTE, 0); I(PUTARG, 0);
            I(CALL, 1); I(POP); I(FVAR, 2); I(PREP, 0);
            I(CALL, 0); I(BIND, 0); I(GLOBAL, 1); I(PREP, 2);
            I(QUOTE, 0); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
            I(CALL, 1); I(PUTARG, 0); I(FVAR, 3); I(PREP, 0);
            I(CALL, 0); I(PUTARG, 1); I(CALL, 2); I(RETURN);
            I(ARG, 0); I(TRAP, 98); I(QUOTE, 2); I(MEQ);
            I(FVAR, 1); I(PREP, 1); I(QUOTE, 2); I(PUTARG, 0);
            I(CALL, 1); I(POP); I(FVAR, 4); I(PREP, 0);
            I(CALL, 0); I(BIND, 0); I(GLOBAL, 1); I(PREP, 2);
            I(LOCAL, 0); I(PUTARG, 0); I(FVAR, 3); I(PREP, 0);
            I(CALL, 0); I(PUTARG, 1); I(CALL, 2); I(RETURN);
            I(NIL); I(RETURN); }),
            N("when"), N(":"), N("comma"))),
        N(":")),
      F("p_listexp", 0, (() -> {
        I(FVAR, 1); I(PREP, 1); I(QUOTE, 0); I(PUTARG, 0);
        I(CALL, 1); I(JFALSE, 15); I(QUOTE, 1); I(RETURN);
        I(FVAR, 2); I(PREP, 0); I(CALL, 0); I(BIND, 0);
        I(QUOTE, 2); I(FRAME, 8); I(FVAR, 7); I(PUTARG, 1);
        I(FVAR, 2); I(PUTARG, 2); I(LOCAL, 0); I(PUTARG, 3);
        I(FVAR, 6); I(PUTARG, 4); I(FVAR, 5); I(PUTARG, 5);
        I(FVAR, 4); I(PUTARG, 6); I(FVAR, 3); I(PUTARG, 7);
        I(CLOSURE, 8); I(BIND, 1); I(FVAR, 8); I(PREP, 1);
        I(LOCAL, 1); I(PUTARG, 0); I(CALL, 1); I(RETURN); }),
        N("ket"), N("nil"), 
        F("case", 1, (() -> {
          I(ARG, 0); I(TRAP, 42); I(QUOTE, 0); I(MEQ);
          I(QUOTE, 1); I(PREP, 1); I(GLOBAL, 2); I(PREP, 2);
          I(FVAR, 3); I(PUTARG, 0); I(FVAR, 1); I(PREP, 2);
          I(FVAR, 2); I(PUTARG, 0); I(QUOTE, 0); I(PUTARG, 1);
          I(CALL, 2); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0);
          I(CALL, 1); I(RETURN); I(ARG, 0); I(TRAP, 91);
          I(QUOTE, 3); I(MEQ); I(FVAR, 4); I(PREP, 1);
          I(QUOTE, 3); I(PUTARG, 0); I(CALL, 1); I(POP);
          I(FVAR, 5); I(PREP, 3); I(QUOTE, 4); I(PREP, 1);
          I(QUOTE, 5); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
          I(FVAR, 3); I(PUTARG, 1); I(FVAR, 2); I(PREP, 0);
          I(CALL, 0); I(PUTARG, 2); I(CALL, 3); I(RETURN);
          I(ARG, 0); I(TRAP, 132); I(QUOTE, 6); I(MEQ);
          I(FVAR, 4); I(PREP, 1); I(QUOTE, 6); I(PUTARG, 0);
          I(CALL, 1); I(POP); I(FVAR, 6); I(PREP, 3);
          I(FVAR, 3); I(PUTARG, 0); I(FVAR, 7); I(PREP, 0);
          I(CALL, 0); I(PUTARG, 1); I(QUOTE, 7); I(PUTARG, 2);
          I(CALL, 3); I(RETURN); I(QUOTE, 1); I(PREP, 1);
          I(FVAR, 3); I(NIL); I(CONS); I(PUTARG, 0);
          I(CALL, 1); I(RETURN); }),
          N("comma"), N("list"), N(":"), N(".."),
          N("var"), N("_range"), N("vbar"), N("nil"))),
      F("p_parenexp", 0, (() -> {
        I(GLOBAL, 0); I(PREP, 2); I(GLOBAL, 1); I(PREP, 1);
        I(FVAR, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
        I(QUOTE, 2); I(PUTARG, 1); I(CALL, 2); I(JFALSE, 28);
        I(QUOTE, 3); I(JUMP, 54); I(GLOBAL, 4); I(PREP, 1);
        I(FVAR, 2); I(PREP, 1); I(GLOBAL, 1); I(PREP, 1);
        I(FVAR, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 0); I(CALL, 1); I(JFALSE, 67);
        I(FVAR, 3); I(PREP, 1); I(GLOBAL, 5); I(PUTARG, 0);
        I(CALL, 1); I(RETURN); I(FVAR, 4); I(PREP, 1);
        I(GLOBAL, 1); I(PREP, 1); I(FVAR, 1); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 0); I(CALL, 1); I(BIND, 0);
        I(FVAR, 5); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
        I(CALL, 1); I(BIND, 1); I(FVAR, 6); I(PREP, 1);
        I(QUOTE, 6); I(PUTARG, 0); I(CALL, 1); I(JFALSE, 122);
        I(QUOTE, 7); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
        I(CALL, 1); I(RETURN); I(FVAR, 7); I(PREP, 3);
        I(QUOTE, 7); I(PREP, 1); I(QUOTE, 8); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 0); I(QUOTE, 7); I(PREP, 1);
        I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1);
        I(FVAR, 8); I(PREP, 2); I(GLOBAL, 9); I(PREP, 1);
        I(LOCAL, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
        I(GLOBAL, 10); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 2);
        I(CALL, 3); I(RETURN); }),
        N("="), N("_get"), N("-"), B(true),
        N("not"), N("true"), N("rpar"), N("var"),
        N("_rsect"), N("_snd"), N("false")),
      F("p_apply", 1, (() -> {
        I(GLOBAL, 0); I(PREP, 1); I(FVAR, 1); I(PREP, 1);
        I(QUOTE, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
        I(CALL, 1); I(JFALSE, 23); I(ARG, 0); I(RETURN);
        I(QUOTE, 2); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
        I(FVAR, 2); I(PREP, 3); I(QUOTE, 1); I(PUTARG, 0);
        I(FVAR, 3); I(PUTARG, 1); I(QUOTE, 3); I(PUTARG, 2);
        I(CALL, 3); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
        N("not"), N("lpar"), N("apply"), N("rpar")),
      F("p_primary", 0, (() -> {
        I(QUOTE, 0); I(FRAME, 7); I(FVAR, 6); I(PUTARG, 1);
        I(FVAR, 5); I(PUTARG, 2); I(FVAR, 4); I(PUTARG, 3);
        I(FVAR, 3); I(PUTARG, 4); I(FVAR, 2); I(PUTARG, 5);
        I(FVAR, 1); I(PUTARG, 6); I(CLOSURE, 7); I(BIND, 0);
        I(FVAR, 7); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
        I(CALL, 1); I(RETURN); }),
        F("case", 1, (() -> {
          I(ARG, 0); I(TRAP, 26); I(QUOTE, 0); I(MEQ);
          I(QUOTE, 1); I(PREP, 1); I(FVAR, 1); I(PREP, 1);
          I(QUOTE, 0); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
          I(CALL, 1); I(RETURN); I(ARG, 0); I(TRAP, 52);
          I(QUOTE, 2); I(MEQ); I(QUOTE, 1); I(PREP, 1);
          I(FVAR, 1); I(PREP, 1); I(QUOTE, 2); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 0); I(CALL, 1); I(RETURN);
          I(ARG, 0); I(TRAP, 90); I(QUOTE, 3); I(MEQ);
          I(FVAR, 1); I(PREP, 1); I(QUOTE, 3); I(PUTARG, 0);
          I(CALL, 1); I(BIND, 0); I(FVAR, 2); I(PREP, 1);
          I(QUOTE, 4); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 0); I(CALL, 1); I(RETURN);
          I(ARG, 0); I(TRAP, 128); I(QUOTE, 5); I(MEQ);
          I(FVAR, 1); I(PREP, 1); I(QUOTE, 5); I(PUTARG, 0);
          I(CALL, 1); I(BIND, 0); I(FVAR, 2); I(PREP, 1);
          I(QUOTE, 1); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 0); I(CALL, 1); I(RETURN);
          I(ARG, 0); I(TRAP, 154); I(QUOTE, 6); I(MEQ);
          I(FVAR, 3); I(PREP, 3); I(QUOTE, 6); I(PUTARG, 0);
          I(FVAR, 4); I(PUTARG, 1); I(QUOTE, 7); I(PUTARG, 2);
          I(CALL, 3); I(RETURN); I(ARG, 0); I(TRAP, 180);
          I(QUOTE, 8); I(MEQ); I(FVAR, 3); I(PREP, 3);
          I(QUOTE, 8); I(PUTARG, 0); I(FVAR, 5); I(PUTARG, 1);
          I(QUOTE, 9); I(PUTARG, 2); I(CALL, 3); I(RETURN);
          I(ARG, 0); I(TRAP, 198); I(QUOTE, 10); I(MEQ);
          I(FVAR, 6); I(PREP, 1); I(QUOTE, 11); I(PUTARG, 0);
          I(CALL, 1); I(RETURN); I(FVAR, 6); I(PREP, 1);
          I(QUOTE, 12); I(PUTARG, 0); I(CALL, 1); I(RETURN); }),
          N("number"), N("const"), N("string"), N("ident"),
          N("var"), N("atom"), N("lpar"), N("rpar"),
          N("bra"), N("ket"), N("eof"), S("#exp"),
          S("#badexp"))),
      F("p_factor", 1, (() -> {
        I(QUOTE, 0); I(FRAME, 8); I(FVAR, 5); I(PUTARG, 1);
        I(ARG, 0); I(PUTARG, 2); I(FVAR, 4); I(PUTARG, 3);
        I(FVAR, 3); I(PUTARG, 4); I(FVAR, 0); I(PUTARG, 5);
        I(FVAR, 2); I(PUTARG, 6); I(FVAR, 1); I(PUTARG, 7);
        I(CLOSURE, 8); I(BIND, 0); I(FVAR, 6); I(PREP, 1);
        I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(RETURN); }),
        F("case", 1, (() -> {
          I(ARG, 0); I(TRAP, 81); I(QUOTE, 0); I(MEQ);
          I(FVAR, 1); I(PREP, 1); I(QUOTE, 0); I(PUTARG, 0);
          I(CALL, 1); I(BIND, 0); I(FVAR, 2); I(JFALSE, 35);
          I(FVAR, 3); I(PREP, 1); I(QUOTE, 1); I(PUTARG, 0);
          I(CALL, 1); I(JUMP, 37); I(QUOTE, 2); I(JFALSE, 50);
          I(QUOTE, 3); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
          I(CALL, 1); I(RETURN); I(FVAR, 4); I(PREP, 2);
          I(QUOTE, 3); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 0); I(FVAR, 5); I(PREP, 1);
          I(GLOBAL, 4); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1);
          I(CALL, 2); I(RETURN); I(ARG, 0); I(TRAP, 200);
          I(QUOTE, 5); I(MEQ); I(FVAR, 6); I(PREP, 1);
          I(QUOTE, 5); I(PUTARG, 0); I(CALL, 1); I(POP);
          I(FVAR, 3); I(PREP, 1); I(QUOTE, 6); I(PUTARG, 0);
          I(CALL, 1); I(JFALSE, 138); I(QUOTE, 7); I(PREP, 1);
          I(GLOBAL, 8); I(PREP, 1); I(FVAR, 1); I(PREP, 1);
          I(QUOTE, 6); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 0); I(CALL, 1); I(RETURN);
          I(FVAR, 2); I(JFALSE, 154); I(FVAR, 3); I(PREP, 1);
          I(QUOTE, 1); I(PUTARG, 0); I(CALL, 1); I(JUMP, 156);
          I(QUOTE, 2); I(JFALSE, 169); I(QUOTE, 3); I(PREP, 1);
          I(QUOTE, 5); I(PUTARG, 0); I(CALL, 1); I(RETURN);
          I(FVAR, 4); I(PREP, 2); I(QUOTE, 3); I(PREP, 1);
          I(QUOTE, 8); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
          I(FVAR, 5); I(PREP, 1); I(GLOBAL, 4); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 1); I(CALL, 2); I(RETURN);
          I(FVAR, 7); I(PREP, 0); I(CALL, 0); I(RETURN); }),
          N("monop"), N("rpar"), B(false), N("var"),
          N("false"), N("-"), N("number"), N("const"),
          N("~"))),
      F("makebin", 3, (() -> {
        I(QUOTE, 0); I(FRAME, 5); I(ARG, 2); I(PUTARG, 1);
        I(ARG, 1); I(PUTARG, 2); I(FVAR, 1); I(PUTARG, 3);
        I(ARG, 0); I(PUTARG, 4); I(CLOSURE, 5); I(BIND, 0);
        I(LOCAL, 0); I(PREP, 1); I(ARG, 0); I(PUTARG, 0);
        I(CALL, 1); I(RETURN); }),
        F("case", 1, (() -> {
          I(ARG, 0); I(TRAP, 34); I(QUOTE, 0); I(MEQ);
          I(QUOTE, 1); I(PREP, 3); I(FVAR, 2); I(PUTARG, 0);
          I(FVAR, 1); I(PUTARG, 1); I(QUOTE, 2); I(PREP, 1);
          I(GLOBAL, 3); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 2);
          I(CALL, 3); I(RETURN); I(ARG, 0); I(TRAP, 68);
          I(QUOTE, 4); I(MEQ); I(QUOTE, 1); I(PREP, 3);
          I(FVAR, 2); I(PUTARG, 0); I(QUOTE, 2); I(PREP, 1);
          I(GLOBAL, 5); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1);
          I(FVAR, 1); I(PUTARG, 2); I(CALL, 3); I(RETURN);
          I(FVAR, 3); I(PREP, 3); I(QUOTE, 6); I(PREP, 1);
          I(FVAR, 4); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
          I(FVAR, 2); I(PUTARG, 1); I(FVAR, 1); I(PUTARG, 2);
          I(CALL, 3); I(RETURN); }),
          N("and"), N("if"), N("const"), N("false"),
          N("or"), N("true"), N("var"))),
      F("_p_term", 2, (() -> {
        I(QUOTE, 0); I(FRAME, 11); I(FVAR, 9); I(PUTARG, 1);
        I(FVAR, 8); I(PUTARG, 2); I(FVAR, 7); I(PUTARG, 3);
        I(FVAR, 6); I(PUTARG, 4); I(FVAR, 5); I(PUTARG, 5);
        I(ARG, 1); I(PUTARG, 6); I(FVAR, 4); I(PUTARG, 7);
        I(FVAR, 3); I(PUTARG, 8); I(FVAR, 2); I(PUTARG, 9);
        I(FVAR, 1); I(PUTARG, 10); I(CLOSURE, 11); I(BIND, 0);
        I(LOCAL, 0); I(PREP, 2); I(FVAR, 10); I(PREP, 1);
        I(ARG, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
        I(ARG, 0); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
        F("p_termcont", 2, (() -> {
          I(GLOBAL, 0); I(PREP, 1); I(FVAR, 1); I(PUTARG, 0);
          I(CALL, 1); I(BIND, 0); I(GLOBAL, 1); I(PREP, 1);
          I(FVAR, 2); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 0); I(CALL, 1); I(JFALSE, 35);
          I(ARG, 0); I(RETURN); I(GLOBAL, 0); I(PREP, 1);
          I(FVAR, 3); I(PUTARG, 0); I(CALL, 1); I(BIND, 1);
          I(FVAR, 4); I(PREP, 1); I(LOCAL, 1); I(PUTARG, 0);
          I(CALL, 1); I(BIND, 2); I(GLOBAL, 2); I(PREP, 2);
          I(GLOBAL, 3); I(PREP, 1); I(LOCAL, 2); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
          I(CALL, 2); I(JFALSE, 86); I(ARG, 0); I(RETURN);
          I(FVAR, 5); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
          I(CALL, 1); I(POP); I(FVAR, 6); I(JFALSE, 113);
          I(FVAR, 7); I(PREP, 1); I(QUOTE, 4); I(PUTARG, 0);
          I(CALL, 1); I(JUMP, 115); I(QUOTE, 5); I(JFALSE, 152);
          I(FVAR, 8); I(PREP, 3); I(QUOTE, 6); I(PREP, 1);
          I(QUOTE, 7); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
          I(QUOTE, 6); I(PREP, 1); I(LOCAL, 1); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 1); I(ARG, 0); I(PUTARG, 2);
          I(CALL, 3); I(RETURN); I(FVAR, 9); I(PREP, 2);
          I(GLOBAL, 8); I(PREP, 1); I(LOCAL, 2); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 0); I(GLOBAL, 9); I(PUTARG, 1);
          I(CALL, 2); I(BIND, 3); I(FVAR, 0); I(PREP, 2);
          I(FVAR, 10); I(PREP, 3); I(LOCAL, 1); I(PUTARG, 0);
          I(ARG, 0); I(PUTARG, 1); I(LOCAL, 3); I(PUTARG, 2);
          I(CALL, 3); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
          I(CALL, 2); I(RETURN); }),
          N("_get"), N("not"), N("<"), N("_fst"),
          N("rpar"), B(false), N("var"), N("_lsect"),
          N("_snd"), N("false"))),
      F("p_cond", 1, (() -> {
        I(FVAR, 1); I(PREP, 1); I(QUOTE, 0); I(PUTARG, 0);
        I(CALL, 1); I(JFALSE, 89); I(FVAR, 0); I(PREP, 1);
        I(GLOBAL, 1); I(PUTARG, 0); I(CALL, 1); I(BIND, 0);
        I(FVAR, 2); I(PREP, 1); I(QUOTE, 2); I(PUTARG, 0);
        I(CALL, 1); I(POP); I(FVAR, 0); I(PREP, 1);
        I(GLOBAL, 1); I(PUTARG, 0); I(CALL, 1); I(BIND, 1);
        I(FVAR, 2); I(PREP, 1); I(QUOTE, 3); I(PUTARG, 0);
        I(CALL, 1); I(POP); I(FVAR, 0); I(PREP, 1);
        I(GLOBAL, 1); I(PUTARG, 0); I(CALL, 1); I(BIND, 2);
        I(QUOTE, 0); I(PREP, 3); I(LOCAL, 0); I(PUTARG, 0);
        I(LOCAL, 1); I(PUTARG, 1); I(LOCAL, 2); I(PUTARG, 2);
        I(CALL, 3); I(RETURN); I(FVAR, 3); I(PREP, 2);
        I(PUSH, 1); I(PUTARG, 0); I(ARG, 0); I(PUTARG, 1);
        I(CALL, 2); I(RETURN); }),
        N("if"), N("false"), N("then"), N("else")),
      F("_p_expr", 1, (() -> {
        I(QUOTE, 0); I(FRAME, 8); I(FVAR, 6); I(PUTARG, 1);
        I(FVAR, 5); I(PUTARG, 2); I(FVAR, 4); I(PUTARG, 3);
        I(FVAR, 3); I(PUTARG, 4); I(FVAR, 2); I(PUTARG, 5);
        I(ARG, 0); I(PUTARG, 6); I(FVAR, 1); I(PUTARG, 7);
        I(CLOSURE, 8); I(BIND, 0); I(FVAR, 7); I(PREP, 1);
        I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(RETURN); }),
        F("case", 1, (() -> {
          I(ARG, 0); I(TRAP, 56); I(QUOTE, 0); I(MEQ);
          I(FVAR, 1); I(PREP, 1); I(QUOTE, 0); I(PUTARG, 0);
          I(CALL, 1); I(POP); I(FVAR, 2); I(PREP, 0);
          I(CALL, 0); I(BIND, 0); I(FVAR, 1); I(PREP, 1);
          I(QUOTE, 1); I(PUTARG, 0); I(CALL, 1); I(POP);
          I(QUOTE, 0); I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0);
          I(FVAR, 3); I(PREP, 0); I(CALL, 0); I(PUTARG, 1);
          I(CALL, 2); I(RETURN); I(ARG, 0); I(TRAP, 123);
          I(QUOTE, 2); I(MEQ); I(FVAR, 1); I(PREP, 1);
          I(QUOTE, 2); I(PUTARG, 0); I(CALL, 1); I(POP);
          I(FVAR, 4); I(PREP, 0); I(CALL, 0); I(BIND, 0);
          I(QUOTE, 2); I(PREP, 2); I(GLOBAL, 3); I(PREP, 1);
          I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
          I(QUOTE, 4); I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0);
          I(FVAR, 3); I(PREP, 0); I(CALL, 0); I(PUTARG, 1);
          I(CALL, 2); I(NIL); I(CONS); I(PUTARG, 1);
          I(CALL, 2); I(RETURN); I(FVAR, 5); I(PREP, 1);
          I(FVAR, 6); I(PUTARG, 0); I(CALL, 1); I(BIND, 0);
          I(FVAR, 7); I(PREP, 1); I(QUOTE, 5); I(PUTARG, 0);
          I(CALL, 1); I(JFALSE, 166); I(QUOTE, 6); I(PREP, 2);
          I(LOCAL, 0); I(PUTARG, 0); I(FVAR, 3); I(PREP, 0);
          I(CALL, 0); I(PUTARG, 1); I(CALL, 2); I(RETURN);
          I(LOCAL, 0); I(RETURN); }),
          N("let"), N("in"), N("function"), N("length"),
          N("rule"), N(">>"), N("seq"))),
      F("p_name", 0, (() -> {
        I(FVAR, 1); I(PREP, 1); I(GLOBAL, 0); I(PREP, 1);
        I(FVAR, 2); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
        I(CALL, 1); I(JFALSE, 24); I(QUOTE, 1); I(JUMP, 34);
        I(FVAR, 3); I(PREP, 1); I(QUOTE, 2); I(PUTARG, 0);
        I(CALL, 1); I(JFALSE, 55); I(FVAR, 4); I(PREP, 1);
        I(GLOBAL, 0); I(PREP, 1); I(FVAR, 2); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 0); I(CALL, 1); I(RETURN);
        I(FVAR, 4); I(PREP, 1); I(QUOTE, 3); I(PUTARG, 0);
        I(CALL, 1); I(RETURN); }),
        N("_get"), B(true), N("monop"), N("ident")),
      F("p_rhs", 1, (() -> {
        I(FVAR, 1); I(PREP, 1); I(QUOTE, 0); I(PUTARG, 0);
        I(CALL, 1); I(POP); I(FVAR, 2); I(PREP, 0);
        I(CALL, 0); I(BIND, 0); I(FVAR, 3); I(PREP, 1);
        I(QUOTE, 1); I(PUTARG, 0); I(CALL, 1); I(JFALSE, 54);
        I(QUOTE, 2); I(PREP, 3); I(ARG, 0); I(PUTARG, 0);
        I(FVAR, 2); I(PREP, 0); I(CALL, 0); I(PUTARG, 1);
        I(LOCAL, 0); I(PUTARG, 2); I(CALL, 3); I(RETURN);
        I(QUOTE, 3); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
        I(LOCAL, 0); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
        N("="), N("when"), N("crule"), N("rule")),
      F("p_rule", 2, (() -> {
        I(GLOBAL, 0); I(PREP, 1); I(FVAR, 1); I(PUTARG, 0);
        I(CALL, 1); I(BIND, 0); I(FVAR, 2); I(PREP, 1);
        I(GLOBAL, 0); I(PREP, 1); I(FVAR, 3); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 0); I(CALL, 1); I(JFALSE, 36);
        I(QUOTE, 1); I(JUMP, 46); I(FVAR, 4); I(PREP, 1);
        I(QUOTE, 2); I(PUTARG, 0); I(CALL, 1); I(JFALSE, 60);
        I(GLOBAL, 0); I(PREP, 1); I(FVAR, 3); I(PUTARG, 0);
        I(CALL, 1); I(JUMP, 62); I(QUOTE, 3); I(BIND, 1);
        I(FVAR, 4); I(PREP, 1); I(LOCAL, 1); I(PUTARG, 0);
        I(CALL, 1); I(JFALSE, 92); I(GLOBAL, 4); I(PREP, 2);
        I(ARG, 0); I(PUTARG, 0); I(LOCAL, 0); I(PUTARG, 1);
        I(CALL, 2); I(JUMP, 94); I(QUOTE, 5); I(JFALSE, 108);
        I(FVAR, 5); I(PREP, 1); I(QUOTE, 6); I(PUTARG, 0);
        I(CALL, 1); I(JUMP, 109); I(NIL); I(POP);
        I(FVAR, 6); I(PREP, 1); I(LOCAL, 1); I(PUTARG, 0);
        I(CALL, 1); I(POP); I(FVAR, 7); I(PREP, 0);
        I(CALL, 0); I(BIND, 2); I(GLOBAL, 7); I(PREP, 2);
        I(GLOBAL, 8); I(PREP, 1); I(LOCAL, 2); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(CALL, 2); I(JFALSE, 156); I(NIL); I(JUMP, 166);
        I(FVAR, 5); I(PREP, 1); I(QUOTE, 9); I(PUTARG, 0);
        I(CALL, 1); I(POP); I(FVAR, 8); I(PREP, 1);
        I(LOCAL, 2); I(PUTARG, 0); I(CALL, 1); I(RETURN); }),
        N("_get"), B(true), N("monop"), N("ident"),
        N("<>"), B(false), S("#names"), N("="),
        N("length"), S("#arity")),
      F("_p_defn", 0, (() -> {
        I(FVAR, 1); I(PREP, 0); I(CALL, 0); I(BIND, 0);
        I(GLOBAL, 0); I(PREP, 1); I(FVAR, 2); I(PREP, 1);
        I(QUOTE, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
        I(CALL, 1); I(JFALSE, 58); I(FVAR, 3); I(PREP, 1);
        I(QUOTE, 2); I(PUTARG, 0); I(CALL, 1); I(POP);
        I(QUOTE, 3); I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0);
        I(FVAR, 4); I(PREP, 0); I(CALL, 0); I(PUTARG, 1);
        I(CALL, 2); I(RETURN); I(FVAR, 5); I(PREP, 0);
        I(CALL, 0); I(BIND, 1); I(GLOBAL, 4); I(PREP, 1);
        I(LOCAL, 1); I(PUTARG, 0); I(CALL, 1); I(BIND, 2);
        I(FVAR, 6); I(PREP, 1); I(LOCAL, 1); I(PUTARG, 0);
        I(CALL, 1); I(BIND, 3); I(QUOTE, 5); I(PREP, 3);
        I(LOCAL, 0); I(PUTARG, 0); I(LOCAL, 2); I(PUTARG, 1);
        I(GLOBAL, 6); I(PREP, 2); I(LOCAL, 3); I(PUTARG, 0);
        I(FVAR, 7); I(PREP, 2); I(QUOTE, 7); I(FRAME, 4);
        I(FVAR, 8); I(PUTARG, 1); I(LOCAL, 2); I(PUTARG, 2);
        I(LOCAL, 0); I(PUTARG, 3); I(CLOSURE, 4); I(PUTARG, 0);
        I(QUOTE, 8); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1);
        I(CALL, 2); I(PUTARG, 2); I(CALL, 3); I(RETURN); }),
        N("not"), N("lpar"), N("="), N("val"),
        N("length"), N("fun"), N(":"), 
        F("<function>", 0, (() -> {
          I(FVAR, 1); I(PREP, 2); I(FVAR, 3); I(PUTARG, 0);
          I(FVAR, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN); })),
        N("vbar")),
      F("p_para", 0, (() -> {
        I(FVAR, 1); I(PREP, 1); I(QUOTE, 0); I(PUTARG, 0);
        I(CALL, 1); I(JFALSE, 15); I(QUOTE, 0); I(RETURN);
        I(FVAR, 2); I(PREP, 1); I(QUOTE, 1); I(PUTARG, 0);
        I(CALL, 1); I(JFALSE, 35); I(FVAR, 3); I(PREP, 0);
        I(CALL, 0); I(JUMP, 41); I(FVAR, 4); I(PREP, 0);
        I(CALL, 0); I(BIND, 0); I(FVAR, 1); I(PREP, 1);
        I(QUOTE, 2); I(PUTARG, 0); I(CALL, 1); I(JFALSE, 67);
        I(FVAR, 5); I(PREP, 1); I(QUOTE, 3); I(PUTARG, 0);
        I(CALL, 1); I(JUMP, 148); I(FVAR, 1); I(PREP, 1);
        I(QUOTE, 4); I(PUTARG, 0); I(CALL, 1); I(JFALSE, 91);
        I(FVAR, 5); I(PREP, 1); I(QUOTE, 5); I(PUTARG, 0);
        I(CALL, 1); I(JUMP, 148); I(GLOBAL, 6); I(PREP, 1);
        I(FVAR, 1); I(PREP, 1); I(QUOTE, 7); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 0); I(CALL, 1); I(JFALSE, 131);
        I(GLOBAL, 6); I(PREP, 1); I(FVAR, 1); I(PREP, 1);
        I(QUOTE, 0); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
        I(CALL, 1); I(JUMP, 133); I(QUOTE, 8); I(JFALSE, 147);
        I(FVAR, 5); I(PREP, 1); I(QUOTE, 9); I(PUTARG, 0);
        I(CALL, 1); I(JUMP, 148); I(NIL); I(POP);
        I(LOCAL, 0); I(RETURN); }),
        N("eof"), N("define"), N("rpar"), S("#parenmatch"),
        N("ket"), S("#bramatch"), N("not"), N("semi"),
        B(false), S("#junk")),
      N("_set"), 
      F("parser", 0, (() -> {
        I(FVAR, 1); I(PREP, 0); I(CALL, 0); I(POP);
        I(FVAR, 2); I(PREP, 0); I(CALL, 0); I(RETURN); })),
      F("flatten", 1, (() -> {
        I(QUOTE, 0); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 0);
        I(LOCAL, 0); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
        I(NIL); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
        F("flat", 2, (() -> {
          I(ARG, 0); I(TRAP, 10); I(QUOTE, 0); I(MEQ);
          I(ARG, 1); I(RETURN); I(ARG, 0); I(TRAP, 39);
          I(QUOTE, 1); I(MPRIM, 1); I(BIND, 0); I(GLOBAL, 2);
          I(PREP, 3); I(FVAR, 0); I(PUTARG, 0); I(ARG, 1);
          I(PUTARG, 1); I(LOCAL, 0); I(PUTARG, 2); I(CALL, 3);
          I(RETURN); I(GLOBAL, 3); I(PREP, 2); I(ARG, 0);
          I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1); I(CALL, 2);
          I(RETURN); }),
          N("NOP"), N("SEQ"), N("foldr"), N(":"))),
      F("assemble", 3, (() -> {
        I(GLOBAL, 0); I(PREP, 0); I(CALL, 0); I(BIND, 0);
        I(QUOTE, 1); I(FRAME, 2); I(LOCAL, 0); I(PUTARG, 1);
        I(CLOSURE, 2); I(BIND, 1); I(QUOTE, 2); I(FRAME, 2);
        I(LOCAL, 0); I(PUTARG, 1); I(CLOSURE, 2); I(BIND, 2);
        I(QUOTE, 3); I(FRAME, 2); I(LOCAL, 1); I(PUTARG, 1);
        I(CLOSURE, 2); I(BIND, 3); I(QUOTE, 4); I(FRAME, 2);
        I(LOCAL, 3); I(PUTARG, 1); I(CLOSURE, 2); I(BIND, 4);
        I(GLOBAL, 5); I(PREP, 3); I(ARG, 0); I(PUTARG, 0);
        I(ARG, 1); I(PUTARG, 1); I(LOCAL, 4); I(PREP, 1);
        I(LOCAL, 2); I(PREP, 3); I(PUSH, 0); I(PUTARG, 0);
        I(ARG, 2); I(PUTARG, 1); I(NIL); I(PUTARG, 2);
        I(CALL, 3); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 2);
        I(CALL, 3); I(RETURN); }),
        N("_hash"), 
        F("fixlab", 1, (() -> {
          I(GLOBAL, 0); I(PREP, 2); I(FVAR, 1); I(PUTARG, 0);
          I(ARG, 0); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
          N("_lookup")),
        F("pass1", 3, (() -> {
          I(ARG, 1); I(TRAP, 8); I(MNIL); I(ARG, 2);
          I(RETURN); I(ARG, 1); I(TRAP, 67); I(MCONS);
          I(BIND, 0); I(BIND, 1); I(GLOBAL, 0); I(PREP, 1);
          I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(JFALSE, 67);
          I(GLOBAL, 1); I(PREP, 3); I(FVAR, 1); I(PUTARG, 0);
          I(LOCAL, 0); I(PUTARG, 1); I(ARG, 0); I(PUTARG, 2);
          I(CALL, 3); I(POP); I(FVAR, 0); I(PREP, 3);
          I(ARG, 0); I(PUTARG, 0); I(LOCAL, 1); I(PUTARG, 1);
          I(ARG, 2); I(PUTARG, 2); I(CALL, 3); I(RETURN);
          I(ARG, 1); I(TRAP, 147); I(MCONS); I(BIND, 0);
          I(BIND, 1); I(FVAR, 0); I(PREP, 3); I(GLOBAL, 2);
          I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1);
          I(JFALSE, 108); I(GLOBAL, 3); I(PREP, 2); I(ARG, 0);
          I(PUTARG, 0); I(PUSH, 1); I(PUTARG, 1); I(CALL, 2);
          I(JUMP, 122); I(GLOBAL, 3); I(PREP, 2); I(ARG, 0);
          I(PUTARG, 0); I(PUSH, 2); I(PUTARG, 1); I(CALL, 2);
          I(PUTARG, 0); I(LOCAL, 1); I(PUTARG, 1); I(GLOBAL, 4);
          I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0); I(ARG, 2);
          I(PUTARG, 1); I(CALL, 2); I(PUTARG, 2); I(CALL, 3);
          I(RETURN); I(FAIL); }),
          N("numeric"), N("_update"), N("_isname"), N("+"),
          N(":")),
        F("fixup", 1, (() -> {
          I(ARG, 0); I(TRAP, 29); I(QUOTE, 0); I(MPRIM, 1);
          I(BIND, 0); I(QUOTE, 0); I(PREP, 1); I(FVAR, 1);
          I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1);
          I(PUTARG, 0); I(CALL, 1); I(RETURN); I(ARG, 0);
          I(TRAP, 58); I(QUOTE, 1); I(MPRIM, 1); I(BIND, 0);
          I(QUOTE, 1); I(PREP, 1); I(FVAR, 1); I(PREP, 1);
          I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
          I(CALL, 1); I(RETURN); I(ARG, 0); I(TRAP, 87);
          I(QUOTE, 2); I(MPRIM, 1); I(BIND, 0); I(QUOTE, 2);
          I(PREP, 1); I(FVAR, 1); I(PREP, 1); I(LOCAL, 0);
          I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0); I(CALL, 1);
          I(RETURN); I(ARG, 0); I(RETURN); }),
          N("JUMP"), N("JFALSE"), N("TRAP")),
        F("pass2", 1, (() -> {
          I(GLOBAL, 0); I(PREP, 3); I(QUOTE, 1); I(FRAME, 2);
          I(FVAR, 1); I(PUTARG, 1); I(CLOSURE, 2); I(PUTARG, 0);
          I(NIL); I(PUTARG, 1); I(ARG, 0); I(PUTARG, 2);
          I(CALL, 3); I(RETURN); }),
          N("foldl"), 
          F("<function>", 2, (() -> {
            I(GLOBAL, 0); I(PREP, 2); I(FVAR, 1); I(PREP, 1);
            I(ARG, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
            I(ARG, 0); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
            N(":"))),
        N("_assemble")),
      F("lookup", 2, (() -> {
        I(ARG, 1); I(TRAP, 37); I(QUOTE, 0); I(MPRIM, 5);
        I(POP); I(POP); I(BIND, 0); I(POP);
        I(POP); I(GLOBAL, 1); I(PREP, 2); I(ARG, 0);
        I(PUTARG, 0); I(GLOBAL, 2); I(PREP, 1); I(LOCAL, 0);
        I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1); I(CALL, 2);
        I(RETURN); I(FAIL); }),
        N("env"), N("_assoc"), N("_get")),
      F("empty", 0, (() -> {
        I(QUOTE, 0); I(PREP, 5); I(PUSH, 0); I(PUTARG, 0);
        I(PUSH, 0); I(PUTARG, 1); I(GLOBAL, 1); I(PREP, 1);
        I(NIL); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 2);
        I(GLOBAL, 1); I(PREP, 1); I(NIL); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 3); I(GLOBAL, 1); I(PREP, 1);
        I(PUSH, 0); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 4);
        I(CALL, 5); I(RETURN); }),
        N("env"), N("_new")),
      F("newblock", 3, (() -> {
        I(ARG, 2); I(TRAP, 166); I(QUOTE, 0); I(MPRIM, 5);
        I(POP); I(POP); I(BIND, 0); I(POP);
        I(BIND, 1); I(GLOBAL, 1); I(PREP, 2); I(ARG, 0);
        I(PUTARG, 0); I(QUOTE, 2); I(PUTARG, 1); I(CALL, 2);
        I(JFALSE, 34); I(NIL); I(JUMP, 82); I(GLOBAL, 3);
        I(PREP, 2); I(ARG, 0); I(PUTARG, 0); I(GLOBAL, 3);
        I(PREP, 2); I(GLOBAL, 4); I(PREP, 2); I(LOCAL, 1);
        I(PUTARG, 0); I(PUSH, 1); I(PUTARG, 1); I(CALL, 2);
        I(PUTARG, 0); I(QUOTE, 5); I(PREP, 1); I(PUSH, 0);
        I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1); I(CALL, 2);
        I(PUTARG, 1); I(CALL, 2); I(NIL); I(CONS);
        I(BIND, 2); I(QUOTE, 0); I(PREP, 5); I(GLOBAL, 4);
        I(PREP, 2); I(LOCAL, 1); I(PUTARG, 0); I(PUSH, 1);
        I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(GLOBAL, 6); I(PREP, 1); I(GLOBAL, 7);
        I(PREP, 2); I(LOCAL, 2); I(PUTARG, 0); I(GLOBAL, 8);
        I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1);
        I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0); I(CALL, 1);
        I(PUTARG, 2); I(GLOBAL, 6); I(PREP, 1); I(NIL);
        I(PUTARG, 0); I(CALL, 1); I(PUTARG, 3); I(GLOBAL, 6);
        I(PREP, 1); I(PUSH, 0); I(PUTARG, 0); I(CALL, 1);
        I(PUTARG, 4); I(CALL, 5); I(RETURN); I(FAIL); }),
        N("env"), N("="), S("<function>"), N("_pair"),
        N("+"), N("FVAR"), N("_new"), N("++"),
        N("_get")),
      F("e_level", 1, (() -> {
        I(ARG, 0); I(TRAP, 17); I(QUOTE, 0); I(MPRIM, 5);
        I(POP); I(POP); I(POP); I(POP);
        I(BIND, 0); I(LOCAL, 0); I(RETURN); I(FAIL); }),
        N("env")),
      F("e_arity", 1, (() -> {
        I(ARG, 0); I(TRAP, 17); I(QUOTE, 0); I(MPRIM, 5);
        I(POP); I(POP); I(POP); I(BIND, 0);
        I(POP); I(LOCAL, 0); I(RETURN); I(FAIL); }),
        N("env")),
      F("e_fvars", 1, (() -> {
        I(ARG, 0); I(TRAP, 25); I(QUOTE, 0); I(MPRIM, 5);
        I(POP); I(BIND, 0); I(POP); I(POP);
        I(POP); I(GLOBAL, 1); I(PREP, 1); I(LOCAL, 0);
        I(PUTARG, 0); I(CALL, 1); I(RETURN); I(FAIL); }),
        N("env"), N("_get")),
      F("e_size", 1, (() -> {
        I(ARG, 0); I(TRAP, 25); I(QUOTE, 0); I(MPRIM, 5);
        I(BIND, 0); I(POP); I(POP); I(POP);
        I(POP); I(GLOBAL, 1); I(PREP, 1); I(LOCAL, 0);
        I(PUTARG, 0); I(CALL, 1); I(RETURN); I(FAIL); }),
        N("env"), N("_get")),
      F("inc_size", 2, (() -> {
        I(ARG, 0); I(TRAP, 49); I(QUOTE, 0); I(MPRIM, 5);
        I(BIND, 0); I(POP); I(POP); I(POP);
        I(POP); I(GLOBAL, 1); I(PREP, 2); I(LOCAL, 0);
        I(PUTARG, 0); I(GLOBAL, 2); I(PREP, 2); I(GLOBAL, 3);
        I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1);
        I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1); I(CALL, 2);
        I(PUTARG, 1); I(CALL, 2); I(RETURN); I(FAIL); }),
        N("env"), N("_set"), N("+"), N("_get")),
      F("bind", 3, (() -> {
        I(ARG, 2); I(TRAP, 74); I(QUOTE, 0); I(MPRIM, 5);
        I(POP); I(POP); I(BIND, 0); I(POP);
        I(BIND, 1); I(GLOBAL, 1); I(PREP, 2); I(LOCAL, 0);
        I(PUTARG, 0); I(GLOBAL, 2); I(PREP, 2); I(GLOBAL, 3);
        I(PREP, 2); I(ARG, 0); I(PUTARG, 0); I(GLOBAL, 3);
        I(PREP, 2); I(LOCAL, 1); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1); I(CALL, 2);
        I(PUTARG, 0); I(GLOBAL, 4); I(PREP, 1); I(LOCAL, 0);
        I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1); I(CALL, 2);
        I(PUTARG, 1); I(CALL, 2); I(RETURN); I(FAIL); }),
        N("env"), N("_set"), N(":"), N("_pair"),
        N("_get")),
      F("unbind", 2, (() -> {
        I(ARG, 1); I(TRAP, 57); I(QUOTE, 0); I(MPRIM, 5);
        I(POP); I(POP); I(BIND, 0); I(POP);
        I(POP); I(QUOTE, 1); I(FRAME, 2); I(ARG, 0);
        I(PUTARG, 1); I(CLOSURE, 2); I(BIND, 1); I(GLOBAL, 2);
        I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0); I(LOCAL, 1);
        I(PREP, 1); I(GLOBAL, 3); I(PREP, 1); I(LOCAL, 0);
        I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0); I(CALL, 1);
        I(PUTARG, 1); I(CALL, 2); I(RETURN); I(FAIL); }),
        N("env"), 
        F("h", 1, (() -> {
          I(ARG, 0); I(TRAP, 59); I(MCONS); I(BIND, 0);
          I(BIND, 1); I(GLOBAL, 0); I(PREP, 2); I(FVAR, 1);
          I(PUTARG, 0); I(GLOBAL, 1); I(PREP, 1); I(LOCAL, 0);
          I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1); I(CALL, 2);
          I(JFALSE, 36); I(LOCAL, 1); I(RETURN); I(GLOBAL, 2);
          I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0); I(FVAR, 0);
          I(PREP, 1); I(LOCAL, 1); I(PUTARG, 0); I(CALL, 1);
          I(PUTARG, 1); I(CALL, 2); I(RETURN); I(ARG, 0);
          I(TRAP, 66); I(MNIL); I(NIL); I(RETURN);
          I(FAIL); }),
          N("="), N("_fst"), N(":")),
        N("_set"), N("_get")),
      F("alloc", 2, (() -> {
        I(FVAR, 1); I(PREP, 1); I(ARG, 1); I(PUTARG, 0);
        I(CALL, 1); I(BIND, 0); I(FVAR, 2); I(PREP, 3);
        I(ARG, 0); I(PUTARG, 0); I(QUOTE, 0); I(PREP, 1);
        I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1);
        I(ARG, 1); I(PUTARG, 2); I(CALL, 3); I(POP);
        I(FVAR, 3); I(PREP, 2); I(ARG, 1); I(PUTARG, 0);
        I(PUSH, 1); I(PUTARG, 1); I(CALL, 2); I(POP);
        I(LOCAL, 0); I(RETURN); }),
        N("LOCAL")),
      F("dealloc", 2, (() -> {
        I(FVAR, 1); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
        I(ARG, 1); I(PUTARG, 1); I(CALL, 2); I(POP);
        I(FVAR, 2); I(PREP, 2); I(ARG, 1); I(PUTARG, 0);
        I(PUSH, -1); I(PUTARG, 1); I(CALL, 2); I(RETURN); })),
      F("alloc_fv", 2, (() -> {
        I(ARG, 1); I(TRAP, 86); I(QUOTE, 0); I(MPRIM, 5);
        I(POP); I(BIND, 0); I(POP); I(POP);
        I(POP); I(GLOBAL, 1); I(PREP, 2); I(GLOBAL, 2);
        I(PREP, 1); I(GLOBAL, 3); I(PREP, 1); I(LOCAL, 0);
        I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0); I(CALL, 1);
        I(PUTARG, 0); I(PUSH, 1); I(PUTARG, 1); I(CALL, 2);
        I(BIND, 1); I(GLOBAL, 4); I(PREP, 2); I(LOCAL, 0);
        I(PUTARG, 0); I(GLOBAL, 5); I(PREP, 2); I(GLOBAL, 3);
        I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1);
        I(PUTARG, 0); I(ARG, 0); I(NIL); I(CONS);
        I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1); I(CALL, 2);
        I(POP); I(LOCAL, 1); I(RETURN); I(FAIL); }),
        N("env"), N("+"), N("length"), N("_get"),
        N("_set"), N("++")),
      F("islocal", 2, (() -> {
        I(QUOTE, 0); I(FRAME, 3); I(FVAR, 1); I(PUTARG, 1);
        I(ARG, 1); I(PUTARG, 2); I(CLOSURE, 3); I(BIND, 0);
        I(LOCAL, 0); I(PREP, 1); I(FVAR, 2); I(PREP, 2);
        I(ARG, 0); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(CALL, 2); I(PUTARG, 0); I(CALL, 1); I(RETURN); }),
        F("case", 1, (() -> {
          I(ARG, 0); I(TRAP, 37); I(MPAIR); I(BIND, 0);
          I(TRAP, 37); I(QUOTE, 0); I(MPRIM, 1); I(POP);
          I(GLOBAL, 1); I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0);
          I(FVAR, 1); I(PREP, 1); I(FVAR, 2); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 1); I(CALL, 2); I(RETURN);
          I(ARG, 0); I(TRAP, 74); I(MPAIR); I(BIND, 0);
          I(TRAP, 74); I(QUOTE, 2); I(MPRIM, 1); I(POP);
          I(GLOBAL, 1); I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0);
          I(FVAR, 1); I(PREP, 1); I(FVAR, 2); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 1); I(CALL, 2); I(RETURN);
          I(GLOBAL, 3); I(RETURN); }),
          N("LOCAL"), N("="), N("ARG"), N("false"))),
      F("selfrec", 2, (() -> {
        I(GLOBAL, 0); I(PREP, 2); I(FVAR, 2); I(PREP, 2);
        I(ARG, 0); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(CALL, 2); I(PUTARG, 0); I(FVAR, 1); I(PREP, 1);
        I(ARG, 1); I(PUTARG, 0); I(CALL, 1); I(QUOTE, 1);
        I(PREP, 1); I(PUSH, 0); I(PUTARG, 0); I(CALL, 1);
        I(NIL); I(CONS); I(CONS); I(PUTARG, 1);
        I(CALL, 2); I(RETURN); }),
        N("="), N("FVAR")),
      F("reset", 1, (() -> {
        I(ARG, 0); I(TRAP, 78); I(QUOTE, 0); I(MPRIM, 5);
        I(BIND, 0); I(POP); I(BIND, 1); I(POP);
        I(BIND, 2); I(QUOTE, 1); I(FRAME, 2); I(LOCAL, 2);
        I(PUTARG, 1); I(CLOSURE, 2); I(BIND, 3); I(GLOBAL, 2);
        I(PREP, 2); I(LOCAL, 1); I(PUTARG, 0); I(GLOBAL, 3);
        I(PREP, 2); I(LOCAL, 3); I(PUTARG, 0); I(GLOBAL, 4);
        I(PREP, 1); I(LOCAL, 1); I(PUTARG, 0); I(CALL, 1);
        I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1); I(CALL, 2);
        I(POP); I(GLOBAL, 2); I(PREP, 2); I(LOCAL, 0);
        I(PUTARG, 0); I(PUSH, 0); I(PUTARG, 1); I(CALL, 2);
        I(RETURN); I(FAIL); }),
        N("env"), 
        F("h", 1, (() -> {
          I(ARG, 0); I(TRAP, 33); I(MPAIR); I(POP);
          I(TRAP, 33); I(MPAIR); I(BIND, 0); I(TRAP, 33);
          I(QUOTE, 0); I(MPRIM, 1); I(POP); I(GLOBAL, 1);
          I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0); I(FVAR, 1);
          I(PUTARG, 1); I(CALL, 2); I(RETURN); I(ARG, 0);
          I(TRAP, 66); I(MPAIR); I(POP); I(TRAP, 66);
          I(MPAIR); I(BIND, 0); I(TRAP, 66); I(QUOTE, 2);
          I(MPRIM, 1); I(POP); I(GLOBAL, 1); I(PREP, 2);
          I(LOCAL, 0); I(PUTARG, 0); I(FVAR, 1); I(PUTARG, 1);
          I(CALL, 2); I(RETURN); I(GLOBAL, 3); I(RETURN); }),
          N("LOCAL"), N("<"), N("ARG"), N("true")),
        N("_set"), N("filter"), N("_get")),
      F("label", 0, (() -> {
        I(GLOBAL, 0); I(PREP, 2); I(FVAR, 1); I(PUTARG, 0);
        I(GLOBAL, 1); I(PREP, 2); I(GLOBAL, 2); I(PREP, 1);
        I(FVAR, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
        I(PUSH, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1);
        I(CALL, 2); I(RETURN); }),
        N("_set"), N("+"), N("_get")),
      F("c_ref", 2, (() -> {
        I(QUOTE, 0); I(FRAME, 6); I(FVAR, 3); I(PUTARG, 1);
        I(ARG, 1); I(PUTARG, 2); I(FVAR, 2); I(PUTARG, 3);
        I(ARG, 0); I(PUTARG, 4); I(FVAR, 1); I(PUTARG, 5);
        I(CLOSURE, 6); I(BIND, 0); I(LOCAL, 0); I(PREP, 1);
        I(FVAR, 4); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
        I(ARG, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0);
        I(CALL, 1); I(RETURN); }),
        F("case", 1, (() -> {
          I(ARG, 0); I(TRAP, 26); I(MPAIR); I(BIND, 0);
          I(TRAP, 26); I(QUOTE, 0); I(MPRIM, 1); I(BIND, 1);
          I(QUOTE, 0); I(PREP, 1); I(LOCAL, 1); I(PUTARG, 0);
          I(CALL, 1); I(RETURN); I(ARG, 0); I(TRAP, 62);
          I(MPAIR); I(BIND, 0); I(BIND, 1); I(GLOBAL, 1);
          I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0); I(FVAR, 1);
          I(PREP, 1); I(FVAR, 2); I(PUTARG, 0); I(CALL, 1);
          I(PUTARG, 1); I(CALL, 2); I(JFALSE, 62); I(LOCAL, 1);
          I(RETURN); I(ARG, 0); I(TRAP, 123); I(MPAIR);
          I(POP); I(POP); I(FVAR, 3); I(PREP, 2);
          I(FVAR, 4); I(PUTARG, 0); I(FVAR, 2); I(PUTARG, 1);
          I(CALL, 2); I(BIND, 0); I(FVAR, 5); I(PREP, 3);
          I(FVAR, 4); I(PUTARG, 0); I(QUOTE, 2); I(PREP, 1);
          I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1);
          I(FVAR, 2); I(PUTARG, 2); I(CALL, 3); I(POP);
          I(QUOTE, 2); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
          I(CALL, 1); I(RETURN); I(QUOTE, 3); I(PREP, 1);
          I(FVAR, 4); I(PUTARG, 0); I(CALL, 1); I(RETURN); }),
          N("QUOTE"), N("="), N("FVAR"), N("GLOBAL"))),
      F("sortby", 2, (() -> {
        I(QUOTE, 0); I(FRAME, 2); I(ARG, 0); I(PUTARG, 1);
        I(CLOSURE, 2); I(BIND, 0); I(GLOBAL, 1); I(PREP, 3);
        I(LOCAL, 0); I(PUTARG, 0); I(NIL); I(PUTARG, 1);
        I(ARG, 1); I(PUTARG, 2); I(CALL, 3); I(RETURN); }),
        F("insert", 2, (() -> {
          I(ARG, 1); I(TRAP, 10); I(MNIL); I(ARG, 0);
          I(NIL); I(CONS); I(RETURN); I(ARG, 1);
          I(TRAP, 105); I(MCONS); I(BIND, 0); I(BIND, 1);
          I(GLOBAL, 0); I(PREP, 2); I(FVAR, 1); I(PREP, 1);
          I(ARG, 0); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
          I(FVAR, 1); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
          I(CALL, 1); I(PUTARG, 1); I(CALL, 2); I(JFALSE, 78);
          I(GLOBAL, 1); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
          I(GLOBAL, 1); I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0);
          I(LOCAL, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1);
          I(CALL, 2); I(RETURN); I(GLOBAL, 1); I(PREP, 2);
          I(LOCAL, 0); I(PUTARG, 0); I(FVAR, 0); I(PREP, 2);
          I(ARG, 0); I(PUTARG, 0); I(LOCAL, 1); I(PUTARG, 1);
          I(CALL, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN);
          I(FAIL); }),
          N("<="), N(":")),
        N("foldr")),
      F("c_exp", 3, (() -> {
        I(GLOBAL, 0); I(PREP, 1); I(FVAR, 1); I(PUTARG, 0);
        I(CALL, 1); I(BIND, 0); I(LOCAL, 0); I(PREP, 3);
        I(ARG, 0); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(ARG, 2); I(PUTARG, 2); I(CALL, 3); I(RETURN); }),
        N("_get")),
      F("pgen", 2, (() -> {
        I(GLOBAL, 0); I(PREP, 2); I(QUOTE, 1); I(PREP, 1);
        I(ARG, 0); I(QUOTE, 1); I(PREP, 1); I(GLOBAL, 2);
        I(PREP, 2); I(GLOBAL, 3); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0); I(CALL, 1);
        I(NIL); I(CONS); I(CONS); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 0); I(GLOBAL, 4); I(PREP, 1);
        I(GLOBAL, 2); I(PREP, 2); I(GLOBAL, 5); I(PUTARG, 0);
        I(ARG, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
        N("_pair"), N("SEQ"), N("map"), N("_fst"),
        N("concat"), N("_snd")),
      F("pleaf", 1, (() -> {
        I(GLOBAL, 0); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
        I(NIL); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
        N("_pair")),
      F("trap", 2, (() -> {
        I(FVAR, 1); I(PREP, 0); I(CALL, 0); I(BIND, 0);
        I(GLOBAL, 0); I(PREP, 2); I(QUOTE, 1); I(PREP, 1);
        I(QUOTE, 2); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
        I(CALL, 1); I(GLOBAL, 3); I(PREP, 1); I(ARG, 1);
        I(PUTARG, 0); I(CALL, 1); I(NIL); I(CONS);
        I(CONS); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
        I(GLOBAL, 4); I(PREP, 2); I(GLOBAL, 0); I(PREP, 2);
        I(LOCAL, 0); I(PUTARG, 0); I(ARG, 0); I(PUTARG, 1);
        I(CALL, 2); I(PUTARG, 0); I(GLOBAL, 5); I(PREP, 1);
        I(ARG, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1);
        I(CALL, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
        N("_pair"), N("SEQ"), N("TRAP"), N("_fst"),
        N(":"), N("_snd")),
      F("c_patt", 3, (() -> {
        I(ARG, 0); I(TRAP, 62); I(QUOTE, 0); I(MPRIM, 1);
        I(BIND, 0); I(FVAR, 1); I(PREP, 0); I(CALL, 0);
        I(BIND, 1); I(FVAR, 2); I(PREP, 2); I(ARG, 1);
        I(PUTARG, 0); I(FVAR, 3); I(PREP, 1); I(QUOTE, 1);
        I(PREP, 1); I(QUOTE, 2); I(PREP, 1); I(LOCAL, 0);
        I(PUTARG, 0); I(CALL, 1); I(QUOTE, 3); I(NIL);
        I(CONS); I(CONS); I(PUTARG, 0); I(CALL, 1);
        I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1); I(CALL, 2);
        I(RETURN); I(ARG, 0); I(TRAP, 175); I(QUOTE, 4);
        I(MPRIM, 1); I(BIND, 0); I(FVAR, 1); I(PREP, 0);
        I(CALL, 0); I(BIND, 1); I(FVAR, 4); I(PREP, 2);
        I(LOCAL, 0); I(PUTARG, 0); I(ARG, 2); I(PUTARG, 1);
        I(CALL, 2); I(JFALSE, 144); I(FVAR, 2); I(PREP, 2);
        I(ARG, 1); I(PUTARG, 0); I(FVAR, 3); I(PREP, 1);
        I(QUOTE, 1); I(PREP, 1); I(FVAR, 5); I(PREP, 2);
        I(LOCAL, 0); I(PUTARG, 0); I(ARG, 2); I(PUTARG, 1);
        I(CALL, 2); I(QUOTE, 3); I(NIL); I(CONS);
        I(CONS); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 1); I(CALL, 2); I(RETURN);
        I(FVAR, 3); I(PREP, 1); I(QUOTE, 5); I(PREP, 1);
        I(FVAR, 6); I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0);
        I(ARG, 2); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 0); I(CALL, 1); I(RETURN);
        I(ARG, 0); I(TRAP, 193); I(QUOTE, 6); I(MEQ);
        I(FVAR, 3); I(PREP, 1); I(QUOTE, 7); I(PUTARG, 0);
        I(CALL, 1); I(RETURN); I(ARG, 0); I(TRAP, 302);
        I(QUOTE, 8); I(MPRIM, 2); I(TRAP, 301); I(MCONS);
        I(BIND, 0); I(TRAP, 301); I(MCONS); I(BIND, 1);
        I(TRAP, 301); I(MNIL); I(TRAP, 302); I(QUOTE, 4);
        I(MPRIM, 1); I(TRAP, 302); I(QUOTE, 9); I(MEQ);
        I(FVAR, 2); I(PREP, 2); I(ARG, 1); I(PUTARG, 0);
        I(FVAR, 7); I(PREP, 2); I(QUOTE, 10); I(PUTARG, 0);
        I(FVAR, 0); I(PREP, 3); I(LOCAL, 0); I(PUTARG, 0);
        I(GLOBAL, 11); I(PREP, 2); I(ARG, 1); I(PUTARG, 0);
        I(PUSH, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1);
        I(ARG, 2); I(PUTARG, 2); I(CALL, 3); I(FVAR, 0);
        I(PREP, 3); I(LOCAL, 1); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(ARG, 2); I(PUTARG, 2); I(CALL, 3);
        I(NIL); I(CONS); I(CONS); I(PUTARG, 1);
        I(CALL, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN);
        I(POP); I(ARG, 0); I(TRAP, 437); I(QUOTE, 8);
        I(MPRIM, 2); I(BIND, 0); I(BIND, 1); I(GLOBAL, 12);
        I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1);
        I(BIND, 2); I(FVAR, 2); I(PREP, 2); I(ARG, 1);
        I(PUTARG, 0); I(FVAR, 7); I(PREP, 2); I(QUOTE, 1);
        I(PREP, 1); I(FVAR, 9); I(PREP, 3); I(LOCAL, 1);
        I(PUTARG, 0); I(ARG, 2); I(PUTARG, 1); I(GLOBAL, 13);
        I(PUTARG, 2); I(CALL, 3); I(QUOTE, 14); I(PREP, 1);
        I(LOCAL, 2); I(PUTARG, 0); I(CALL, 1); I(NIL);
        I(CONS); I(CONS); I(PUTARG, 0); I(CALL, 1);
        I(PUTARG, 0); I(GLOBAL, 15); I(PREP, 1); I(GLOBAL, 16);
        I(PREP, 3); I(QUOTE, 17); I(FRAME, 3); I(FVAR, 0);
        I(PUTARG, 1); I(ARG, 2); I(PUTARG, 2); I(CLOSURE, 3);
        I(PUTARG, 0); I(FVAR, 8); I(PREP, 2); I(ARG, 1);
        I(PUTARG, 0); I(LOCAL, 0); I(PUTARG, 1); I(CALL, 2);
        I(PUTARG, 1); I(NIL); I(PUTARG, 2); I(CALL, 3);
        I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1); I(CALL, 2);
        I(PUTARG, 1); I(CALL, 2); I(RETURN); I(ARG, 0);
        I(TRAP, 525); I(QUOTE, 18); I(MPRIM, 2); I(BIND, 0);
        I(BIND, 1); I(FVAR, 2); I(PREP, 2); I(ARG, 1);
        I(PUTARG, 0); I(FVAR, 7); I(PREP, 2); I(QUOTE, 19);
        I(PUTARG, 0); I(FVAR, 0); I(PREP, 3); I(LOCAL, 1);
        I(PUTARG, 0); I(GLOBAL, 11); I(PREP, 2); I(ARG, 1);
        I(PUTARG, 0); I(PUSH, 1); I(PUTARG, 1); I(CALL, 2);
        I(PUTARG, 1); I(ARG, 2); I(PUTARG, 2); I(CALL, 3);
        I(FVAR, 0); I(PREP, 3); I(LOCAL, 0); I(PUTARG, 0);
        I(ARG, 1); I(PUTARG, 1); I(ARG, 2); I(PUTARG, 2);
        I(CALL, 3); I(NIL); I(CONS); I(CONS);
        I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1); I(CALL, 2);
        I(RETURN); I(ARG, 0); I(TRAP, 555); I(QUOTE, 20);
        I(MEQ); I(FVAR, 2); I(PREP, 2); I(ARG, 1);
        I(PUTARG, 0); I(FVAR, 3); I(PREP, 1); I(QUOTE, 21);
        I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1); I(CALL, 2);
        I(RETURN); I(ARG, 0); I(TRAP, 592); I(QUOTE, 22);
        I(MPRIM, 1); I(BIND, 0); I(FVAR, 0); I(PREP, 3);
        I(FVAR, 10); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(ARG, 2); I(PUTARG, 2); I(CALL, 3); I(RETURN);
        I(ARG, 0); I(TRAP, 657); I(QUOTE, 23); I(MPRIM, 2);
        I(BIND, 0); I(BIND, 1); I(FVAR, 2); I(PREP, 2);
        I(ARG, 1); I(PUTARG, 0); I(FVAR, 7); I(PREP, 2);
        I(QUOTE, 24); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 0); I(FVAR, 0); I(PREP, 3);
        I(LOCAL, 1); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(ARG, 2); I(PUTARG, 2); I(CALL, 3); I(NIL);
        I(CONS); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1);
        I(CALL, 2); I(RETURN); I(FAIL); }),
        N("const"), N("SEQ"), N("QUOTE"), N("MEQ"),
        N("var"), N("BIND"), N("anon"), N("POP"),
        N("prim"), N("_pair"), N("MPAIR"), N("+"),
        N("length"), N("false"), N("MPRIM"), N("reverse"),
        N("_mapa"), 
        F("<function>", 2, (() -> {
          I(ARG, 0); I(TRAP, 37); I(MCONS); I(BIND, 0);
          I(TRAP, 37); I(MCONS); I(BIND, 1); I(TRAP, 37);
          I(MNIL); I(FVAR, 1); I(PREP, 3); I(LOCAL, 1);
          I(PUTARG, 0); I(LOCAL, 0); I(PUTARG, 1); I(FVAR, 2);
          I(PUTARG, 2); I(CALL, 3); I(ARG, 1); I(CONS);
          I(RETURN); I(ARG, 1); I(RETURN); })),
        N("cons"), N("MCONS"), N("nil"), N("MNIL"),
        N("list"), N("plus"), N("MPLUS")),
      F("c_arg", 3, (() -> {
        I(ARG, 1); I(TRAP, 72); I(QUOTE, 0); I(MPRIM, 1);
        I(BIND, 0); I(GLOBAL, 1); I(PREP, 1); I(FVAR, 1);
        I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0); I(ARG, 2);
        I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0); I(CALL, 1);
        I(JFALSE, 72); I(FVAR, 2); I(PREP, 3); I(LOCAL, 0);
        I(PUTARG, 0); I(QUOTE, 2); I(PREP, 1); I(ARG, 0);
        I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1); I(ARG, 2);
        I(PUTARG, 2); I(CALL, 3); I(POP); I(FVAR, 3);
        I(PREP, 1); I(QUOTE, 3); I(PUTARG, 0); I(CALL, 1);
        I(RETURN); I(ARG, 1); I(TRAP, 90); I(QUOTE, 4);
        I(MEQ); I(FVAR, 3); I(PREP, 1); I(QUOTE, 3);
        I(PUTARG, 0); I(CALL, 1); I(RETURN); I(FVAR, 4);
        I(PREP, 2); I(QUOTE, 2); I(PREP, 1); I(ARG, 0);
        I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0); I(FVAR, 5);
        I(PREP, 3); I(ARG, 1); I(PUTARG, 0); I(PUSH, 0);
        I(PUTARG, 1); I(ARG, 2); I(PUTARG, 2); I(CALL, 3);
        I(NIL); I(CONS); I(PUTARG, 1); I(CALL, 2);
        I(RETURN); }),
        N("var"), N("not"), N("ARG"), N("NOP"),
        N("anon")),
      F("c_match", 2, (() -> {
        I(QUOTE, 0); I(FRAME, 3); I(FVAR, 1); I(PUTARG, 1);
        I(ARG, 1); I(PUTARG, 2); I(CLOSURE, 3); I(BIND, 0);
        I(LOCAL, 0); I(PREP, 2); I(PUSH, 0); I(PUTARG, 0);
        I(ARG, 0); I(PUTARG, 1); I(CALL, 2); I(BIND, 1);
        I(GLOBAL, 1); I(PREP, 2); I(QUOTE, 2); I(PREP, 1);
        I(GLOBAL, 3); I(PREP, 2); I(GLOBAL, 4); I(PUTARG, 0);
        I(LOCAL, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 0); I(GLOBAL, 5); I(PREP, 1);
        I(GLOBAL, 3); I(PREP, 2); I(GLOBAL, 6); I(PUTARG, 0);
        I(LOCAL, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
        F("compile", 2, (() -> {
          I(ARG, 1); I(TRAP, 7); I(MNIL); I(NIL);
          I(RETURN); I(ARG, 1); I(TRAP, 75); I(MCONS);
          I(BIND, 0); I(BIND, 1); I(FVAR, 1); I(PREP, 3);
          I(ARG, 0); I(PUTARG, 0); I(LOCAL, 0); I(PUTARG, 1);
          I(FVAR, 2); I(PUTARG, 2); I(CALL, 3); I(BIND, 2);
          I(GLOBAL, 0); I(PREP, 2); I(LOCAL, 2); I(PUTARG, 0);
          I(FVAR, 0); I(PREP, 2); I(GLOBAL, 1); I(PREP, 2);
          I(ARG, 0); I(PUTARG, 0); I(PUSH, 1); I(PUTARG, 1);
          I(CALL, 2); I(PUTARG, 0); I(LOCAL, 1); I(PUTARG, 1);
          I(CALL, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN);
          I(FAIL); }),
          N(":"), N("+")),
        N("_pair"), N("SEQ"), N("map"), N("_fst"),
        N("concat"), N("_snd")),
      F("c_rule", 2, (() -> {
        I(ARG, 0); I(TRAP, 103); I(QUOTE, 0); I(MPRIM, 2);
        I(BIND, 0); I(BIND, 1); I(FVAR, 1); I(PREP, 2);
        I(LOCAL, 1); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(CALL, 2); I(BIND, 2); I(FVAR, 2); I(PREP, 3);
        I(LOCAL, 0); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(GLOBAL, 1); I(PUTARG, 2); I(CALL, 3); I(BIND, 3);
        I(FVAR, 3); I(PREP, 1); I(ARG, 1); I(PUTARG, 0);
        I(CALL, 1); I(POP); I(GLOBAL, 2); I(PREP, 2);
        I(QUOTE, 3); I(PREP, 1); I(GLOBAL, 4); I(PREP, 1);
        I(LOCAL, 2); I(PUTARG, 0); I(CALL, 1); I(LOCAL, 3);
        I(NIL); I(CONS); I(CONS); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 0); I(GLOBAL, 5); I(PREP, 1);
        I(LOCAL, 2); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1);
        I(CALL, 2); I(RETURN); I(ARG, 0); I(TRAP, 274);
        I(QUOTE, 6); I(MPRIM, 3); I(BIND, 0); I(BIND, 1);
        I(BIND, 2); I(FVAR, 4); I(PREP, 0); I(CALL, 0);
        I(BIND, 3); I(FVAR, 1); I(PREP, 2); I(LOCAL, 2);
        I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1); I(CALL, 2);
        I(BIND, 4); I(FVAR, 2); I(PREP, 3); I(LOCAL, 1);
        I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1); I(GLOBAL, 7);
        I(PUTARG, 2); I(CALL, 3); I(BIND, 5); I(FVAR, 2);
        I(PREP, 3); I(LOCAL, 0); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(GLOBAL, 1); I(PUTARG, 2); I(CALL, 3);
        I(BIND, 6); I(FVAR, 3); I(PREP, 1); I(ARG, 1);
        I(PUTARG, 0); I(CALL, 1); I(POP); I(GLOBAL, 2);
        I(PREP, 2); I(QUOTE, 3); I(PREP, 1); I(GLOBAL, 4);
        I(PREP, 1); I(LOCAL, 4); I(PUTARG, 0); I(CALL, 1);
        I(LOCAL, 5); I(QUOTE, 8); I(PREP, 1); I(LOCAL, 3);
        I(PUTARG, 0); I(CALL, 1); I(LOCAL, 6); I(NIL);
        I(CONS); I(CONS); I(CONS); I(CONS);
        I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0); I(GLOBAL, 9);
        I(PREP, 2); I(GLOBAL, 2); I(PREP, 2); I(LOCAL, 3);
        I(PUTARG, 0); I(PUSH, 0); I(PUTARG, 1); I(CALL, 2);
        I(PUTARG, 0); I(GLOBAL, 5); I(PREP, 1); I(LOCAL, 4);
        I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1); I(CALL, 2);
        I(PUTARG, 1); I(CALL, 2); I(RETURN); I(FAIL); }),
        N("rule"), N("true"), N("_pair"), N("SEQ"),
        N("_fst"), N("_snd"), N("crule"), N("false"),
        N("JFALSE"), N(":")),
      F("c_traps", 1, (() -> {
        I(QUOTE, 0); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 0);
        I(QUOTE, 1); I(PREP, 1); I(LOCAL, 0); I(PREP, 3);
        I(PUSH, 0); I(PUTARG, 0); I(ARG, 0); I(PUTARG, 1);
        I(NIL); I(PUTARG, 2); I(CALL, 3); I(PUTARG, 0);
        I(CALL, 1); I(RETURN); }),
        F("h", 3, (() -> {
          I(ARG, 1); I(TRAP, 8); I(MNIL); I(ARG, 2);
          I(RETURN); I(ARG, 1); I(TRAP, 135); I(MCONS);
          I(BIND, 0); I(BIND, 1); I(GLOBAL, 0); I(PREP, 2);
          I(ARG, 0); I(PUTARG, 0); I(GLOBAL, 1); I(PREP, 1);
          I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1);
          I(CALL, 2); I(JFALSE, 80); I(FVAR, 0); I(PREP, 3);
          I(ARG, 0); I(PUTARG, 0); I(LOCAL, 1); I(PUTARG, 1);
          I(GLOBAL, 2); I(PREP, 2); I(GLOBAL, 3); I(PREP, 1);
          I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
          I(ARG, 2); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 2);
          I(CALL, 3); I(RETURN); I(FVAR, 0); I(PREP, 3);
          I(GLOBAL, 4); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
          I(PUSH, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0);
          I(GLOBAL, 2); I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0);
          I(LOCAL, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1);
          I(GLOBAL, 2); I(PREP, 2); I(QUOTE, 5); I(PUTARG, 0);
          I(ARG, 2); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 2);
          I(CALL, 3); I(RETURN); I(FAIL); }),
          N("="), N("_snd"), N(":"), N("_fst"),
          N("+"), N("POP")),
        N("SEQ")),
      F("c_body", 2, (() -> {
        I(ARG, 0); I(TRAP, 8); I(MNIL); I(QUOTE, 0);
        I(RETURN); I(ARG, 0); I(TRAP, 133); I(MCONS);
        I(BIND, 0); I(BIND, 1); I(FVAR, 1); I(PREP, 2);
        I(LOCAL, 0); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(CALL, 2); I(BIND, 2); I(FVAR, 2); I(PREP, 2);
        I(GLOBAL, 1); I(PUTARG, 0); I(GLOBAL, 1); I(PREP, 1);
        I(LOCAL, 2); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1);
        I(CALL, 2); I(BIND, 3); I(QUOTE, 2); I(PREP, 1);
        I(GLOBAL, 3); I(PREP, 1); I(LOCAL, 2); I(PUTARG, 0);
        I(CALL, 1); I(GLOBAL, 4); I(PREP, 2); I(LOCAL, 3);
        I(PUTARG, 0); I(NIL); I(PUTARG, 1); I(CALL, 2);
        I(JFALSE, 90); I(QUOTE, 5); I(JUMP, 125); I(QUOTE, 2);
        I(PREP, 1); I(FVAR, 3); I(PREP, 1); I(LOCAL, 3);
        I(PUTARG, 0); I(CALL, 1); I(FVAR, 0); I(PREP, 2);
        I(LOCAL, 1); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(CALL, 2); I(NIL); I(CONS); I(CONS);
        I(PUTARG, 0); I(CALL, 1); I(NIL); I(CONS);
        I(CONS); I(PUTARG, 0); I(CALL, 1); I(RETURN);
        I(FAIL); }),
        N("FAIL"), N("_snd"), N("SEQ"), N("_fst"),
        N("="), N("NOP")),
      F("c_closure", 4, (() -> {
        I(FVAR, 1); I(PREP, 3); I(ARG, 0); I(PUTARG, 0);
        I(ARG, 1); I(PUTARG, 1); I(ARG, 3); I(PUTARG, 2);
        I(CALL, 3); I(BIND, 0); I(FVAR, 2); I(PREP, 1);
        I(FVAR, 3); I(PREP, 2); I(ARG, 2); I(PUTARG, 0);
        I(LOCAL, 0); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0);
        I(CALL, 1); I(BIND, 1); I(FVAR, 4); I(PREP, 1);
        I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(BIND, 2);
        I(GLOBAL, 0); I(PREP, 1); I(LOCAL, 2); I(PUTARG, 0);
        I(CALL, 1); I(BIND, 3); I(FVAR, 5); I(PREP, 2);
        I(PUSH, 1); I(PUTARG, 0); I(LOCAL, 1); I(PUTARG, 1);
        I(CALL, 2); I(POP); I(QUOTE, 1); I(PREP, 1);
        I(QUOTE, 2); I(PREP, 1); I(FVAR, 6); I(PREP, 3);
        I(ARG, 0); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(LOCAL, 1); I(PUTARG, 2); I(CALL, 3); I(PUTARG, 0);
        I(CALL, 1); I(QUOTE, 3); I(PREP, 1); I(GLOBAL, 4);
        I(PREP, 2); I(LOCAL, 3); I(PUTARG, 0); I(PUSH, 1);
        I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0); I(CALL, 1);
        I(QUOTE, 1); I(PREP, 1); I(GLOBAL, 5); I(PREP, 3);
        I(QUOTE, 6); I(FRAME, 3); I(FVAR, 8); I(PUTARG, 1);
        I(ARG, 3); I(PUTARG, 2); I(CLOSURE, 3); I(PUTARG, 0);
        I(FVAR, 7); I(PREP, 2); I(PUSH, 1); I(PUTARG, 0);
        I(LOCAL, 2); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1);
        I(NIL); I(PUTARG, 2); I(CALL, 3); I(PUTARG, 0);
        I(CALL, 1); I(QUOTE, 7); I(PREP, 1); I(GLOBAL, 4);
        I(PREP, 2); I(LOCAL, 3); I(PUTARG, 0); I(PUSH, 1);
        I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0); I(CALL, 1);
        I(NIL); I(CONS); I(CONS); I(CONS);
        I(CONS); I(PUTARG, 0); I(CALL, 1); I(RETURN); }),
        N("length"), N("SEQ"), N("QUOTE"), N("FRAME"),
        N("+"), N("_mapa"), 
        F("<function>", 2, (() -> {
          I(ARG, 0); I(TRAP, 54); I(MCONS); I(BIND, 0);
          I(TRAP, 54); I(MCONS); I(BIND, 1); I(TRAP, 54);
          I(MNIL); I(QUOTE, 0); I(PREP, 1); I(FVAR, 1);
          I(PREP, 2); I(LOCAL, 1); I(PUTARG, 0); I(FVAR, 2);
          I(PUTARG, 1); I(CALL, 2); I(QUOTE, 1); I(PREP, 1);
          I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(NIL);
          I(CONS); I(CONS); I(PUTARG, 0); I(CALL, 1);
          I(ARG, 1); I(CONS); I(RETURN); I(ARG, 1);
          I(RETURN); }),
          N("SEQ"), N("PUTARG")),
        N("CLOSURE")),
      F("yield", 2, (() -> {
        I(ARG, 1); I(JFALSE, 20); I(QUOTE, 0); I(PREP, 1);
        I(ARG, 0); I(QUOTE, 1); I(NIL); I(CONS);
        I(CONS); I(PUTARG, 0); I(CALL, 1); I(RETURN);
        I(ARG, 0); I(RETURN); }),
        N("SEQ"), N("RETURN")),
      F("c_expr", 3, (() -> {
        I(ARG, 0); I(TRAP, 73); I(QUOTE, 0); I(MPRIM, 1);
        I(BIND, 0); I(GLOBAL, 1); I(PREP, 1); I(LOCAL, 0);
        I(PUTARG, 0); I(CALL, 1); I(JFALSE, 46); I(GLOBAL, 2);
        I(PREP, 2); I(GLOBAL, 3); I(PREP, 1); I(LOCAL, 0);
        I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0); I(LOCAL, 0);
        I(PUTARG, 1); I(CALL, 2); I(JUMP, 48); I(QUOTE, 4);
        I(JFALSE, 73); I(FVAR, 1); I(PREP, 2); I(QUOTE, 5);
        I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1);
        I(PUTARG, 0); I(ARG, 2); I(PUTARG, 1); I(CALL, 2);
        I(RETURN); I(ARG, 0); I(TRAP, 106); I(QUOTE, 0);
        I(MPRIM, 1); I(BIND, 0); I(FVAR, 1); I(PREP, 2);
        I(QUOTE, 6); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 0); I(ARG, 2); I(PUTARG, 1);
        I(CALL, 2); I(RETURN); I(ARG, 0); I(TRAP, 143);
        I(QUOTE, 7); I(MPRIM, 1); I(BIND, 0); I(FVAR, 1);
        I(PREP, 2); I(FVAR, 2); I(PREP, 2); I(LOCAL, 0);
        I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1); I(CALL, 2);
        I(PUTARG, 0); I(ARG, 2); I(PUTARG, 1); I(CALL, 2);
        I(RETURN); I(ARG, 0); I(TRAP, 288); I(QUOTE, 8);
        I(MPRIM, 2); I(BIND, 0); I(TRAP, 288); I(QUOTE, 7);
        I(MPRIM, 1); I(BIND, 1); I(ARG, 2); I(JFALSE, 181);
        I(FVAR, 3); I(PREP, 2); I(LOCAL, 1); I(PUTARG, 0);
        I(ARG, 1); I(PUTARG, 1); I(CALL, 2); I(JUMP, 183);
        I(QUOTE, 4); I(JFALSE, 217); I(GLOBAL, 2); I(PREP, 2);
        I(GLOBAL, 9); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 0); I(FVAR, 4); I(PREP, 1);
        I(ARG, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1);
        I(CALL, 2); I(JUMP, 219); I(QUOTE, 4); I(JFALSE, 288);
        I(QUOTE, 10); I(PREP, 1); I(QUOTE, 10); I(PREP, 1);
        I(GLOBAL, 11); I(PREP, 3); I(QUOTE, 12); I(FRAME, 3);
        I(FVAR, 0); I(PUTARG, 1); I(ARG, 1); I(PUTARG, 2);
        I(CLOSURE, 3); I(PUTARG, 0); I(LOCAL, 0); I(PUTARG, 1);
        I(NIL); I(PUTARG, 2); I(CALL, 3); I(PUTARG, 0);
        I(CALL, 1); I(QUOTE, 13); I(PREP, 1); I(GLOBAL, 9);
        I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1);
        I(PUTARG, 0); I(CALL, 1); I(NIL); I(CONS);
        I(CONS); I(PUTARG, 0); I(CALL, 1); I(RETURN);
        I(ARG, 0); I(TRAP, 425); I(QUOTE, 8); I(MPRIM, 2);
        I(BIND, 0); I(BIND, 1); I(GLOBAL, 9); I(PREP, 1);
        I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(BIND, 2);
        I(FVAR, 1); I(PREP, 2); I(QUOTE, 10); I(PREP, 1);
        I(FVAR, 0); I(PREP, 3); I(LOCAL, 1); I(PUTARG, 0);
        I(ARG, 1); I(PUTARG, 1); I(GLOBAL, 14); I(PUTARG, 2);
        I(CALL, 3); I(QUOTE, 15); I(PREP, 1); I(LOCAL, 2);
        I(PUTARG, 0); I(CALL, 1); I(QUOTE, 10); I(PREP, 1);
        I(GLOBAL, 11); I(PREP, 3); I(QUOTE, 16); I(FRAME, 3);
        I(FVAR, 0); I(PUTARG, 1); I(ARG, 1); I(PUTARG, 2);
        I(CLOSURE, 3); I(PUTARG, 0); I(FVAR, 5); I(PREP, 2);
        I(PUSH, 0); I(PUTARG, 0); I(LOCAL, 0); I(PUTARG, 1);
        I(CALL, 2); I(PUTARG, 1); I(NIL); I(PUTARG, 2);
        I(CALL, 3); I(PUTARG, 0); I(CALL, 1); I(QUOTE, 17);
        I(PREP, 1); I(LOCAL, 2); I(PUTARG, 0); I(CALL, 1);
        I(NIL); I(CONS); I(CONS); I(CONS);
        I(CONS); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
        I(ARG, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN);
        I(ARG, 0); I(TRAP, 635); I(QUOTE, 18); I(MPRIM, 3);
        I(BIND, 0); I(BIND, 1); I(BIND, 2); I(FVAR, 6);
        I(PREP, 0); I(CALL, 0); I(BIND, 3); I(FVAR, 6);
        I(PREP, 0); I(CALL, 0); I(BIND, 4); I(ARG, 2);
        I(JFALSE, 540); I(QUOTE, 10); I(PREP, 1); I(FVAR, 0);
        I(PREP, 3); I(LOCAL, 2); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(GLOBAL, 14); I(PUTARG, 2); I(CALL, 3);
        I(QUOTE, 19); I(PREP, 1); I(LOCAL, 3); I(PUTARG, 0);
        I(CALL, 1); I(FVAR, 0); I(PREP, 3); I(LOCAL, 1);
        I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1); I(GLOBAL, 20);
        I(PUTARG, 2); I(CALL, 3); I(LOCAL, 3); I(FVAR, 0);
        I(PREP, 3); I(LOCAL, 0); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(GLOBAL, 20); I(PUTARG, 2); I(CALL, 3);
        I(NIL); I(CONS); I(CONS); I(CONS);
        I(CONS); I(CONS); I(PUTARG, 0); I(CALL, 1);
        I(RETURN); I(QUOTE, 10); I(PREP, 1); I(FVAR, 0);
        I(PREP, 3); I(LOCAL, 2); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(GLOBAL, 14); I(PUTARG, 2); I(CALL, 3);
        I(QUOTE, 19); I(PREP, 1); I(LOCAL, 3); I(PUTARG, 0);
        I(CALL, 1); I(FVAR, 0); I(PREP, 3); I(LOCAL, 1);
        I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1); I(GLOBAL, 14);
        I(PUTARG, 2); I(CALL, 3); I(QUOTE, 21); I(PREP, 1);
        I(LOCAL, 4); I(PUTARG, 0); I(CALL, 1); I(LOCAL, 3);
        I(FVAR, 0); I(PREP, 3); I(LOCAL, 0); I(PUTARG, 0);
        I(ARG, 1); I(PUTARG, 1); I(GLOBAL, 14); I(PUTARG, 2);
        I(CALL, 3); I(LOCAL, 4); I(NIL); I(CONS);
        I(CONS); I(CONS); I(CONS); I(CONS);
        I(CONS); I(CONS); I(PUTARG, 0); I(CALL, 1);
        I(RETURN); I(ARG, 0); I(TRAP, 727); I(QUOTE, 22);
        I(MPRIM, 2); I(BIND, 0); I(TRAP, 727); I(QUOTE, 23);
        I(MPRIM, 2); I(TRAP, 726); I(QUOTE, 0); I(MPRIM, 1);
        I(BIND, 1); I(BIND, 2); I(FVAR, 7); I(PREP, 3);
        I(LOCAL, 2); I(PUTARG, 0); I(QUOTE, 6); I(PREP, 1);
        I(LOCAL, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1);
        I(ARG, 1); I(PUTARG, 2); I(CALL, 3); I(POP);
        I(FVAR, 0); I(PREP, 3); I(LOCAL, 0); I(PUTARG, 0);
        I(ARG, 1); I(PUTARG, 1); I(ARG, 2); I(PUTARG, 2);
        I(CALL, 3); I(BIND, 3); I(FVAR, 8); I(PREP, 2);
        I(LOCAL, 2); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(CALL, 2); I(POP); I(LOCAL, 3); I(RETURN);
        I(POP); I(ARG, 0); I(TRAP, 845); I(QUOTE, 22);
        I(MPRIM, 2); I(BIND, 0); I(TRAP, 845); I(QUOTE, 23);
        I(MPRIM, 2); I(BIND, 1); I(BIND, 2); I(FVAR, 0);
        I(PREP, 3); I(LOCAL, 1); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(GLOBAL, 14); I(PUTARG, 2); I(CALL, 3);
        I(BIND, 3); I(FVAR, 9); I(PREP, 2); I(LOCAL, 2);
        I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1); I(CALL, 2);
        I(BIND, 4); I(FVAR, 0); I(PREP, 3); I(LOCAL, 0);
        I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1); I(ARG, 2);
        I(PUTARG, 2); I(CALL, 3); I(BIND, 5); I(FVAR, 10);
        I(PREP, 2); I(LOCAL, 2); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(CALL, 2); I(POP); I(QUOTE, 10);
        I(PREP, 1); I(LOCAL, 3); I(QUOTE, 24); I(PREP, 1);
        I(LOCAL, 4); I(PUTARG, 0); I(CALL, 1); I(LOCAL, 5);
        I(NIL); I(CONS); I(CONS); I(CONS);
        I(PUTARG, 0); I(CALL, 1); I(RETURN); I(ARG, 0);
        I(TRAP, 969); I(QUOTE, 22); I(MPRIM, 2); I(BIND, 0);
        I(TRAP, 969); I(QUOTE, 25); I(MPRIM, 3); I(BIND, 1);
        I(BIND, 2); I(BIND, 3); I(FVAR, 11); I(PREP, 4);
        I(LOCAL, 3); I(PUTARG, 0); I(LOCAL, 2); I(PUTARG, 1);
        I(LOCAL, 1); I(PUTARG, 2); I(ARG, 1); I(PUTARG, 3);
        I(CALL, 4); I(BIND, 4); I(FVAR, 9); I(PREP, 2);
        I(LOCAL, 3); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(CALL, 2); I(BIND, 5); I(FVAR, 0); I(PREP, 3);
        I(LOCAL, 0); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(ARG, 2); I(PUTARG, 2); I(CALL, 3); I(BIND, 6);
        I(FVAR, 10); I(PREP, 2); I(LOCAL, 3); I(PUTARG, 0);
        I(ARG, 1); I(PUTARG, 1); I(CALL, 2); I(POP);
        I(QUOTE, 10); I(PREP, 1); I(LOCAL, 4); I(QUOTE, 24);
        I(PREP, 1); I(LOCAL, 5); I(PUTARG, 0); I(CALL, 1);
        I(LOCAL, 6); I(NIL); I(CONS); I(CONS);
        I(CONS); I(PUTARG, 0); I(CALL, 1); I(RETURN);
        I(ARG, 0); I(TRAP, 1016); I(QUOTE, 26); I(MPRIM, 2);
        I(BIND, 0); I(BIND, 1); I(FVAR, 1); I(PREP, 2);
        I(FVAR, 11); I(PREP, 4); I(QUOTE, 27); I(PUTARG, 0);
        I(LOCAL, 1); I(PUTARG, 1); I(LOCAL, 0); I(PUTARG, 2);
        I(ARG, 1); I(PUTARG, 3); I(CALL, 4); I(PUTARG, 0);
        I(ARG, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN);
        I(ARG, 0); I(TRAP, 1091); I(QUOTE, 28); I(MPRIM, 2);
        I(BIND, 0); I(BIND, 1); I(FVAR, 1); I(PREP, 2);
        I(QUOTE, 10); I(PREP, 1); I(FVAR, 0); I(PREP, 3);
        I(LOCAL, 1); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(GLOBAL, 14); I(PUTARG, 2); I(CALL, 3); I(FVAR, 0);
        I(PREP, 3); I(LOCAL, 0); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(GLOBAL, 14); I(PUTARG, 2); I(CALL, 3);
        I(QUOTE, 29); I(NIL); I(CONS); I(CONS);
        I(CONS); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 0);
        I(ARG, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN);
        I(ARG, 0); I(TRAP, 1113); I(QUOTE, 30); I(MEQ);
        I(FVAR, 1); I(PREP, 2); I(QUOTE, 31); I(PUTARG, 0);
        I(ARG, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN);
        I(ARG, 0); I(TRAP, 1150); I(QUOTE, 32); I(MPRIM, 1);
        I(BIND, 0); I(FVAR, 0); I(PREP, 3); I(FVAR, 12);
        I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1);
        I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1); I(ARG, 2);
        I(PUTARG, 2); I(CALL, 3); I(RETURN); I(ARG, 0);
        I(TRAP, 1213); I(QUOTE, 33); I(MPRIM, 2); I(BIND, 0);
        I(BIND, 1); I(QUOTE, 10); I(PREP, 1); I(FVAR, 0);
        I(PREP, 3); I(LOCAL, 1); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(GLOBAL, 14); I(PUTARG, 2); I(CALL, 3);
        I(QUOTE, 34); I(FVAR, 0); I(PREP, 3); I(LOCAL, 0);
        I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1); I(ARG, 2);
        I(PUTARG, 2); I(CALL, 3); I(NIL); I(CONS);
        I(CONS); I(CONS); I(PUTARG, 0); I(CALL, 1);
        I(RETURN); I(FAIL); }),
        N("const"), N("numeric"), N("="), N("int"),
        B(false), N("PUSH"), N("QUOTE"), N("var"),
        N("apply"), N("length"), N("SEQ"), N("_mapa"),
        F("<function>", 2, (() -> {
          I(FVAR, 1); I(PREP, 3); I(ARG, 0); I(PUTARG, 0);
          I(FVAR, 2); I(PUTARG, 1); I(GLOBAL, 0); I(PUTARG, 2);
          I(CALL, 3); I(ARG, 1); I(CONS); I(RETURN); }),
          N("false")),
        N("TCALL"), N("false"), N("PREP"), 
        F("<function>", 2, (() -> {
          I(ARG, 0); I(TRAP, 58); I(MCONS); I(BIND, 0);
          I(TRAP, 58); I(MCONS); I(BIND, 1); I(TRAP, 58);
          I(MNIL); I(QUOTE, 0); I(PREP, 1); I(FVAR, 1);
          I(PREP, 3); I(LOCAL, 1); I(PUTARG, 0); I(FVAR, 2);
          I(PUTARG, 1); I(GLOBAL, 1); I(PUTARG, 2); I(CALL, 3);
          I(QUOTE, 2); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
          I(CALL, 1); I(NIL); I(CONS); I(CONS);
          I(PUTARG, 0); I(CALL, 1); I(ARG, 1); I(CONS);
          I(RETURN); I(ARG, 1); I(RETURN); }),
          N("SEQ"), N("false"), N("PUTARG")),
        N("CALL"), N("if"), N("JFALSE"), N("true"),
        N("JUMP"), N("let"), N("val"), N("BIND"),
        N("fun"), N("function"), S("<function>"), N("cons"),
        N("CONS"), N("nil"), N("NIL"), N("list"),
        N("seq"), N("POP")),
      F("i_func", 4, (() -> {
        I(FVAR, 1); I(PREP, 1); I(FVAR, 2); I(PREP, 2);
        I(ARG, 2); I(PUTARG, 0); I(FVAR, 3); I(PREP, 3);
        I(ARG, 0); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(ARG, 3); I(PUTARG, 2); I(CALL, 3); I(PUTARG, 1);
        I(CALL, 2); I(PUTARG, 0); I(CALL, 1); I(BIND, 0);
        I(FVAR, 4); I(PREP, 2); I(PUSH, 1); I(PUTARG, 0);
        I(LOCAL, 0); I(PUTARG, 1); I(CALL, 2); I(POP);
        I(GLOBAL, 0); I(PREP, 1); I(FVAR, 5); I(PREP, 3);
        I(ARG, 0); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(LOCAL, 0); I(PUTARG, 2); I(CALL, 3); I(PUTARG, 0);
        I(CALL, 1); I(RETURN); }),
        N("_closure")),
      F("interp", 2, (() -> {
        I(ARG, 0); I(TRAP, 13); I(QUOTE, 0); I(MPRIM, 1);
        I(BIND, 0); I(LOCAL, 0); I(RETURN); I(ARG, 0);
        I(TRAP, 58); I(QUOTE, 1); I(MPRIM, 1); I(BIND, 0);
        I(QUOTE, 2); I(FRAME, 2); I(LOCAL, 0); I(PUTARG, 1);
        I(CLOSURE, 2); I(BIND, 1); I(LOCAL, 1); I(PREP, 1);
        I(FVAR, 1); I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0);
        I(ARG, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0);
        I(CALL, 1); I(RETURN); I(ARG, 0); I(TRAP, 124);
        I(QUOTE, 3); I(MPRIM, 2); I(BIND, 0); I(BIND, 1);
        I(GLOBAL, 4); I(PREP, 2); I(FVAR, 0); I(PREP, 2);
        I(LOCAL, 1); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(CALL, 2); I(PUTARG, 0); I(GLOBAL, 5); I(PREP, 3);
        I(QUOTE, 6); I(FRAME, 3); I(FVAR, 0); I(PUTARG, 1);
        I(ARG, 1); I(PUTARG, 2); I(CLOSURE, 3); I(PUTARG, 0);
        I(LOCAL, 0); I(PUTARG, 1); I(NIL); I(PUTARG, 2);
        I(CALL, 3); I(PUTARG, 1); I(CALL, 2); I(RETURN);
        I(ARG, 0); I(TRAP, 184); I(QUOTE, 7); I(MPRIM, 3);
        I(BIND, 0); I(BIND, 1); I(BIND, 2); I(FVAR, 0);
        I(PREP, 2); I(LOCAL, 2); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(CALL, 2); I(JFALSE, 169); I(FVAR, 0);
        I(PREP, 2); I(LOCAL, 1); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(CALL, 2); I(RETURN); I(FVAR, 0);
        I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(CALL, 2); I(RETURN); I(ARG, 0);
        I(TRAP, 281); I(QUOTE, 8); I(MPRIM, 2); I(BIND, 0);
        I(TRAP, 281); I(QUOTE, 9); I(MPRIM, 2); I(BIND, 1);
        I(BIND, 2); I(FVAR, 0); I(PREP, 2); I(LOCAL, 1);
        I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1); I(CALL, 2);
        I(BIND, 3); I(FVAR, 2); I(PREP, 3); I(LOCAL, 2);
        I(PUTARG, 0); I(QUOTE, 10); I(PREP, 1); I(LOCAL, 3);
        I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1); I(ARG, 1);
        I(PUTARG, 2); I(CALL, 3); I(POP); I(FVAR, 0);
        I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(CALL, 2); I(BIND, 4); I(FVAR, 3);
        I(PREP, 2); I(LOCAL, 2); I(PUTARG, 0); I(ARG, 1);
        I(PUTARG, 1); I(CALL, 2); I(POP); I(LOCAL, 4);
        I(RETURN); I(ARG, 0); I(TRAP, 388); I(QUOTE, 8);
        I(MPRIM, 2); I(BIND, 0); I(TRAP, 388); I(QUOTE, 11);
        I(MPRIM, 3); I(BIND, 1); I(BIND, 2); I(BIND, 3);
        I(FVAR, 4); I(PREP, 4); I(LOCAL, 3); I(PUTARG, 0);
        I(LOCAL, 2); I(PUTARG, 1); I(LOCAL, 1); I(PUTARG, 2);
        I(ARG, 1); I(PUTARG, 3); I(CALL, 4); I(BIND, 4);
        I(FVAR, 2); I(PREP, 3); I(LOCAL, 3); I(PUTARG, 0);
        I(QUOTE, 10); I(PREP, 1); I(LOCAL, 4); I(PUTARG, 0);
        I(CALL, 1); I(PUTARG, 1); I(ARG, 1); I(PUTARG, 2);
        I(CALL, 3); I(POP); I(FVAR, 0); I(PREP, 2);
        I(LOCAL, 0); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(CALL, 2); I(BIND, 5); I(FVAR, 3); I(PREP, 2);
        I(LOCAL, 3); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(CALL, 2); I(POP); I(LOCAL, 5); I(RETURN);
        I(ARG, 0); I(TRAP, 423); I(QUOTE, 12); I(MPRIM, 2);
        I(BIND, 0); I(BIND, 1); I(FVAR, 4); I(PREP, 4);
        I(QUOTE, 13); I(PUTARG, 0); I(LOCAL, 1); I(PUTARG, 1);
        I(LOCAL, 0); I(PUTARG, 2); I(ARG, 1); I(PUTARG, 3);
        I(CALL, 4); I(RETURN); I(ARG, 0); I(TRAP, 474);
        I(QUOTE, 14); I(MPRIM, 2); I(BIND, 0); I(BIND, 1);
        I(GLOBAL, 15); I(PREP, 2); I(FVAR, 0); I(PREP, 2);
        I(LOCAL, 1); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(CALL, 2); I(PUTARG, 0); I(FVAR, 0); I(PREP, 2);
        I(LOCAL, 0); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(CALL, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN);
        I(ARG, 0); I(TRAP, 483); I(QUOTE, 16); I(MEQ);
        I(NIL); I(RETURN); I(ARG, 0); I(TRAP, 523);
        I(QUOTE, 17); I(MPRIM, 1); I(BIND, 0); I(GLOBAL, 5);
        I(PREP, 3); I(QUOTE, 18); I(FRAME, 3); I(FVAR, 0);
        I(PUTARG, 1); I(ARG, 1); I(PUTARG, 2); I(CLOSURE, 3);
        I(PUTARG, 0); I(LOCAL, 0); I(PUTARG, 1); I(NIL);
        I(PUTARG, 2); I(CALL, 3); I(RETURN); I(ARG, 0);
        I(TRAP, 565); I(QUOTE, 19); I(MPRIM, 2); I(BIND, 0);
        I(BIND, 1); I(FVAR, 0); I(PREP, 2); I(LOCAL, 1);
        I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1); I(CALL, 2);
        I(POP); I(FVAR, 0); I(PREP, 2); I(LOCAL, 0);
        I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1); I(CALL, 2);
        I(RETURN); I(FAIL); }),
        N("const"), N("var"), 
        F("case", 1, (() -> {
          I(ARG, 0); I(TRAP, 17); I(MPAIR); I(POP);
          I(TRAP, 17); I(QUOTE, 0); I(MPRIM, 1); I(BIND, 0);
          I(LOCAL, 0); I(RETURN); I(GLOBAL, 1); I(PREP, 1);
          I(FVAR, 1); I(PUTARG, 0); I(CALL, 1); I(RETURN); }),
          N("QUOTE"), N("_glodef")),
        N("apply"), N("_apply"), N("_mapa"), 
        F("<function>", 2, (() -> {
          I(FVAR, 1); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
          I(FVAR, 2); I(PUTARG, 1); I(CALL, 2); I(ARG, 1);
          I(CONS); I(RETURN); })),
        N("if"), N("let"), N("val"), N("QUOTE"),
        N("fun"), N("function"), S("<function>"), N("cons"),
        N(":"), N("nil"), N("list"), 
        F("<function>", 2, (() -> {
          I(FVAR, 1); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
          I(FVAR, 2); I(PUTARG, 1); I(CALL, 2); I(ARG, 1);
          I(CONS); I(RETURN); })),
        N("seq")),
      F("exec", 1, (() -> {
        I(ARG, 0); I(TRAP, 54); I(QUOTE, 0); I(MPRIM, 2);
        I(BIND, 0); I(BIND, 1); I(GLOBAL, 1); I(PREP, 1);
        I(LOCAL, 1); I(PUTARG, 0); I(CALL, 1); I(POP);
        I(GLOBAL, 2); I(PREP, 2); I(LOCAL, 1); I(PUTARG, 0);
        I(FVAR, 1); I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0);
        I(FVAR, 2); I(PREP, 0); I(CALL, 0); I(PUTARG, 1);
        I(CALL, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN);
        I(ARG, 0); I(TRAP, 118); I(QUOTE, 3); I(MPRIM, 3);
        I(BIND, 0); I(BIND, 1); I(BIND, 2); I(GLOBAL, 1);
        I(PREP, 1); I(LOCAL, 2); I(PUTARG, 0); I(CALL, 1);
        I(POP); I(GLOBAL, 2); I(PREP, 2); I(LOCAL, 2);
        I(PUTARG, 0); I(FVAR, 3); I(PREP, 4); I(LOCAL, 2);
        I(PUTARG, 0); I(LOCAL, 1); I(PUTARG, 1); I(LOCAL, 0);
        I(PUTARG, 2); I(FVAR, 2); I(PREP, 0); I(CALL, 0);
        I(PUTARG, 3); I(CALL, 4); I(PUTARG, 1); I(CALL, 2);
        I(RETURN); I(GLOBAL, 4); I(PREP, 1); I(FVAR, 1);
        I(PREP, 2); I(ARG, 0); I(PUTARG, 0); I(FVAR, 2);
        I(PREP, 0); I(CALL, 0); I(PUTARG, 1); I(CALL, 2);
        I(PUTARG, 0); I(CALL, 1); I(RETURN); }),
        N("val"), N("_redefine"), N("_topdef"), N("fun"),
        N("_topval")),
      N("_defined"), N("_syntax"), N("_topdef"), N("_hash"),
      F("<function>", 0, (() -> {
        I(FVAR, 1); I(PREP, 0); I(CALL, 0); I(BIND, 0);
        I(GLOBAL, 0); I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0);
        I(QUOTE, 1); I(PUTARG, 1); I(CALL, 2); I(JFALSE, 27);
        I(GLOBAL, 2); I(RETURN); I(GLOBAL, 3); I(PREP, 0);
        I(CALL, 0); I(POP); I(FVAR, 2); I(PREP, 2);
        I(PUSH, 0); I(PUTARG, 0); I(LOCAL, 0); I(PUTARG, 1);
        I(CALL, 2); I(POP); I(GLOBAL, 4); I(PREP, 2);
        I(FVAR, 3); I(PUTARG, 0); I(PUSH, 0); I(PUTARG, 1);
        I(CALL, 2); I(POP); I(GLOBAL, 5); I(PREP, 1);
        I(FVAR, 4); I(PUTARG, 0); I(CALL, 1); I(POP);
        I(FVAR, 5); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
        I(CALL, 1); I(POP); I(GLOBAL, 6); I(RETURN); }),
        N("="), N("eof"), N("false"), N("_toptext"),
        N("_set"), N("_setroot"), N("true")))));
    D("_assoc", C(F("_assoc", 2, (() -> {
      I(ARG, 1); I(TRAP, 7); I(MNIL); I(NIL);
      I(RETURN); I(ARG, 1); I(TRAP, 66); I(MCONS);
      I(BIND, 0); I(BIND, 1); I(GLOBAL, 0); I(PREP, 2);
      I(ARG, 0); I(PUTARG, 0); I(GLOBAL, 1); I(PREP, 1);
      I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1);
      I(CALL, 2); I(JFALSE, 51); I(GLOBAL, 2); I(PREP, 1);
      I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(RETURN);
      I(FVAR, 0); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
      I(LOCAL, 1); I(PUTARG, 1); I(CALL, 2); I(RETURN);
      I(FAIL); }),
      N("="), N("_fst"), N("_snd"))));
    D("_infixl", C(F("_infixl", 2, (() -> {
      I(GLOBAL, 0); I(PREP, 3); I(GLOBAL, 1); I(PUTARG, 0);
      I(ARG, 0); I(PUTARG, 1); I(GLOBAL, 2); I(PREP, 2);
      I(QUOTE, 3); I(PUTARG, 0); I(GLOBAL, 2); I(PREP, 2);
      I(ARG, 1); I(PUTARG, 0); I(GLOBAL, 4); I(PREP, 2);
      I(ARG, 1); I(PUTARG, 0); I(PUSH, 1); I(PUTARG, 1);
      I(CALL, 2); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1);
      I(CALL, 2); I(PUTARG, 2); I(CALL, 3); I(RETURN); }),
      N("_update"), N("_syntax"), N("_pair"), N("binop"),
      N("+"))));
    D("_infixr", C(F("_infixr", 2, (() -> {
      I(GLOBAL, 0); I(PREP, 3); I(GLOBAL, 1); I(PUTARG, 0);
      I(ARG, 0); I(PUTARG, 1); I(GLOBAL, 2); I(PREP, 2);
      I(QUOTE, 3); I(PUTARG, 0); I(GLOBAL, 2); I(PREP, 2);
      I(ARG, 1); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
      I(CALL, 2); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 2);
      I(CALL, 3); I(RETURN); }),
      N("_update"), N("_syntax"), N("_pair"), N("binop"))));
    D("_iter", C(F("_iter", 2, (() -> {
      I(ARG, 1); I(TRAP, 7); I(MNIL); I(NIL);
      I(RETURN); I(ARG, 1); I(TRAP, 42); I(MCONS);
      I(BIND, 0); I(BIND, 1); I(ARG, 0); I(PREP, 1);
      I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(POP);
      I(FVAR, 0); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
      I(LOCAL, 1); I(PUTARG, 1); I(CALL, 2); I(RETURN);
      I(FAIL); }))));
    D("_lsect", C(F("_lsect", 2, (() -> {
      I(QUOTE, 0); I(FRAME, 3); I(ARG, 0); I(PUTARG, 1);
      I(ARG, 1); I(PUTARG, 2); I(CLOSURE, 3); I(RETURN); }),
      F("<function>", 1, (() -> {
        I(FVAR, 1); I(PREP, 2); I(FVAR, 2); I(PUTARG, 0);
        I(ARG, 0); I(PUTARG, 1); I(CALL, 2); I(RETURN); })))));
    D("_mapa", C(F("_mapa", 3, (() -> {
      I(ARG, 1); I(TRAP, 8); I(MNIL); I(ARG, 2);
      I(RETURN); I(ARG, 1); I(TRAP, 48); I(MCONS);
      I(BIND, 0); I(BIND, 1); I(ARG, 0); I(PREP, 2);
      I(LOCAL, 0); I(PUTARG, 0); I(FVAR, 0); I(PREP, 3);
      I(ARG, 0); I(PUTARG, 0); I(LOCAL, 1); I(PUTARG, 1);
      I(ARG, 2); I(PUTARG, 2); I(CALL, 3); I(PUTARG, 1);
      I(CALL, 2); I(RETURN); I(FAIL); }))));
    D("_range", C(F("_range", 2, (() -> {
      I(GLOBAL, 0); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
      I(ARG, 1); I(PUTARG, 1); I(CALL, 2); I(JFALSE, 18);
      I(NIL); I(RETURN); I(GLOBAL, 1); I(PREP, 2);
      I(ARG, 0); I(PUTARG, 0); I(FVAR, 0); I(PREP, 2);
      I(GLOBAL, 2); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
      I(PUSH, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 0);
      I(ARG, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1);
      I(CALL, 2); I(RETURN); }),
      N(">"), N(":"), N("+"))));
    D("_rsect", C(F("_rsect", 2, (() -> {
      I(QUOTE, 0); I(FRAME, 3); I(ARG, 0); I(PUTARG, 1);
      I(ARG, 1); I(PUTARG, 2); I(CLOSURE, 3); I(RETURN); }),
      F("<function>", 1, (() -> {
        I(FVAR, 1); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
        I(FVAR, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN); })))));
    D("_top", C(F("_top", 0, (() -> {
      I(GLOBAL, 0); I(PREP, 0); I(CALL, 0); I(BIND, 0);
      I(LOCAL, 0); I(PREP, 0); I(CALL, 0); I(RETURN); }),
      N("__top"))));
    D("concat", C(F("concat", 1, (() -> {
      I(ARG, 0); I(TRAP, 7); I(MNIL); I(NIL);
      I(RETURN); I(ARG, 0); I(TRAP, 39); I(MCONS);
      I(BIND, 0); I(BIND, 1); I(GLOBAL, 0); I(PREP, 2);
      I(LOCAL, 0); I(PUTARG, 0); I(FVAR, 0); I(PREP, 1);
      I(LOCAL, 1); I(PUTARG, 0); I(CALL, 1); I(PUTARG, 1);
      I(CALL, 2); I(RETURN); I(FAIL); }),
      N("++"))));
    D("false", B(false));
    D("filter", C(F("filter", 2, (() -> {
      I(ARG, 1); I(TRAP, 7); I(MNIL); I(NIL);
      I(RETURN); I(ARG, 1); I(TRAP, 70); I(MCONS);
      I(BIND, 0); I(BIND, 1); I(ARG, 0); I(PREP, 1);
      I(LOCAL, 0); I(PUTARG, 0); I(CALL, 1); I(JFALSE, 55);
      I(GLOBAL, 0); I(PREP, 2); I(LOCAL, 0); I(PUTARG, 0);
      I(FVAR, 0); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
      I(LOCAL, 1); I(PUTARG, 1); I(CALL, 2); I(PUTARG, 1);
      I(CALL, 2); I(RETURN); I(FVAR, 0); I(PREP, 2);
      I(ARG, 0); I(PUTARG, 0); I(LOCAL, 1); I(PUTARG, 1);
      I(CALL, 2); I(RETURN); I(FAIL); }),
      N(":"))));
    D("foldl", C(F("foldl", 3, (() -> {
      I(ARG, 2); I(TRAP, 8); I(MNIL); I(ARG, 1);
      I(RETURN); I(ARG, 2); I(TRAP, 48); I(MCONS);
      I(BIND, 0); I(BIND, 1); I(FVAR, 0); I(PREP, 3);
      I(ARG, 0); I(PUTARG, 0); I(ARG, 0); I(PREP, 2);
      I(ARG, 1); I(PUTARG, 0); I(LOCAL, 0); I(PUTARG, 1);
      I(CALL, 2); I(PUTARG, 1); I(LOCAL, 1); I(PUTARG, 2);
      I(CALL, 3); I(RETURN); I(FAIL); }))));
    D("foldr", C(F("foldr", 3, (() -> {
      I(ARG, 2); I(TRAP, 8); I(MNIL); I(ARG, 1);
      I(RETURN); I(ARG, 2); I(TRAP, 48); I(MCONS);
      I(BIND, 0); I(BIND, 1); I(ARG, 0); I(PREP, 2);
      I(LOCAL, 0); I(PUTARG, 0); I(FVAR, 0); I(PREP, 3);
      I(ARG, 0); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
      I(LOCAL, 1); I(PUTARG, 2); I(CALL, 3); I(PUTARG, 1);
      I(CALL, 2); I(RETURN); I(FAIL); }))));
    D("length", C(F("length", 1, (() -> {
      I(ARG, 0); I(TRAP, 8); I(MNIL); I(PUSH, 0);
      I(RETURN); I(ARG, 0); I(TRAP, 40); I(MCONS);
      I(BIND, 0); I(BIND, 1); I(GLOBAL, 0); I(PREP, 2);
      I(FVAR, 0); I(PREP, 1); I(LOCAL, 1); I(PUTARG, 0);
      I(CALL, 1); I(PUTARG, 0); I(PUSH, 1); I(PUTARG, 1);
      I(CALL, 2); I(RETURN); I(FAIL); }),
      N("+"))));
    D("map", C(F("map", 2, (() -> {
      I(ARG, 1); I(TRAP, 7); I(MNIL); I(NIL);
      I(RETURN); I(ARG, 1); I(TRAP, 51); I(MCONS);
      I(BIND, 0); I(BIND, 1); I(GLOBAL, 0); I(PREP, 2);
      I(ARG, 0); I(PREP, 1); I(LOCAL, 0); I(PUTARG, 0);
      I(CALL, 1); I(PUTARG, 0); I(FVAR, 0); I(PREP, 2);
      I(ARG, 0); I(PUTARG, 0); I(LOCAL, 1); I(PUTARG, 1);
      I(CALL, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN);
      I(FAIL); }),
      N(":"))));
    D("not", C(F("not", 1, (() -> {
      I(ARG, 0); I(JFALSE, 7); I(GLOBAL, 0); I(RETURN);
      I(GLOBAL, 1); I(RETURN); }),
      N("false"), N("true"))));
    D("reverse", C(F("reverse", 1, (() -> {
      I(QUOTE, 0); I(FRAME, 1); I(CLOSURE, 1); I(BIND, 0);
      I(LOCAL, 0); I(PREP, 2); I(ARG, 0); I(PUTARG, 0);
      I(NIL); I(PUTARG, 1); I(CALL, 2); I(RETURN); }),
      F("reva", 2, (() -> {
        I(ARG, 0); I(TRAP, 8); I(MNIL); I(ARG, 1);
        I(RETURN); I(ARG, 0); I(TRAP, 44); I(MCONS);
        I(BIND, 0); I(BIND, 1); I(FVAR, 0); I(PREP, 2);
        I(LOCAL, 1); I(PUTARG, 0); I(GLOBAL, 0); I(PREP, 2);
        I(LOCAL, 0); I(PUTARG, 0); I(ARG, 1); I(PUTARG, 1);
        I(CALL, 2); I(PUTARG, 1); I(CALL, 2); I(RETURN);
        I(FAIL); }),
        N(":")))));
    D("true", B(true));
  }
}
