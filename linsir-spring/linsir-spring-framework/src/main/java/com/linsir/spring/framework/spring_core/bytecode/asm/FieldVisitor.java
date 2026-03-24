package com.linsir.spring.framework.spring_core.bytecode.asm;

/**
 * ASM字段访问器
 *
 * <p>用于访问和生成类字段的访问器。通过此类可以访问字段的注解和属性。
 *
 * <p>使用示例：
 * <pre>{@code
 * FieldVisitor fv = cw.visitField(ACC_PRIVATE, "name", "Ljava/lang/String;", null, null);
 * fv.visitAnnotation("Ljavax/persistence/Column;", true);
 * fv.visitEnd();
 * }</pre>
 *
 * @author linsir
 * @since 1.0
 * @see ClassVisitor
 * @see ClassWriter
 */
public abstract class FieldVisitor {

    /**
     * ASM API版本
     */
    protected final int api;

    /**
     * 下一个访问器（链式调用）
     */
    protected FieldVisitor fv;

    /**
     * 构造函数
     *
     * @param api ASM API版本
     */
    public FieldVisitor(int api) {
        this(api, null);
    }

    /**
     * 构造函数
     *
     * @param api ASM API版本
     * @param fv 下一个访问器
     */
    public FieldVisitor(int api, FieldVisitor fv) {
        this.api = api;
        this.fv = fv;
    }

    /**
     * 访问字段注解
     *
     * @param descriptor 注解类型描述符
     * @param visible 运行时是否可见
     * @return 注解访问器
     */
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (fv != null) {
            return fv.visitAnnotation(descriptor, visible);
        }
        return null;
    }

    /**
     * 访问字段结束
     */
    public void visitEnd() {
        if (fv != null) {
            fv.visitEnd();
        }
    }
}
