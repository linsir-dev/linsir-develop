package com.linsir.spring.framework.spring_core.bytecode.asm;

/**
 * ASM标签
 *
 * <p>用于标记字节码中的位置，主要用于跳转指令的目标位置。
 * 在字节码生成过程中，Label用于标记代码中的特定位置，
 * 然后在跳转指令中引用这些位置。
 *
 * <p>使用示例：
 * <pre>{@code
 * Label start = new Label();
 * Label end = new Label();
 *
 * mv.visitLabel(start);
 * // ... 生成代码
 * mv.visitJumpInsn(IFEQ, end);
 * // ... 生成代码
 * mv.visitLabel(end);
 * }</pre>
 *
 * @author linsir
 * @since 1.0
 * @see MethodVisitor
 */
public class Label {

    /**
     * 标签标识
     */
    private final int id;

    /**
     * 全局计数器
     */
    private static int counter = 0;

    /**
     * 字节码偏移量（在解析后设置）
     */
    private int offset = -1;

    /**
     * 是否已解析
     */
    private boolean resolved = false;

    /**
     * 构造函数
     */
    public Label() {
        this.id = counter++;
    }

    /**
     * 获取标签ID
     *
     * @return 标签ID
     */
    public int getId() {
        return id;
    }

    /**
     * 获取字节码偏移量
     *
     * @return 偏移量，如果未解析返回-1
     */
    public int getOffset() {
        return offset;
    }

    /**
     * 设置字节码偏移量
     *
     * @param offset 偏移量
     */
    public void setOffset(int offset) {
        this.offset = offset;
        this.resolved = true;
    }

    /**
     * 检查标签是否已解析
     *
     * @return true如果已解析
     */
    public boolean isResolved() {
        return resolved;
    }

    @Override
    public String toString() {
        return "L" + id + (resolved ? "(" + offset + ")" : "");
    }
}
