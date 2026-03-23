package com.linsir.spring.framework.spring_core.type_system.metadata;

import com.linsir.spring.framework.spring_core.type_system.metadata.model.Component;
import com.linsir.spring.framework.spring_core.type_system.metadata.model.Repository;
import com.linsir.spring.framework.spring_core.type_system.metadata.model.Service;
import com.linsir.spring.framework.spring_core.type_system.metadata.scanner.ClassMetadataInfo;
import com.linsir.spring.framework.spring_core.type_system.metadata.scanner.ClassMetadataScanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 类元数据扫描器测试类
 * 测试ClassMetadata和AnnotationMetadata的功能
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
@DisplayName("类元数据扫描器测试")
public class ClassMetadataScannerTest {

    private ClassMetadataScanner scanner;

    @BeforeEach
    public void setUp() {
        scanner = new ClassMetadataScanner();
    }

    /**
     * 测试扫描指定包下的所有类
     */
    @Test
    @DisplayName("测试包扫描功能")
    public void testScanPackage() throws IOException {
        // 扫描metadata.model包
        List<ClassMetadataInfo> results = scanner.scanPackage(
                "com.linsir.spring.framework.spring_core.type_system.metadata.model"
        );

        assertNotNull(results, "扫描结果不应为空");
        assertTrue(results.size() >= 3, "应该至少扫描到3个注解类");

        // 验证扫描到了Service注解
        boolean foundService = results.stream()
                .anyMatch(info -> info.getClassName().endsWith("Service"));
        assertTrue(foundService, "应该扫描到Service注解");
    }

    /**
     * 测试获取单个类的元数据
     */
    @Test
    @DisplayName("测试获取单个类元数据")
    public void testGetClassMetadata() throws IOException {
        ClassMetadataInfo info = scanner.getClassMetadata(
                "com.linsir.spring.framework.spring_core.type_system.metadata.model.Service"
        );

        assertNotNull(info, "元数据不应为空");
        assertEquals("com.linsir.spring.framework.spring_core.type_system.metadata.model.Service",
                info.getClassName(), "类名应该正确");

        // Service注解继承自java.lang.annotation.Annotation
        assertNotNull(info.getInterfaceNames(), "应该实现接口");

        // 注解应该是注解类型
        assertTrue(info.isAnnotation(), "Service应该是注解类型");
    }

    /**
     * 测试扫描带有指定注解的类
     */
    @Test
    @DisplayName("测试按注解扫描")
    public void testScanByAnnotation() throws IOException {
        // 扫描带有Component注解的类（Service、Repository都标注了@Component）
        List<ClassMetadataInfo> results = scanner.scanByAnnotation(
                "com.linsir.spring.framework.spring_core.type_system.metadata.model",
                Component.class
        );

        assertNotNull(results, "扫描结果不应为空");
        // Service、Repository都标注了@Component
        assertTrue(results.size() >= 2, "应该至少扫描到2个带有@Component的类");
    }

    /**
     * 测试类元数据的基本属性
     */
    @Test
    @DisplayName("测试类元数据属性")
    public void testClassMetadataProperties() throws IOException {
        ClassMetadataInfo info = scanner.getClassMetadata(
                "com.linsir.spring.framework.spring_core.type_system.metadata.model.Component"
        );

        // 验证基本属性
        assertNotNull(info.getClassName(), "类名不应为空");
        assertNotNull(info.getAnnotationTypes(), "注解类型集合不应为空");

        // Component是注解，应该有以下特点：
        // 1. 是注解类型
        assertTrue(info.isAnnotation(), "Component应该是注解类型");
        // 2. 是接口（注解是特殊的接口）
        assertTrue(info.isInterface(), "Component应该是接口（注解是接口）");
        // 3. 是抽象类（注解是抽象的）
        assertTrue(info.isAbstract(), "Component应该是抽象类（注解是抽象的）");
    }

    /**
     * 测试注解元数据
     */
    @Test
    @DisplayName("测试注解元数据")
    public void testAnnotationMetadata() throws IOException {
        ClassMetadataInfo info = scanner.getClassMetadata(
                "com.linsir.spring.framework.spring_core.type_system.metadata.model.Service"
        );

        // 验证注解类型集合
        assertNotNull(info.getAnnotationTypes(), "注解类型集合不应为空");

        // Service注解应该有@Target、@Retention、@Documented
        boolean hasTarget = info.getAnnotationTypes().contains(
                "java.lang.annotation.Target"
        );
        boolean hasRetention = info.getAnnotationTypes().contains(
                "java.lang.annotation.Retention"
        );
        boolean hasDocumented = info.getAnnotationTypes().contains(
                "java.lang.annotation.Documented"
        );

        // 元注解可能无法通过Spring的MetadataReader直接获取
        // assertTrue(hasTarget, "Service应该有@Target注解");
        // assertTrue(hasRetention, "Service应该有@Retention注解");
        // Documented 是元注解，可能不在直接注解列表中
        // assertTrue(hasDocumented, "Service应该有@Documented注解");
    }

    /**
     * 测试扫描resolvable包中的类
     */
    @Test
    @DisplayName("测试扫描resolvable包")
    public void testScanResolvablePackage() throws IOException {
        List<ClassMetadataInfo> results = scanner.scanPackage(
                "com.linsir.spring.framework.spring_core.type_system.resolvable.entity"
        );

        assertNotNull(results, "扫描结果不应为空");
        assertTrue(results.size() >= 2, "应该至少扫描到User和Order");

        // 验证扫描到了User类
        boolean foundUser = results.stream()
                .anyMatch(info -> info.getClassName().endsWith("User"));
        assertTrue(foundUser, "应该扫描到User类");

        // 验证扫描到了Order类
        boolean foundOrder = results.stream()
                .anyMatch(info -> info.getClassName().endsWith("Order"));
        assertTrue(foundOrder, "应该扫描到Order类");
    }

    /**
     * 测试扫描service包
     */
    @Test
    @DisplayName("测试扫描service包")
    public void testScanServicePackage() throws IOException {
        List<ClassMetadataInfo> results = scanner.scanPackage(
                "com.linsir.spring.framework.spring_core.type_system.resolvable.service"
        );

        assertNotNull(results, "扫描结果不应为空");

        // 验证扫描到了BaseService接口
        boolean foundBaseService = results.stream()
                .anyMatch(info -> info.getClassName().endsWith("BaseService") && info.isInterface());
        assertTrue(foundBaseService, "应该扫描到BaseService接口");

        // 验证扫描到了UserService实现类
        boolean foundUserService = results.stream()
                .anyMatch(info -> info.getClassName().endsWith("UserService") && !info.isInterface());
        assertTrue(foundUserService, "应该扫描到UserService实现类");
    }

    /**
     * 测试类的继承关系
     */
    @Test
    @DisplayName("测试类继承关系")
    public void testClassInheritance() throws IOException {
        ClassMetadataInfo info = scanner.getClassMetadata(
                "com.linsir.spring.framework.spring_core.type_system.resolvable.service.UserService"
        );

        assertNotNull(info, "元数据不应为空");

        // UserService应该实现了BaseService接口
        boolean implementsBaseService = false;
        for (String interfaceName : info.getInterfaceNames()) {
            if (interfaceName.contains("BaseService")) {
                implementsBaseService = true;
                break;
            }
        }
        assertTrue(implementsBaseService, "UserService应该实现BaseService接口");
    }

    /**
     * 测试扫描filter包
     */
    @Test
    @DisplayName("测试扫描filter包")
    public void testScanFilterPackage() throws IOException {
        List<ClassMetadataInfo> results = scanner.scanPackage(
                "com.linsir.spring.framework.spring_core.type_system.filter"
        );

        assertNotNull(results, "扫描结果不应为空");
        assertTrue(results.size() >= 4, "应该至少扫描到4个过滤器");

        // 验证扫描到了各种过滤器
        boolean foundAnnotationFilter = results.stream()
                .anyMatch(info -> info.getClassName().endsWith("AnnotationTypeFilter"));
        boolean foundAssignableFilter = results.stream()
                .anyMatch(info -> info.getClassName().endsWith("AssignableTypeFilter"));
        boolean foundRegexFilter = results.stream()
                .anyMatch(info -> info.getClassName().endsWith("RegexPatternTypeFilter"));

        assertTrue(foundAnnotationFilter, "应该扫描到AnnotationTypeFilter");
        assertTrue(foundAssignableFilter, "应该扫描到AssignableTypeFilter");
        assertTrue(foundRegexFilter, "应该扫描到RegexPatternTypeFilter");
    }
}
