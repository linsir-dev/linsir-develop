package com.linsir.spring.framework.spring_core.bytecode.asm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ASM Opcodes测试类
 *
 * <p>测试ASM操作码常量的正确性，包括：
 * <ul>
 *   <li>ASM版本常量</li>
 *   <li>访问标志常量</li>
 *   <li>基本类型常量</li>
 *   <li>指令操作码</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 */
public class OpcodesTest {

    /**
     * 测试ASM版本常量
     */
    @Test
    public void testAsmVersions() {
        // ASM版本格式: (major << 16) | (minor << 8)
        assertEquals((4 << 16) | (0 << 8), Opcodes.ASM4);
        assertEquals((5 << 16) | (0 << 8), Opcodes.ASM5);
        assertEquals((6 << 16) | (0 << 8), Opcodes.ASM6);
        assertEquals((7 << 16) | (0 << 8), Opcodes.ASM7);
        assertEquals((8 << 16) | (0 << 8), Opcodes.ASM8);
        assertEquals((9 << 16) | (0 << 8), Opcodes.ASM9);
    }

    /**
     * 测试访问标志常量
     */
    @Test
    public void testAccessFlags() {
        // 访问修饰符
        assertEquals(0x0001, Opcodes.ACC_PUBLIC);
        assertEquals(0x0002, Opcodes.ACC_PRIVATE);
        assertEquals(0x0004, Opcodes.ACC_PROTECTED);

        // 其他修饰符
        assertEquals(0x0008, Opcodes.ACC_STATIC);
        assertEquals(0x0010, Opcodes.ACC_FINAL);
        assertEquals(0x0020, Opcodes.ACC_SUPER);
        assertEquals(0x0040, Opcodes.ACC_VOLATILE);
        assertEquals(0x0080, Opcodes.ACC_TRANSIENT);
        assertEquals(0x0100, Opcodes.ACC_NATIVE);
        assertEquals(0x0200, Opcodes.ACC_INTERFACE);
        assertEquals(0x0400, Opcodes.ACC_ABSTRACT);
        assertEquals(0x0800, Opcodes.ACC_STRICT);
        assertEquals(0x1000, Opcodes.ACC_SYNTHETIC);
        assertEquals(0x2000, Opcodes.ACC_ANNOTATION);
        assertEquals(0x4000, Opcodes.ACC_ENUM);
    }

    /**
     * 测试基本类型常量
     */
    @Test
    public void testPrimitiveTypes() {
        assertEquals(4, Opcodes.T_BOOLEAN);
        assertEquals(5, Opcodes.T_CHAR);
        assertEquals(6, Opcodes.T_FLOAT);
        assertEquals(7, Opcodes.T_DOUBLE);
        assertEquals(8, Opcodes.T_BYTE);
        assertEquals(9, Opcodes.T_SHORT);
        assertEquals(10, Opcodes.T_INT);
        assertEquals(11, Opcodes.T_LONG);
    }

    /**
     * 测试常量加载指令
     */
    @Test
    public void testConstInstructions() {
        assertEquals(0, Opcodes.NOP);
        assertEquals(1, Opcodes.ACONST_NULL);

        // int常量
        assertEquals(2, Opcodes.ICONST_M1);
        assertEquals(3, Opcodes.ICONST_0);
        assertEquals(4, Opcodes.ICONST_1);
        assertEquals(5, Opcodes.ICONST_2);
        assertEquals(6, Opcodes.ICONST_3);
        assertEquals(7, Opcodes.ICONST_4);
        assertEquals(8, Opcodes.ICONST_5);

        // long常量
        assertEquals(9, Opcodes.LCONST_0);
        assertEquals(10, Opcodes.LCONST_1);

        // float常量
        assertEquals(11, Opcodes.FCONST_0);
        assertEquals(12, Opcodes.FCONST_1);
        assertEquals(13, Opcodes.FCONST_2);

        // double常量
        assertEquals(14, Opcodes.DCONST_0);
        assertEquals(15, Opcodes.DCONST_1);
    }

    /**
     * 测试加载和存储指令
     */
    @Test
    public void testLoadStoreInstructions() {
        // 加载指令
        assertEquals(21, Opcodes.ILOAD);
        assertEquals(22, Opcodes.LLOAD);
        assertEquals(23, Opcodes.FLOAD);
        assertEquals(24, Opcodes.DLOAD);
        assertEquals(25, Opcodes.ALOAD);

        // 存储指令
        assertEquals(54, Opcodes.ISTORE);
        assertEquals(55, Opcodes.LSTORE);
        assertEquals(56, Opcodes.FSTORE);
        assertEquals(57, Opcodes.DSTORE);
        assertEquals(58, Opcodes.ASTORE);
    }

    /**
     * 测试算术指令
     */
    @Test
    public void testArithmeticInstructions() {
        // 加法
        assertEquals(96, Opcodes.IADD);
        assertEquals(97, Opcodes.LADD);
        assertEquals(98, Opcodes.FADD);
        assertEquals(99, Opcodes.DADD);

        // 减法
        assertEquals(100, Opcodes.ISUB);
        assertEquals(101, Opcodes.LSUB);
        assertEquals(102, Opcodes.FSUB);
        assertEquals(103, Opcodes.DSUB);

        // 乘法
        assertEquals(104, Opcodes.IMUL);
        assertEquals(105, Opcodes.LMUL);
        assertEquals(106, Opcodes.FMUL);
        assertEquals(107, Opcodes.DMUL);

        // 除法
        assertEquals(108, Opcodes.IDIV);
        assertEquals(109, Opcodes.LDIV);
        assertEquals(110, Opcodes.FDIV);
        assertEquals(111, Opcodes.DDIV);
    }

    /**
     * 测试方法调用指令
     */
    @Test
    public void testMethodInvocationInstructions() {
        assertEquals(182, Opcodes.INVOKEVIRTUAL);
        assertEquals(183, Opcodes.INVOKESPECIAL);
        assertEquals(184, Opcodes.INVOKESTATIC);
        assertEquals(185, Opcodes.INVOKEINTERFACE);
        assertEquals(186, Opcodes.INVOKEDYNAMIC);
    }

    /**
     * 测试字段访问指令
     */
    @Test
    public void testFieldAccessInstructions() {
        assertEquals(178, Opcodes.GETSTATIC);
        assertEquals(179, Opcodes.PUTSTATIC);
        assertEquals(180, Opcodes.GETFIELD);
        assertEquals(181, Opcodes.PUTFIELD);
    }

    /**
     * 测试返回指令
     */
    @Test
    public void testReturnInstructions() {
        assertEquals(172, Opcodes.IRETURN);
        assertEquals(173, Opcodes.LRETURN);
        assertEquals(174, Opcodes.FRETURN);
        assertEquals(175, Opcodes.DRETURN);
        assertEquals(176, Opcodes.ARETURN);
        assertEquals(177, Opcodes.RETURN);
    }

    /**
     * 测试跳转指令
     */
    @Test
    public void testJumpInstructions() {
        assertEquals(153, Opcodes.IFEQ);
        assertEquals(154, Opcodes.IFNE);
        assertEquals(155, Opcodes.IFLT);
        assertEquals(156, Opcodes.IFGE);
        assertEquals(157, Opcodes.IFGT);
        assertEquals(158, Opcodes.IFLE);
        assertEquals(167, Opcodes.GOTO);
    }

    /**
     * 测试对象操作指令
     */
    @Test
    public void testObjectInstructions() {
        assertEquals(187, Opcodes.NEW);
        assertEquals(188, Opcodes.NEWARRAY);
        assertEquals(189, Opcodes.ANEWARRAY);
        assertEquals(190, Opcodes.ARRAYLENGTH);
        assertEquals(191, Opcodes.ATHROW);
        assertEquals(192, Opcodes.CHECKCAST);
        assertEquals(193, Opcodes.INSTANCEOF);
    }

    /**
     * 测试栈操作指令
     */
    @Test
    public void testStackInstructions() {
        assertEquals(87, Opcodes.POP);
        assertEquals(88, Opcodes.POP2);
        assertEquals(89, Opcodes.DUP);
        assertEquals(90, Opcodes.DUP_X1);
        assertEquals(91, Opcodes.DUP_X2);
        assertEquals(92, Opcodes.DUP2);
        assertEquals(95, Opcodes.SWAP);
    }

    /**
     * 测试方法句柄类型
     */
    @Test
    public void testMethodHandleTypes() {
        assertEquals(1, Opcodes.H_GETFIELD);
        assertEquals(2, Opcodes.H_GETSTATIC);
        assertEquals(3, Opcodes.H_PUTFIELD);
        assertEquals(4, Opcodes.H_PUTSTATIC);
        assertEquals(5, Opcodes.H_INVOKEVIRTUAL);
        assertEquals(6, Opcodes.H_INVOKESTATIC);
        assertEquals(7, Opcodes.H_INVOKESPECIAL);
        assertEquals(8, Opcodes.H_NEWINVOKESPECIAL);
        assertEquals(9, Opcodes.H_INVOKEINTERFACE);
    }

    /**
     * 测试栈映射帧类型
     */
    @Test
    public void testFrameTypes() {
        assertEquals(-1, Opcodes.F_NEW);
        assertEquals(0, Opcodes.F_FULL);
        assertEquals(1, Opcodes.F_APPEND);
        assertEquals(2, Opcodes.F_CHOP);
        assertEquals(3, Opcodes.F_SAME);
        assertEquals(4, Opcodes.F_SAME1);
    }
}
