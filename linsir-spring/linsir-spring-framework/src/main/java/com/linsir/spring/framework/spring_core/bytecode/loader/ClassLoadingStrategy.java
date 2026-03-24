package com.linsir.spring.framework.spring_core.bytecode.loader;

/**
 * 类加载策略接口
 *
 * <p>定义了类加载的策略模式接口。不同的策略可以控制
 * 类加载的方式和时机。
 *
 * <p>常见策略：
 * <ul>
 *   <li>ChildFirstStrategy - 子类加载器优先</li>
 *   <li>ParentFirstStrategy - 父类加载器优先（默认）</li>
 *   <li>SelfFirstStrategy - 自身优先</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 * @see BytecodeClassLoader
 */
public interface ClassLoadingStrategy {

    /**
     * 加载类
     *
     * @param name 类全限定名
     * @param loader 类加载器
     * @return Class对象
     * @throws ClassNotFoundException 如果找不到类
     */
    Class<?> loadClass(String name, ClassLoader loader) throws ClassNotFoundException;

    /**
     * 父类优先策略
     *
     * <p>标准的双亲委派模型，先委托父加载器加载。
     */
    ClassLoadingStrategy PARENT_FIRST = new ClassLoadingStrategy() {
        @Override
        public Class<?> loadClass(String name, ClassLoader loader) throws ClassNotFoundException {
            // 双亲委派：先让父加载器尝试加载
            ClassLoader parent = loader.getParent();
            if (parent != null) {
                try {
                    return parent.loadClass(name);
                } catch (ClassNotFoundException e) {
                    // 父加载器无法加载，继续
                }
            }

            // 父加载器无法加载，自己加载
            if (loader instanceof BytecodeClassLoader) {
                Class<?> clazz = ((BytecodeClassLoader) loader).getDefinedClass(name);
                if (clazz != null) {
                    return clazz;
                }
            }

            throw new ClassNotFoundException(name);
        }
    };

    /**
     * 子类优先策略
     *
     * <p>先尝试自己加载，找不到再委托父加载器。
     * 适用于需要覆盖父加载器中的类的场景。
     */
    ClassLoadingStrategy CHILD_FIRST = new ClassLoadingStrategy() {
        @Override
        public Class<?> loadClass(String name, ClassLoader loader) throws ClassNotFoundException {
            // 先自己尝试加载
            if (loader instanceof BytecodeClassLoader) {
                Class<?> clazz = ((BytecodeClassLoader) loader).getDefinedClass(name);
                if (clazz != null) {
                    return clazz;
                }
            }

            // 自己无法加载，委托给父加载器
            ClassLoader parent = loader.getParent();
            if (parent != null) {
                return parent.loadClass(name);
            }

            throw new ClassNotFoundException(name);
        }
    };
}
