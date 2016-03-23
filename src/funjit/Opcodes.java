/*
 * Opcodes.java
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

package funjit;

/** Opcodes and other constants for the JVM */
class Opcodes {
    // versions
    public static final int V1_5 = 49, V1_6 = 50;

    // access flags
    public static final int 
    	ACC_PUBLIC = 0x0001,  // class, field, method
        ACC_PRIVATE = 0x0002,  // class, field, method
        ACC_PROTECTED = 0x0004,  // class, field, method
        ACC_STATIC = 0x0008,  // field, method
        ACC_FINAL = 0x0010,  // class, field, method
        ACC_SUPER = 0x0020,  // class
        ACC_SYNCHRONIZED = 0x0020,  // method
        ACC_VOLATILE = 0x0040,  // field
        ACC_BRIDGE = 0x0040,  // method
        ACC_VARARGS = 0x0080,  // method
        ACC_TRANSIENT = 0x0080,  // field
        ACC_NATIVE = 0x0100,  // method
        ACC_INTERFACE = 0x0200,  // class
        ACC_ABSTRACT = 0x0400,  // class, method
        ACC_STRICT = 0x0800,  // method
        ACC_ENUM = 0x4000; // class(?) field inner

    // types for NEWARRAY
    public static final int 
    	T_BOOLEAN = 4, T_CHAR = 5, T_FLOAT = 6, T_DOUBLE = 7, T_BYTE = 8, 
    	T_SHORT = 9, T_INT = 10, T_LONG = 11;
    
    public enum Op {
        AALOAD(50, -1), AASTORE(83, -3), ACONST_NULL(1, 1),
        ALOAD_0(42, 1), ALOAD(25, 1), ANEWARRAY(189, 0), ARETURN(176, -1),
        ARRAYLENGTH(190, 0), ASTORE_0(75, -1), ASTORE(58, -1),
        ATHROW(191, 0), BALOAD(51, -1), BASTORE(84, -3), BIPUSH(16, 1),
        CALOAD(52, -1), CASTORE(85, -3), CHECKCAST(192, 0), D2F(144, -1),
        D2I(142, -1), D2L(143, 0), DADD(99, -2), DALOAD(49, 0),
        DASTORE(82, -4), DCMPG(152, -3), DCMPL(151, -3), DCONST_0(14, 2),
        DCONST_1(15, 2), DDIV(111, -2), DLOAD_0(38, 2), DLOAD(24, 2),
        DMUL(107, -2), DNEG(119, 0), DREM(115, -2), DRETURN(175, -2),
        DSTORE_0(71, -2), DSTORE(57, -2), DSUB(103, -2), DUP2(92, 2),
        DUP2_X1(93, 2), DUP2_X2(94, 2), DUP(89, 1), DUP_X1(90, 1),
        DUP_X2(91, 1), F2D(141, 1), F2I(139, 0), F2L(140, 1), FADD(98, -1),
        FALOAD(48, -1), FASTORE(81, -3), FCMPG(150, -1), FCMPL(149, -1),
        FCONST_0(11, 1), FDIV(110, -1), FLOAD_0(34, 1), FLOAD(23, 1),
        FMUL(106, -1), FNEG(118, 0), FREM(114, -1), FRETURN(174, -1),
        FSTORE_0(67, -1), FSTORE(56, -1), FSUB(102, -1), GETFIELD(180, 0),
        GETSTATIC(178, 0), GOTO(167, 0), I2B(145, 0), I2C(146, 0),
        I2D(135, 1), I2F(134, 0), I2L(133, 1), I2S(147, 0), IADD(96, -1),
        IALOAD(46, -1), IAND(126, -1), IASTORE(79, -3), ICONST_0(3, 1),
        IDIV(108, -1), IF_ACMPEQ(165, -2), IF_ACMPNE(166, -2),
        IFEQ(153, -1), IFGE(156, -1), IFGT(157, -1), IF_ICMPEQ(159, -2),
        IF_ICMPGE(162, -2), IF_ICMPGT(163, -2), IF_ICMPLE(164, -2),
        IF_ICMPLT(161, -2), IF_ICMPNE(160, -2), IFLE(158, -1),
        IFLT(155, -1), IFNE(154, -1), IFNONNULL(199, -1), IFNULL(198, -1),
        IINC(132, 0), ILOAD_0(26, 1), ILOAD(21, 1), IMUL(104, -1),
        INEG(116, 0), INSTANCEOF(193, 0), INVOKEDYNAMIC(186, 0),
        INVOKEINTERFACE(185, 0), INVOKESPECIAL(183, 0),
        INVOKESTATIC(184, 0), INVOKEVIRTUAL(182, 0), IOR(128, -1),
        IREM(112, -1), IRETURN(172, -1), ISHL(120, -1), ISHR(122, -1),
        ISTORE_0(59, -1), ISTORE(54, -1), ISUB(100, -1), IUSHR(124, -1),
        IXOR(130, -1), L2D(138, 0), L2F(137, -1), L2I(136, -1),
        LADD(97, -2), LALOAD(47, 0), LAND(127, -2), LASTORE(80, -4),
        LCMP(148, -3), LCONST_0(9, 2), LDC(18, 1), LDC2_W(20, 2),
        LDC_W(19, 1), LDIV(109, -2), LLOAD_0(30, 2), LLOAD(22, 2),
        LMUL(105, -2), LNEG(117, 0), LOOKUPSWITCH(171, -1), LOR(129, -2),
        LREM(113, -2), LRETURN(173, -2), LSHL(121, -1), LSHR(123, -1),
        LSTORE_0(63, -2), LSTORE(55, -2), LSUB(101, -2), LUSHR(125, -1),
        LXOR(131, -2), MONITORENTER(194, -1), MONITOREXIT(195, -1),
        MULTIANEWARRAY(197, 0), NEW(187, 1), NEWARRAY(188, 0), NOP(0, 0),
        POP2(88, -2), POP(87, -1), PUTFIELD(181, 0), PUTSTATIC(179, 0),
        RETURN(177, 0), SALOAD(53, -1), SASTORE(86, -3), SIPUSH(17, 1),
        SWAP(95, 0), TABLESWITCH(170, -1), WIDE(196, 0),

	// Fictitious instructions for assembler
	CLASS(998, 1), CONST(999, 1);

	public final int byteval;
	public final int delta;

	private Op(int byteval, int delta) {
	    this.byteval = byteval; this.delta = delta;
	}
    }
}
