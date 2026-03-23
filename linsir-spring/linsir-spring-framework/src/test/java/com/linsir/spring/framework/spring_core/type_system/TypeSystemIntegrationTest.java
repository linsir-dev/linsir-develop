package com.linsir.spring.framework.spring_core.type_system;

import com.linsir.spring.framework.spring_core.type_system.component.ComponentInfo;
import com.linsir.spring.framework.spring_core.type_system.component.ComponentScanner;
import com.linsir.spring.framework.spring_core.type_system.component.ComponentType;
import com.linsir.spring.framework.spring_core.type_system.component.ScanResult;
import com.linsir.spring.framework.spring_core.type_system.filter.AnnotationTypeFilter;
import com.linsir.spring.framework.spring_core.type_system.filter.AssignableTypeFilter;
import com.linsir.spring.framework.spring_core.type_system.metadata.scanner.ClassMetadataInfo;
import com.linsir.spring.framework.spring_core.type_system.metadata.scanner.ClassMetadataScanner;
import com.linsir.spring.framework.spring_core.type_system.resolvable.container.DataHolder;
import com.linsir.spring.framework.spring_core.type_system.resolvable.entity.User;
import com.linsir.spring.framework.spring_core.type_system.resolvable.processor.TypeResolver;
import com.linsir.spring.framework.spring_core.type_system.resolvable.service.BaseService;
import com.linsir.spring.framework.spring_core.type_system.resolvable.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 类型系统集成测试类
 * 测试类型系统各组件的协同工作
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
@DisplayName("类型系统集成测试")
public class TypeSystemIntegrationTest {

    private ClassMetadataScanner metadataScanner;
    private ComponentScanner componentScanner;

    @BeforeEach
    public void setUp() {
        metadataScanner = new ClassMetadataScanner();
        componentScanner = new ComponentScanner();
    }

    /**
     * 集成测试：从类扫描到泛型解析的完整流程
     * 1. 扫描包获取类元数据
     * 2. 解析类的泛型参数
     * 3. 验证解析结果
     */
    @Test
    @DisplayName("测试从扫描到泛型解析的完整流程")
    public void testScanToGenericResolution() throws IOException, ClassNotFoundException {
        // 1. 扫描service包
        List<ClassMetadataInfo> metadataList = metadataScanner.scanPackage(
                "com.linsir.spring.framework.spring_core.type_system.resolvable.service"
        );

        assertFalse(metadataList.isEmpty(), "应该扫描到类");

        // 2. 找到UserService类
        ClassMetadataInfo userServiceMetadata = metadataList.stream()
                .filter(info -> info.getClassName().endsWith("UserService"))
                .findFirst()
                .orElse(null);

        assertNotNull(userServiceMetadata, "应该找到UserService");

        // 3. 加载类并解析泛型
        Class<?> userServiceClass = Class.forName(userServiceMetadata.getClassName());
        Class<?>[] generics = TypeResolver.resolveClassGenerics(userServiceClass, BaseService.class);

        // 4. 验证泛型解析结果
        assertEquals(2, generics.length, "应该有2个泛型参数");
        assertEquals(User.class, generics[0], "第一个泛型应该是User");
        assertEquals(Long.class, generics[1], "第二个泛型应该是Long");

        System.out.println("类: " + userServiceMetadata.getClassName());
        System.out.println("泛型参数: " + generics[0].getSimpleName() + ", " + generics[1].getSimpleName());
    }

    /**
     * 集成测试：类型过滤 + 元数据读取 + 泛型解析
     */
    @Test
    @DisplayName("测试类型过滤与泛型解析集成")
    public void testFilterWithGenericResolution() throws IOException, ClassNotFoundException {
        // 1. 添加过滤器：只扫描实现了BaseService的类
        componentScanner.addIncludeFilter(new AssignableTypeFilter(BaseService.class));

        // 2. 扫描resolvable包
        ScanResult result = componentScanner.scan(
                "com.linsir.spring.framework.spring_core.type_system.resolvable"
        );

        assertFalse(result.getComponents().isEmpty(), "应该扫描到组件");

        // 3. 对每个扫描到的组件解析泛型
        for (ComponentInfo info : result.getComponents()) {
            String className = info.getClassName();

            // 跳过接口
            if (className.endsWith("BaseService")) {
                continue;
            }

            // 加载类
            Class<?> clazz = Class.forName(className);

            // 解析泛型
            ResolvableType resolvableType = ResolvableType.forClass(clazz).as(BaseService.class);
            if (resolvableType != ResolvableType.NONE) {
                ResolvableType[] generics = resolvableType.getGenerics();
                System.out.println("类: " + className);
                for (int i = 0; i < generics.length; i++) {
                    System.out.println("  泛型[" + i + "]: " + generics[i].resolve());
                }
            }
        }
    }

    /**
     * 集成测试：复杂泛型字段的完整解析
     */
    @Test
    @DisplayName("测试复杂泛型字段解析")
    public void testComplexGenericFieldResolution() throws NoSuchFieldException {
        // 1. 获取DataHolder类
        Class<DataHolder> clazz = DataHolder.class;

        // 2. 解析各种泛型字段
        Field stringListField = clazz.getDeclaredField("stringList");
        Field nestedListField = clazz.getDeclaredField("nestedList");
        Field complexMapField = clazz.getDeclaredField("complexMap");

        // 3. 使用ResolvableType解析
        ResolvableType stringListType = ResolvableType.forField(stringListField);
        ResolvableType nestedListType = ResolvableType.forField(nestedListField);
        ResolvableType complexMapType = ResolvableType.forField(complexMapField);

        // 4. 验证解析结果
        // stringList: List<String>
        assertEquals(String.class, stringListType.getGeneric(0).resolve());

        // nestedList: List<List<String>>
        assertTrue(nestedListType.getGeneric(0).resolve().isAssignableFrom(List.class));
        assertEquals(String.class, nestedListType.getGeneric(0).getGeneric(0).resolve());

        // complexMap: Map<String, List<Integer>>
        assertEquals(String.class, complexMapType.getGeneric(0).resolve());
        assertTrue(complexMapType.getGeneric(1).resolve().isAssignableFrom(List.class));
        assertEquals(Integer.class, complexMapType.getGeneric(1).getGeneric(0).resolve());

        System.out.println("stringList泛型: " + stringListType.getGeneric(0).resolve());
        System.out.println("nestedList嵌套泛型: " + nestedListType.getGeneric(0).getGeneric(0).resolve());
        System.out.println("complexMap Key: " + complexMapType.getGeneric(0).resolve());
        System.out.println("complexMap Value: " + complexMapType.getGeneric(1).resolve());
        System.out.println("complexMap Value泛型: " + complexMapType.getGeneric(1).getGeneric(0).resolve());
    }

    /**
     * 集成测试：元数据 + 过滤 + 组件类型判断
     */
    @Test
    @DisplayName("测试元数据与组件类型判断集成")
    public void testMetadataWithComponentType() throws IOException {
        // 1. 扫描metadata.model包
        List<ClassMetadataInfo> metadataList = metadataScanner.scanPackage(
                "com.linsir.spring.framework.spring_core.type_system.metadata.model"
        );

        assertFalse(metadataList.isEmpty(), "应该扫描到类");

        // 2. 分析每个类的元数据
        for (ClassMetadataInfo info : metadataList) {
            System.out.println("类: " + info.getClassName());
            System.out.println("  是注解: " + info.isAnnotation());
            System.out.println("  是接口: " + info.isInterface());
            System.out.println("  注解类型: " + info.getAnnotationTypes());

            // 判断组件类型
            if (info.getAnnotationTypes().contains(
                    "com.linsir.spring.framework.spring_core.type_system.metadata.model.Service")) {
                System.out.println("  组件类型: SERVICE");
            } else if (info.getAnnotationTypes().contains(
                    "com.linsir.spring.framework.spring_core.type_system.metadata.model.Repository")) {
                System.out.println("  组件类型: REPOSITORY");
            } else if (info.getAnnotationTypes().contains(
                    "com.linsir.spring.framework.spring_core.type_system.metadata.model.Component")) {
                System.out.println("  组件类型: COMPONENT");
            }
        }
    }

    /**
     * 集成测试：完整的组件扫描流程
     */
    @Test
    @DisplayName("测试完整组件扫描流程")
    public void testCompleteComponentScan() throws IOException {
        // 1. 配置扫描器
        // 包含：所有类
        // 排除：测试类
        componentScanner.addExcludeFilter(
                new com.linsir.spring.framework.spring_core.type_system.filter.RegexPatternTypeFilter(".*Test.*")
        );

        // 2. 扫描整个type_system包
        ScanResult result = componentScanner.scan(
                "com.linsir.spring.framework.spring_core.type_system"
        );

        assertNotNull(result, "扫描结果不应为空");
        assertFalse(result.getComponents().isEmpty(), "应该扫描到组件");

        // 3. 统计各类组件
        int entityCount = 0;
        int serviceCount = 0;
        int filterCount = 0;
        int otherCount = 0;

        for (ComponentInfo info : result.getComponents()) {
            String className = info.getClassName();

            if (className.contains("entity")) {
                entityCount++;
            } else if (className.contains("service")) {
                serviceCount++;
            } else if (className.contains("filter")) {
                filterCount++;
            } else {
                otherCount++;
            }
        }

        System.out.println("扫描统计:");
        System.out.println("  实体类: " + entityCount);
        System.out.println("  服务类: " + serviceCount);
        System.out.println("  过滤器: " + filterCount);
        System.out.println("  其他: " + otherCount);
        System.out.println("  总计: " + result.getComponents().size());

        assertTrue(entityCount >= 2, "应该至少扫描到2个实体类");
        assertTrue(serviceCount >= 3, "应该至少扫描到3个服务类");
        assertTrue(filterCount >= 4, "应该至少扫描到4个过滤器");
    }

    /**
     * 集成测试：ResolvableType与传统反射对比
     */
    @Test
    @DisplayName("测试ResolvableType与传统反射对比")
    public void testResolvableTypeVsTraditionalReflection() {
        // 1. 使用ResolvableType解析
        long start1 = System.nanoTime();
        ResolvableType resolvableType = ResolvableType.forClass(UserService.class).as(BaseService.class);
        Class<?> generic1 = resolvableType.getGeneric(0).resolve();
        long end1 = System.nanoTime();

        // 2. 使用传统反射解析
        long start2 = System.nanoTime();
        java.lang.reflect.Type[] generics = TypeResolver.resolveGenericsTraditionally(
                UserService.class, BaseService.class
        );
        long end2 = System.nanoTime();

        // 3. 验证结果一致
        assertEquals(generic1, generics[0], "两种方法解析结果应该一致");

        // 4. 输出性能对比
        System.out.println("ResolvableType耗时: " + (end1 - start1) + " ns");
        System.out.println("传统反射耗时: " + (end2 - start2) + " ns");
        System.out.println("解析结果: " + generic1.getSimpleName());
    }

    /**
     * 集成测试：类型系统的核心功能验证
     */
    @Test
    @DisplayName("测试类型系统核心功能")
    public void testTypeSystemCoreFeatures() {
        // 1. 测试ResolvableType
        ResolvableType userServiceType = ResolvableType.forClass(UserService.class);
        assertNotNull(userServiceType, "ResolvableType不应为空");

        // 2. 测试类型转换
        ResolvableType baseServiceType = userServiceType.as(BaseService.class);
        assertNotNull(baseServiceType, "应该能转换为BaseService类型");
        assertNotSame(ResolvableType.NONE, baseServiceType, "转换结果不应为NONE");

        // 3. 测试泛型解析
        ResolvableType[] generics = baseServiceType.getGenerics();
        assertEquals(2, generics.length, "应该有2个泛型参数");
        assertEquals(User.class, generics[0].resolve());
        assertEquals(Long.class, generics[1].resolve());

        // 4. 测试类型检查
        assertTrue(baseServiceType.resolve().isAssignableFrom(BaseService.class));
        assertFalse(baseServiceType.isArray());

        System.out.println("类型系统核心功能测试通过!");
        System.out.println("UserService泛型参数: " + generics[0].resolve().getSimpleName() + ", " +
                generics[1].resolve().getSimpleName());
    }
}
