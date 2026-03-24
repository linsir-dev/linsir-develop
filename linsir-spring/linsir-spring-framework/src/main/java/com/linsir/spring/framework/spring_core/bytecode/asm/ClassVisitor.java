package com.linsir.spring.framework.spring_core.bytecode.asm;

/**
 * ASM类访问器
 *
 * <p>ASM框架的核心抽象类，采用访问者模式（Visitor Pattern）遍历类的结构。
 * 通过继承此类并重写相应方法，可以在类被访问时插入自定义逻辑。
 *
 * <p>访问顺序：
 * <ol>
 *   <li>visit - 访问类头信息</li>
 *   <li>visitSource - 访问源文件信息（可选）</li>
 *   <li>visitField - 访问字段（可多次）</li>
 *   <li>visitMethod - 访问方法（可多次）</li>
 *   <li>visitEnd - 访问结束</li>
 * </ol>
 *
 * <p>使用示例：
 * <pre>{@code
 * public class MyClassVisitor extends ClassVisitor {
 *     public MyClassVisitor(ClassVisitor cv) {
 *         super(Opcodes.ASM9, cv);
 *     }
 *
 *     @Override
 *     public MethodVisitor visitMethod(int access, String name, String descriptor,
 *                                      String signature, String[] exceptions) {
 *         System.out.println("访问方法: " + name);
 *         return super.visitMethod(access, name, descriptor, signature, exceptions);
 *     }
 * }
 * }</pre>
 *
 * @author linsir
 * @since 1.0
 * @see ClassWriter
 * @see MethodVisitor
 * @see Opcodes
 */
public abstract class ClassVisitor {

    /**
     * ASM API版本
     */
    protected final int api;

    /**
     * 下一个访问器（链式调用）
     */
    protected ClassVisitor cv;

    /**
     * 构造函数
     *
     * @param api ASM API版本
     */
    public ClassVisitor(int api) {
        this(api, null);
    }

    /**
     * 构造函数
     *
     * @param api ASM API版本
     * @param cv 下一个访问器
     */
    public ClassVisitor(int api, ClassVisitor cv) {
        this.api = api;
        this.cv = cv;
    }

    /**
     * 访问类头信息
     *
     * @param version 类版本（如Opcodes.V1_8）
     * @param access 访问标志（如Opcodes.ACC_PUBLIC）
     * @param name 类名（内部格式，如"java/lang/Object"）
     * @param signature 泛型签名（可能为null）
     * @param superName 父类名（内部格式）
     * @param interfaces 实现的接口数组（内部格式）
     */
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        if (cv != null) {
            cv.visit(version, access, name, signature, superName, interfaces);
        }
    }

    /**
     * 访问源文件信息
     *
     * @param source 源文件名
     * @param debug 调试信息
     */
    public void visitSource(String source, String debug) {
        if (cv != null) {
            cv.visitSource(source, debug);
        }
    }

    /**
     * 访问字段
     *
     * @param access 访问标志
     * @param name 字段名
     * @param descriptor 字段描述符
     * @param signature 泛型签名
     * @param value 常量值（仅对静态常量字段）
     * @return 字段访问器，或null表示不需要访问此字段
     */
    public FieldVisitor visitField(int access, String name, String descriptor,
                                   String signature, Object value) {
        if (cv != null) {
            return cv.visitField(access, name, descriptor, signature, value);
        }
        return null;
    }

    /**
     * 访问方法
     *
     * @param access 访问标志
     * @param name 方法名
     * @param descriptor 方法描述符
     * @param signature 泛型签名
     * @param exceptions 抛出的异常数组
     * @return 方法访问器，或null表示不需要访问此方法
     */
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        if (cv != null) {
            return cv.visitMethod(access, name, descriptor, signature, exceptions);
        }
        return null;
    }

    /**
     * 访问结束
     *
     * <p>在所有其他访问方法之后调用。
     */
    public void visitEnd() {
        if (cv != null) {
            cv.visitEnd();
        }
    }
}
