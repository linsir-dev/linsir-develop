package com.linsir.spring.framework.spring_core.bytecode.asm;

/**
 * ASM方法访问器
 *
 * <p>用于访问和生成方法的访问器，是ASM框架的核心类之一。
 * 通过此类可以生成方法的字节码指令。
 *
 * <p>访问顺序：
 * <ol>
 *   <li>visitCode - 开始访问方法代码</li>
 *   <li>visitXxxInsn - 访问各种指令（可多次）</li>
 *   <li>visitMaxs - 设置最大栈深度和局部变量表大小</li>
 *   <li>visitEnd - 访问结束</li>
 * </ol>
 *
 * <p>使用示例：
 * <pre>{@code
 * MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "add", "(II)I", null, null);
 * mv.visitCode();
 * mv.visitVarInsn(ILOAD, 1);  // 加载第一个参数
 * mv.visitVarInsn(ILOAD, 2);  // 加载第二个参数
 * mv.visitInsn(IADD);         // 相加
 * mv.visitInsn(IRETURN);      // 返回
 * mv.visitMaxs(2, 3);         // 最大栈深度2，局部变量3
 * mv.visitEnd();
 * }</pre>
 *
 * @author linsir
 * @since 1.0
 * @see ClassVisitor
 * @see ClassWriter
 * @see Opcodes
 */
public abstract class MethodVisitor {

    /**
     * ASM API版本
     */
    protected final int api;

    /**
     * 下一个访问器（链式调用）
     */
    protected MethodVisitor mv;

    /**
     * 构造函数
     *
     * @param api ASM API版本
     */
    public MethodVisitor(int api) {
        this(api, null);
    }

    /**
     * 构造函数
     *
     * @param api ASM API版本
     * @param mv 下一个访问器
     */
    public MethodVisitor(int api, MethodVisitor mv) {
        this.api = api;
        this.mv = mv;
    }

    /**
     * 访问方法代码开始
     *
     * <p>在visitParameter和visitAnnotation之后调用。
     */
    public void visitCode() {
        if (mv != null) {
            mv.visitCode();
        }
    }

    /**
     * 访问零操作数指令
     *
     * @param opcode 操作码
     */
    public void visitInsn(int opcode) {
        if (mv != null) {
            mv.visitInsn(opcode);
        }
    }

    /**
     * 访问单字节操作数指令
     *
     * @param opcode 操作码
     * @param operand 操作数
     */
    public void visitIntInsn(int opcode, int operand) {
        if (mv != null) {
            mv.visitIntInsn(opcode, operand);
        }
    }

    /**
     * 访问局部变量加载指令
     *
     * @param opcode 操作码（如ILOAD, ALOAD等）
     * @param var 局部变量索引
     */
    public void visitVarInsn(int opcode, int var) {
        if (mv != null) {
            mv.visitVarInsn(opcode, var);
        }
    }

    /**
     * 访问类型指令
     *
     * @param opcode 操作码（如NEW, ANEWARRAY, CHECKCAST, INSTANCEOF）
     * @param type 类型描述符
     */
    public void visitTypeInsn(int opcode, String type) {
        if (mv != null) {
            mv.visitTypeInsn(opcode, type);
        }
    }

    /**
     * 访问字段访问指令
     *
     * @param opcode 操作码（如GETSTATIC, PUTSTATIC, GETFIELD, PUTFIELD）
     * @param owner 字段所在类的内部名
     * @param name 字段名
     * @param descriptor 字段描述符
     */
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if (mv != null) {
            mv.visitFieldInsn(opcode, owner, name, descriptor);
        }
    }

    /**
     * 访问方法调用指令
     *
     * @param opcode 操作码（如INVOKEVIRTUAL, INVOKESPECIAL等）
     * @param owner 方法所在类的内部名
     * @param name 方法名
     * @param descriptor 方法描述符
     * @param isInterface 是否是接口方法
     */
    public void visitMethodInsn(int opcode, String owner, String name,
                                String descriptor, boolean isInterface) {
        if (mv != null) {
            mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }

    /**
     * 访问跳转指令
     *
     * @param opcode 操作码（如IFEQ, GOTO等）
     * @param label 跳转目标标签
     */
    public void visitJumpInsn(int opcode, Label label) {
        if (mv != null) {
            mv.visitJumpInsn(opcode, label);
        }
    }

    /**
     * 访问标签
     *
     * @param label 标签
     */
    public void visitLabel(Label label) {
        if (mv != null) {
            mv.visitLabel(label);
        }
    }

    /**
     * 访问LDC指令（加载常量）
     *
     * @param value 常量值
     */
    public void visitLdcInsn(Object value) {
        if (mv != null) {
            mv.visitLdcInsn(value);
        }
    }

    /**
     * 访问IINC指令（局部变量自增）
     *
     * @param var 局部变量索引
     * @param increment 增量
     */
    public void visitIincInsn(int var, int increment) {
        if (mv != null) {
            mv.visitIincInsn(var, increment);
        }
    }

    /**
     * 访问TABLESWITCH指令
     *
     * @param min 最小值
     * @param max 最大值
     * @param dflt 默认标签
     * @param labels 标签数组
     */
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        if (mv != null) {
            mv.visitTableSwitchInsn(min, max, dflt, labels);
        }
    }

    /**
     * 访问LOOKUPSWITCH指令
     *
     * @param dflt 默认标签
     * @param keys 键数组
     * @param labels 标签数组
     */
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        if (mv != null) {
            mv.visitLookupSwitchInsn(dflt, keys, labels);
        }
    }

    /**
     * 访问MULTIANEWARRAY指令（创建多维数组）
     *
     * @param descriptor 数组描述符
     * @param numDimensions 维度数
     */
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        if (mv != null) {
            mv.visitMultiANewArrayInsn(descriptor, numDimensions);
        }
    }

    /**
     * 访问最大栈深度和局部变量表大小
     *
     * @param maxStack 最大栈深度
     * @param maxLocals 局部变量表大小
     */
    public void visitMaxs(int maxStack, int maxLocals) {
        if (mv != null) {
            mv.visitMaxs(maxStack, maxLocals);
        }
    }

    /**
     * 访问方法结束
     */
    public void visitEnd() {
        if (mv != null) {
            mv.visitEnd();
        }
    }
}
