package com.linsir.spring.framework.spring_core.bytecode.asm;

/**
 * ASM注解访问器
 *
 * <p>用于访问和生成注解的访问器。通过此类可以访问注解的属性和值。
 *
 * <p>使用示例：
 * <pre>{@code
 * AnnotationVisitor av = fv.visitAnnotation("Ljavax/persistence/Column;", true);
 * av.visit("name", "user_name");
 * av.visit("nullable", false);
 * av.visit("length", 100);
 * av.visitEnd();
 * }</pre>
 *
 * @author linsir
 * @since 1.0
 * @see FieldVisitor
 * @see MethodVisitor
 * @see ClassVisitor
 */
public abstract class AnnotationVisitor {

    /**
     * ASM API版本
     */
    protected final int api;

    /**
     * 下一个访问器（链式调用）
     */
    protected AnnotationVisitor av;

    /**
     * 构造函数
     *
     * @param api ASM API版本
     */
    public AnnotationVisitor(int api) {
        this(api, null);
    }

    /**
     * 构造函数
     *
     * @param api ASM API版本
     * @param av 下一个访问器
     */
    public AnnotationVisitor(int api, AnnotationVisitor av) {
        this.api = api;
        this.av = av;
    }

    /**
     * 访问注解属性
     *
     * @param name 属性名
     * @param value 属性值（基本类型、String、Type或数组）
     */
    public void visit(String name, Object value) {
        if (av != null) {
            av.visit(name, value);
        }
    }

    /**
     * 访问枚举属性
     *
     * @param name 属性名
     * @param descriptor 枚举类型描述符
     * @param value 枚举值
     */
    public void visitEnum(String name, String descriptor, String value) {
        if (av != null) {
            av.visitEnum(name, descriptor, value);
        }
    }

    /**
     * 访问嵌套注解
     *
     * @param name 属性名
     * @param descriptor 注解类型描述符
     * @return 嵌套注解访问器
     */
    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        if (av != null) {
            return av.visitAnnotation(name, descriptor);
        }
        return null;
    }

    /**
     * 访问数组属性
     *
     * @param name 属性名
     * @return 数组访问器
     */
    public AnnotationVisitor visitArray(String name) {
        if (av != null) {
            return av.visitArray(name);
        }
        return null;
    }

    /**
     * 访问注解结束
     */
    public void visitEnd() {
        if (av != null) {
            av.visitEnd();
        }
    }
}
