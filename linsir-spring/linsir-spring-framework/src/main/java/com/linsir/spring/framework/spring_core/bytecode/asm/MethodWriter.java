package com.linsir.spring.framework.spring_core.bytecode.asm;

import java.util.ArrayList;
import java.util.List;

/**
 * ASM方法写入器
 *
 * <p>MethodVisitor的具体实现，用于实际生成方法的字节码。
 * 收集所有访问的指令，最终输出为字节码数组。
 *
 * <p>此类负责：
 * <ul>
 *   <li>收集字节码指令</li>
 *   <li>计算栈映射帧</li>
 *   <li>生成最终的字节码</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 * @see MethodVisitor
 * @see ClassWriter
 */
public class MethodWriter extends MethodVisitor {

    /**
     * 方法信息
     */
    private final ClassWriter.MethodInfo methodInfo;

    /**
     * 字节码指令列表
     */
    private final List<Instruction> instructions = new ArrayList<>();

    /**
     * 最大栈深度
     */
    private int maxStack;

    /**
     * 局部变量表大小
     */
    private int maxLocals;

    /**
     * 构造函数
     *
     * @param methodInfo 方法信息
     */
    public MethodWriter(ClassWriter.MethodInfo methodInfo) {
        super(Opcodes.ASM9);
        this.methodInfo = methodInfo;

        // 计算局部变量表初始大小（this + 参数）
        this.maxLocals = calculateInitialLocals(methodInfo.descriptor);
    }

    /**
     * 计算初始局部变量数
     *
     * @param descriptor 方法描述符
     * @return 初始局部变量数
     */
    private int calculateInitialLocals(String descriptor) {
        // 非静态方法有this
        int locals = (methodInfo.access & Opcodes.ACC_STATIC) == 0 ? 1 : 0;

        // 解析参数
        int i = 1; // 跳过'('
        while (i < descriptor.length() && descriptor.charAt(i) != ')') {
            char c = descriptor.charAt(i);
            if (c == 'J' || c == 'D') {
                // long和double占两个槽位
                locals += 2;
                i++;
            } else if (c == 'L') {
                // 对象类型
                locals++;
                // 跳过到';'
                while (descriptor.charAt(i) != ';') i++;
                i++;
            } else if (c == '[') {
                // 数组类型
                locals++;
                // 跳过数组维度
                while (descriptor.charAt(i) == '[') i++;
                // 跳过元素类型
                if (descriptor.charAt(i) == 'L') {
                    while (descriptor.charAt(i) != ';') i++;
                }
                i++;
            } else {
                // 基本类型
                locals++;
                i++;
            }
        }

        return locals;
    }

    @Override
    public void visitCode() {
        // 方法代码开始
    }

    @Override
    public void visitInsn(int opcode) {
        instructions.add(new Instruction(opcode));
        updateStack(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        instructions.add(new IntInstruction(opcode, operand));
        updateStack(opcode);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        instructions.add(new VarInstruction(opcode, var));
        updateStack(opcode);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        instructions.add(new TypeInstruction(opcode, type));
        updateStack(opcode);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        instructions.add(new FieldInstruction(opcode, owner, name, descriptor));
        updateStack(opcode);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name,
                                String descriptor, boolean isInterface) {
        instructions.add(new MethodInstruction(opcode, owner, name, descriptor, isInterface));
        updateStack(opcode);
    }

    @Override
    public void visitLdcInsn(Object value) {
        // LdcInstruction does not extend Instruction, so we handle it separately
        // For simplicity, we just track the maxStack here
        maxStack = Math.max(maxStack, 1);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        this.maxStack = Math.max(this.maxStack, maxStack);
        this.maxLocals = Math.max(this.maxLocals, maxLocals);
    }

    @Override
    public void visitEnd() {
        // 方法结束，可以在这里生成最终字节码
    }

    /**
     * 更新栈深度
     *
     * @param opcode 操作码
     */
    private void updateStack(int opcode) {
        // 简化实现：根据操作码类型更新栈深度
        // 实际实现应该更精确地计算每个指令的栈影响

        int stackChange = getStackChange(opcode);
        maxStack = Math.max(maxStack, stackChange);
    }

    /**
     * 获取操作码的栈变化
     *
     * @param opcode 操作码
     * @return 栈变化（正数表示入栈，负数表示出栈）
     */
    private int getStackChange(int opcode) {
        // 简化实现
        switch (opcode) {
            case Opcodes.ACONST_NULL:
            case Opcodes.ICONST_M1:
            case Opcodes.ICONST_0:
            case Opcodes.ICONST_1:
            case Opcodes.ICONST_2:
            case Opcodes.ICONST_3:
            case Opcodes.ICONST_4:
            case Opcodes.ICONST_5:
            case Opcodes.LCONST_0:
            case Opcodes.LCONST_1:
            case Opcodes.FCONST_0:
            case Opcodes.FCONST_1:
            case Opcodes.FCONST_2:
            case Opcodes.DCONST_0:
            case Opcodes.DCONST_1:
            case Opcodes.BIPUSH:
            case Opcodes.SIPUSH:
                return 1;
            case Opcodes.IRETURN:
            case Opcodes.FRETURN:
            case Opcodes.ARETURN:
            case Opcodes.IALOAD:
            case Opcodes.FALOAD:
            case Opcodes.AALOAD:
            case Opcodes.BALOAD:
            case Opcodes.CALOAD:
            case Opcodes.SALOAD:
                return -1;
            case Opcodes.LRETURN:
            case Opcodes.DRETURN:
            case Opcodes.LALOAD:
            case Opcodes.DALOAD:
                return -2;
            case Opcodes.IADD:
            case Opcodes.ISUB:
            case Opcodes.IMUL:
            case Opcodes.IDIV:
            case Opcodes.IREM:
            case Opcodes.IAND:
            case Opcodes.IOR:
            case Opcodes.IXOR:
            case Opcodes.ISHL:
            case Opcodes.ISHR:
            case Opcodes.IUSHR:
            case Opcodes.LCMP:
            case Opcodes.FCMPL:
            case Opcodes.FCMPG:
            case Opcodes.DCMPL:
            case Opcodes.DCMPG:
                return -1;
            case Opcodes.LADD:
            case Opcodes.LSUB:
            case Opcodes.LMUL:
            case Opcodes.LDIV:
            case Opcodes.LREM:
            case Opcodes.LAND:
            case Opcodes.LOR:
            case Opcodes.LXOR:
                return -2;
            case Opcodes.RETURN:
            case Opcodes.POP:
                return 0;
            case Opcodes.POP2:
                return 0;
            case Opcodes.DUP:
                return 1;
            default:
                return 1;
        }
    }

    /**
     * 获取生成的字节码
     *
     * @return 字节码数组
     */
    public byte[] toByteArray() {
        // 简化实现：实际应该生成完整的字节码
        return new byte[0];
    }

    // 指令内部类

    private static class Instruction {
        final int opcode;

        Instruction(int opcode) {
            this.opcode = opcode;
        }
    }

    private static class IntInstruction extends Instruction {
        final int operand;

        IntInstruction(int opcode, int operand) {
            super(opcode);
            this.operand = operand;
        }
    }

    private static class VarInstruction extends Instruction {
        final int var;

        VarInstruction(int opcode, int var) {
            super(opcode);
            this.var = var;
        }
    }

    private static class TypeInstruction extends Instruction {
        final String type;

        TypeInstruction(int opcode, String type) {
            super(opcode);
            this.type = type;
        }
    }

    private static class FieldInstruction extends Instruction {
        final String owner;
        final String name;
        final String descriptor;

        FieldInstruction(int opcode, String owner, String name, String descriptor) {
            super(opcode);
            this.owner = owner;
            this.name = name;
            this.descriptor = descriptor;
        }
    }

    private static class MethodInstruction extends Instruction {
        final String owner;
        final String name;
        final String descriptor;
        final boolean isInterface;

        MethodInstruction(int opcode, String owner, String name,
                         String descriptor, boolean isInterface) {
            super(opcode);
            this.owner = owner;
            this.name = name;
            this.descriptor = descriptor;
            this.isInterface = isInterface;
        }
    }

}
