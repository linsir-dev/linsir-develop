package com.linsir.spring.framework.spring_core.type_system.component;

import com.linsir.spring.framework.spring_core.type_system.filter.AnnotationTypeFilter;
import com.linsir.spring.framework.spring_core.type_system.filter.AssignableTypeFilter;
import com.linsir.spring.framework.spring_core.type_system.filter.CompositeTypeFilter;
import com.linsir.spring.framework.spring_core.type_system.filter.RegexPatternTypeFilter;
import com.linsir.spring.framework.spring_core.type_system.metadata.model.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组件扫描器测试类
 * 测试类型系统各组件的协作
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
@DisplayName("组件扫描器测试")
public class ComponentScannerTest {

    private ComponentScanner scanner;

    @BeforeEach
    public void setUp() {
        scanner = new ComponentScanner();
    }

    /**
     * 测试基本扫描功能
     */
    @Test
    @DisplayName("测试基本扫描功能")
    public void testBasicScan() throws IOException {
        // 扫描resolvable.entity包
        ScanResult result = scanner.scan(
                "com.linsir.spring.framework.spring_core.type_system.resolvable.entity"
        );

        assertNotNull(result, "扫描结果不应为空");
        assertFalse(result.getComponents().isEmpty(), "应该扫描到组件");

        // 验证扫描到了User和Order
        boolean foundUser = result.getScannedClasses().stream()
                .anyMatch(name -> name.endsWith("User"));
        boolean foundOrder = result.getScannedClasses().stream()
                .anyMatch(name -> name.endsWith("Order"));

        assertTrue(foundUser, "应该扫描到User类");
        assertTrue(foundOrder, "应该扫描到Order类");

        System.out.println(result.getStatistics());
    }

    /**
     * 测试使用注解过滤器扫描
     */
    @Test
    @DisplayName("测试使用注解过滤器扫描")
    public void testScanWithAnnotationFilter() throws IOException {
        // 添加注解过滤器，只扫描标注了@Service的类
        scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));

        // 扫描metadata.model包（包含@Service注解定义）
        ScanResult result = scanner.scan(
                "com.linsir.spring.framework.spring_core.type_system.metadata.model"
        );

        assertNotNull(result, "扫描结果不应为空");

        // 验证扫描结果
        System.out.println("扫描到的类: " + result.getScannedClasses());
        System.out.println("排除的类: " + result.getExcludedClasses());
    }

    /**
     * 测试使用可分配类型过滤器扫描
     */
    @Test
    @DisplayName("测试使用可分配类型过滤器扫描")
    public void testScanWithAssignableFilter() throws IOException {
        // 添加过滤器，只扫描实现了TypeFilter接口的类
        scanner.addIncludeFilter(new AssignableTypeFilter(
                com.linsir.spring.framework.spring_core.type_system.filter.TypeFilter.class
        ));

        // 扫描filter包
        ScanResult result = scanner.scan(
                "com.linsir.spring.framework.spring_core.type_system.filter"
        );

        assertNotNull(result, "扫描结果不应为空");

        // 应该扫描到所有实现了TypeFilter的类
        boolean foundAnnotationFilter = result.getScannedClasses().stream()
                .anyMatch(name -> name.endsWith("AnnotationTypeFilter"));
        boolean foundAssignableFilter = result.getScannedClasses().stream()
                .anyMatch(name -> name.endsWith("AssignableTypeFilter"));

        assertTrue(foundAnnotationFilter, "应该扫描到AnnotationTypeFilter");
        assertTrue(foundAssignableFilter, "应该扫描到AssignableTypeFilter");

        System.out.println(result.getStatistics());
    }

    /**
     * 测试使用正则过滤器扫描
     */
    @Test
    @DisplayName("测试使用正则过滤器扫描")
    public void testScanWithRegexFilter() throws IOException {
        // 添加过滤器，只扫描类名包含"Service"的类
        scanner.addIncludeFilter(new RegexPatternTypeFilter(".*Service.*"));

        // 扫描resolvable.service包
        ScanResult result = scanner.scan(
                "com.linsir.spring.framework.spring_core.type_system.resolvable.service"
        );

        assertNotNull(result, "扫描结果不应为空");

        // 应该扫描到UserService和BaseService
        boolean foundUserService = result.getScannedClasses().stream()
                .anyMatch(name -> name.endsWith("UserService"));
        boolean foundBaseService = result.getScannedClasses().stream()
                .anyMatch(name -> name.endsWith("BaseService"));

        assertTrue(foundUserService, "应该扫描到UserService");
        assertTrue(foundBaseService, "应该扫描到BaseService");

        System.out.println(result.getStatistics());
    }

    /**
     * 测试使用组合过滤器扫描
     */
    @Test
    @DisplayName("测试使用组合过滤器扫描")
    public void testScanWithCompositeFilter() throws IOException {
        // 创建组合过滤器：类名包含"Filter" AND 实现TypeFilter接口
        RegexPatternTypeFilter regexFilter = new RegexPatternTypeFilter(".*Filter.*");
        AssignableTypeFilter assignableFilter = new AssignableTypeFilter(
                com.linsir.spring.framework.spring_core.type_system.filter.TypeFilter.class
        );

        CompositeTypeFilter compositeFilter = new CompositeTypeFilter(
                CompositeTypeFilter.MatchMode.ALL,
                regexFilter,
                assignableFilter
        );

        scanner.addIncludeFilter(compositeFilter);

        // 扫描整个type_system包
        ScanResult result = scanner.scan(
                "com.linsir.spring.framework.spring_core.type_system"
        );

        assertNotNull(result, "扫描结果不应为空");

        // 验证扫描到了所有过滤器
        boolean foundAnnotationFilter = result.getScannedClasses().stream()
                .anyMatch(name -> name.endsWith("AnnotationTypeFilter"));
        boolean foundAssignableFilter = result.getScannedClasses().stream()
                .anyMatch(name -> name.endsWith("AssignableTypeFilter"));
        boolean foundRegexFilter = result.getScannedClasses().stream()
                .anyMatch(name -> name.endsWith("RegexPatternTypeFilter"));
        boolean foundCompositeFilter = result.getScannedClasses().stream()
                .anyMatch(name -> name.endsWith("CompositeTypeFilter"));

        assertTrue(foundAnnotationFilter, "应该扫描到AnnotationTypeFilter");
        assertTrue(foundAssignableFilter, "应该扫描到AssignableTypeFilter");
        assertTrue(foundRegexFilter, "应该扫描到RegexPatternTypeFilter");
        assertTrue(foundCompositeFilter, "应该扫描到CompositeTypeFilter");

        System.out.println(result.getStatistics());
    }

    /**
     * 测试使用排除过滤器
     */
    @Test
    @DisplayName("测试使用排除过滤器")
    public void testScanWithExcludeFilter() throws IOException {
        // 添加排除过滤器，排除类名包含"Test"的类
        scanner.addExcludeFilter(new RegexPatternTypeFilter(".*Test.*"));

        // 扫描resolvable包
        ScanResult result = scanner.scan(
                "com.linsir.spring.framework.spring_core.type_system.resolvable"
        );

        assertNotNull(result, "扫描结果不应为空");

        // 验证没有扫描到测试类
        boolean foundTestClass = result.getScannedClasses().stream()
                .anyMatch(name -> name.endsWith("Test"));

        assertFalse(foundTestClass, "不应该扫描到测试类");

        System.out.println(result.getStatistics());
    }

    /**
     * 测试同时包含包含过滤器和排除过滤器
     */
    @Test
    @DisplayName("测试包含和排除过滤器组合")
    public void testScanWithIncludeAndExcludeFilters() throws IOException {
        // 包含：类名包含"Service"
        scanner.addIncludeFilter(new RegexPatternTypeFilter(".*Service.*"));

        // 排除：类名包含"Base"
        scanner.addExcludeFilter(new RegexPatternTypeFilter(".*Base.*"));

        // 扫描resolvable.service包
        ScanResult result = scanner.scan(
                "com.linsir.spring.framework.spring_core.type_system.resolvable.service"
        );

        assertNotNull(result, "扫描结果不应为空");

        // 应该扫描到UserService，但不包括BaseService
        boolean foundUserService = result.getScannedClasses().stream()
                .anyMatch(name -> name.endsWith("UserService"));
        boolean foundBaseService = result.getScannedClasses().stream()
                .anyMatch(name -> name.endsWith("BaseService"));

        assertTrue(foundUserService, "应该扫描到UserService");
        assertFalse(foundBaseService, "不应该扫描到BaseService");

        System.out.println(result.getStatistics());
    }

    /**
     * 测试扫描空包
     */
    @Test
    @DisplayName("测试扫描空包")
    public void testScanEmptyPackage() throws IOException {
        // 扫描一个不存在的包
        ScanResult result = scanner.scan("com.nonexistent.package");

        assertNotNull(result, "扫描结果不应为空");
        assertTrue(result.getComponents().isEmpty(), "空包应该没有组件");
        assertTrue(result.getScannedClasses().isEmpty(), "空包应该没有扫描到类");

        System.out.println(result.getStatistics());
    }

    /**
     * 测试组件信息的完整性
     */
    @Test
    @DisplayName("测试组件信息完整性")
    public void testComponentInfoCompleteness() throws IOException {
        // 扫描resolvable.entity包
        ScanResult result = scanner.scan(
                "com.linsir.spring.framework.spring_core.type_system.resolvable.entity"
        );

        assertFalse(result.getComponents().isEmpty(), "应该扫描到组件");

        // 验证每个组件的信息完整性
        for (ComponentInfo info : result.getComponents()) {
            assertNotNull(info.getClassName(), "类名不应为空");
            assertNotNull(info.getAnnotationTypes(), "注解类型集合不应为空");
            assertNotNull(info.getComponentType(), "组件类型不应为空");

            System.out.println("组件: " + info.getClassName());
            System.out.println("  类型: " + info.getComponentType());
            System.out.println("  注解: " + info.getAnnotationTypes());
        }
    }
}
