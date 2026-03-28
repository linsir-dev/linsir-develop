package com.linsir.abc.core.jvm.compile.processor;

import com.linsir.abc.core.jvm.compile.annotation.AutoToString;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * AutoToString注解处理器
 * <p>
 * 该处理器在编译期间扫描被{@link AutoToString}注解标记的类，
 * 自动生成对应的toString()方法实现类。
 * </p>
 *
 * <p><strong>处理流程：</strong></p>
 * <ol>
 *   <li>扫描被@AutoToString注解的类</li>
 *   <li>收集类的字段信息（排除static字段和指定排除的字段）</li>
 *   <li>生成对应的ToString实现类</li>
 * </ol>
 *
 * @author linsir
 * @version 1.0
 * @since 2026-03-28
 * @see AutoToString
 */
@SupportedAnnotationTypes("com.linsir.abc.core.jvm.compile.annotation.AutoToString")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class AutoToStringProcessor extends AbstractProcessor {

    /**
     * 消息报告器，用于输出编译期日志和错误信息
     */
    private Messager messager;

    /**
     * 文件创建器，用于创建生成的Java源文件
     */
    private Filer filer;

    /**
     * 处理器初始化方法
     * <p>
     * 在处理开始前被调用，用于获取处理环境相关的工具类。
     * </p>
     *
     * @param processingEnv 处理环境，提供访问编译器工具的方法
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();
    }

    /**
     * 处理注解的核心方法
     * <p>
     * 编译器在发现被支持的注解时会调用此方法。
     * 方法会遍历所有被@AutoToString注解的元素，为每个类生成toString实现。
     * </p>
     *
     * @param annotations 本次处理轮次中发现的注解类型集合
     * @param roundEnv    当前处理轮次的环境，提供访问被注解元素的方法
     * @return true表示这些注解已被处理，其他处理器无需再处理
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(AutoToString.class)) {
            // 检查注解是否应用于类
            if (element.getKind() != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@AutoToString只能用于类", element);
                continue;
            }

            TypeElement classElement = (TypeElement) element;
            AutoToString annotation = classElement.getAnnotation(AutoToString.class);

            try {
                generateToString(classElement, annotation);
                messager.printMessage(Diagnostic.Kind.NOTE,
                        "已为类 " + classElement.getSimpleName() + " 生成toString实现", classElement);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "生成toString方法失败: " + e.getMessage(), element);
            }
        }
        return true;
    }

    /**
     * 生成toString方法实现类
     * <p>
     * 根据被注解的类信息和注解配置，生成对应的ToString实现类。
     * 生成的类包含一个静态方法toString(Object obj)，返回格式化的字符串表示。
     * </p>
     *
     * @param classElement 被注解的类元素
     * @param annotation   AutoToString注解实例
     * @throws IOException 当文件创建或写入失败时抛出
     */
    private void generateToString(TypeElement classElement, AutoToString annotation) throws IOException {
        String className = classElement.getSimpleName().toString();
        String packageName = processingEnv.getElementUtils()
                .getPackageOf(classElement).getQualifiedName().toString();

        // 获取需要排除的字段名
        Set<String> excludeFields = Set.of(annotation.exclude());

        // 收集类字段
        List<FieldInfo> fields = collectFields(classElement, excludeFields, annotation.includeSuper());

        // 生成代码
        String generatedClassName = className + "ToStringImpl";
        JavaFileObject sourceFile = filer.createSourceFile(
                packageName + "." + generatedClassName, classElement);

        try (PrintWriter out = new PrintWriter(sourceFile.openWriter())) {
            writeGeneratedClass(out, packageName, generatedClassName, className, fields);
        }
    }

    /**
     * 收集类的字段信息
     * <p>
     * 遍历类及其父类（如果配置包含）的所有字段，
     * 排除static字段和指定排除的字段。
     * </p>
     *
     * @param classElement  类元素
     * @param excludeFields 需要排除的字段名集合
     * @param includeSuper  是否包含父类字段
     * @return 字段信息列表
     */
    private List<FieldInfo> collectFields(TypeElement classElement, Set<String> excludeFields, boolean includeSuper) {
        List<FieldInfo> fields = new ArrayList<>();

        // 收集当前类的字段
        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.FIELD) {
                VariableElement field = (VariableElement) enclosed;
                String fieldName = field.getSimpleName().toString();

                // 跳过静态字段和被排除的字段
                if (field.getModifiers().contains(Modifier.STATIC) ||
                        excludeFields.contains(fieldName)) {
                    continue;
                }

                fields.add(new FieldInfo(fieldName, field.asType().toString()));
            }
        }

        // 如果需要，递归收集父类字段
        if (includeSuper) {
            TypeElement superClass = (TypeElement) processingEnv.getTypeUtils()
                    .asElement(classElement.getSuperclass());
            if (superClass != null && !superClass.getQualifiedName().toString().equals("java.lang.Object")) {
                fields.addAll(collectFields(superClass, excludeFields, true));
            }
        }

        return fields;
    }

    /**
     * 写入生成的类文件内容
     * <p>
     * 生成包含toString静态方法的工具类。
     * </p>
     *
     * @param out               输出写入器
     * @param packageName       包名
     * @param generatedClassName 生成的类名
     * @param targetClassName   目标类名
     * @param fields            字段信息列表
     */
    private void writeGeneratedClass(PrintWriter out, String packageName,
                                     String generatedClassName, String targetClassName,
                                     List<FieldInfo> fields) {
        // 包声明
        out.println("package " + packageName + ";");
        out.println();

        // 类注释
        out.println("/**");
        out.println(" * 由AutoToStringProcessor自动生成的toString实现类");
        out.println(" * <p>");
        out.println(" * 该类为 {@link " + targetClassName + "} 提供toString()方法的实现。");
        out.println(" * </p>");
        out.println(" * <p><strong>注意：</strong>此类由注解处理器自动生成，请勿手动修改</p>");
        out.println(" *");
        out.println(" * @author AutoToStringProcessor");
        out.println(" * @version 1.0");
        out.println(" * @since 2026-03-28");
        out.println(" * @see " + targetClassName);
        out.println(" */");

        // 类声明
        out.println("public class " + generatedClassName + " {");
        out.println();

        // 私有构造方法，防止实例化
        out.println("    /**");
        out.println("     * 私有构造方法，防止实例化");
        out.println("     */");
        out.println("    private " + generatedClassName + "() {");
        out.println("        throw new AssertionError(\"工具类不应被实例化\");");
        out.println("    }");
        out.println();

        // toString方法
        out.println("    /**");
        out.println("     * 生成目标对象的字符串表示");
        out.println("     * <p>");
        out.println("     * 返回格式：ClassName{field1='value1', field2='value2', ...}");
        out.println("     * </p>");
        out.println("     *");
        out.println("     * @param obj 目标对象，不能为null");
        out.println("     * @return 对象的字符串表示");
        out.println("     * @throws NullPointerException 如果obj为null");
        out.println("     */");
        out.println("    public static String toString(" + targetClassName + " obj) {");
        out.println("        if (obj == null) {");
        out.println("            return \"null\";");
        out.println("        }");
        out.println();
        out.println("        StringBuilder sb = new StringBuilder();");
        out.println("        sb.append(\"" + targetClassName + "{\");");

        // 添加字段输出
        for (int i = 0; i < fields.size(); i++) {
            FieldInfo field = fields.get(i);
            if (i > 0) {
                out.println("        sb.append(\", \");");
            }
            out.println("        sb.append(\"" + field.name + "=\");");
            out.println("        sb.append(obj." + field.name + ");");
        }

        out.println("        sb.append('}');");
        out.println("        return sb.toString();");
        out.println("    }");
        out.println("}");
    }

    /**
     * 字段信息内部类
     * <p>
     * 用于存储字段的名称和类型信息。
     * </p>
     */
    private static class FieldInfo {
        /** 字段名 */
        final String name;
        /** 字段类型 */
        final String type;

        /**
         * 构造字段信息
         *
         * @param name 字段名
         * @param type 字段类型
         */
        FieldInfo(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
}
