package com.linsir.abc.core.base.lang.object;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DeepCloneable测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class DeepCloneableTest {

    private Department department;
    private Employee employee1;
    private Employee employee2;
    private Address address1;
    private Address address2;

    @BeforeEach
    public void setUp() {
        // 创建测试数据
        address1 = new Address("广东省", "深圳市", "南山区");
        address2 = new Address("北京市", "北京市", "海淀区");

        employee1 = new Employee(1L, "张三", 10000.0, address1);
        employee2 = new Employee(2L, "李四", 15000.0, address2);

        department = new Department();
        department.setId(1L);
        department.setName("技术部");
        department.addEmployee(employee1);
        department.addEmployee(employee2);
    }

    @Test
    public void testDeepClone() throws CloneNotSupportedException {
        // 测试深拷贝
        Department cloned = department.deepClone();

        // 验证基本字段相等
        assertEquals(department.getId(), cloned.getId());
        assertEquals(department.getName(), cloned.getName());
        assertEquals(department.getEmployees().size(), cloned.getEmployees().size());

        // 验证深拷贝：修改原对象不影响克隆对象
        department.setName("市场部");
        assertEquals("技术部", cloned.getName());

        // 验证员工列表是深拷贝
        assertNotSame(department.getEmployees(), cloned.getEmployees());

        // 验证员工对象是深拷贝
        Employee originalEmp = department.getEmployees().get(0);
        Employee clonedEmp = cloned.getEmployees().get(0);
        assertNotSame(originalEmp, clonedEmp);
        assertEquals(originalEmp.getId(), clonedEmp.getId());
        assertEquals(originalEmp.getName(), clonedEmp.getName());
    }

    @Test
    public void testDeepCloneBySerialization() throws IOException, ClassNotFoundException {
        // 测试序列化深拷贝
        Department cloned = department.deepCloneBySerialization();

        // 验证基本字段相等
        assertEquals(department.getId(), cloned.getId());
        assertEquals(department.getName(), cloned.getName());
        assertEquals(department.getEmployees().size(), cloned.getEmployees().size());

        // 验证深拷贝：修改原对象不影响克隆对象
        department.setName("财务部");
        assertEquals("技术部", cloned.getName());

        // 验证员工列表是深拷贝
        assertNotSame(department.getEmployees(), cloned.getEmployees());
    }

    @Test
    public void testCopyConstructor() {
        // 测试拷贝构造器
        Department cloned = new Department(department);

        // 验证基本字段相等
        assertEquals(department.getId(), cloned.getId());
        assertEquals(department.getName(), cloned.getName());
        assertEquals(department.getEmployees().size(), cloned.getEmployees().size());

        // 验证深拷贝
        department.setName("人事部");
        assertEquals("技术部", cloned.getName());
    }

    @Test
    public void testEmployeeDeepClone() throws CloneNotSupportedException {
        // 测试员工深拷贝
        Employee cloned = employee1.deepClone();

        // 验证基本字段相等
        assertEquals(employee1.getId(), cloned.getId());
        assertEquals(employee1.getName(), cloned.getName());
        assertEquals(employee1.getSalary(), cloned.getSalary());

        // 验证地址是深拷贝
        assertNotSame(employee1.getAddress(), cloned.getAddress());
        assertEquals(employee1.getAddress().getCity(), cloned.getAddress().getCity());

        // 修改原对象地址不影响克隆对象
        employee1.getAddress().setCity("广州市");
        assertEquals("深圳市", cloned.getAddress().getCity());
    }

    @Test
    public void testEmployeeCopyConstructor() {
        // 测试员工拷贝构造器
        Employee cloned = new Employee(employee1);

        // 验证基本字段相等
        assertEquals(employee1.getId(), cloned.getId());
        assertEquals(employee1.getName(), cloned.getName());
        assertEquals(employee1.getSalary(), cloned.getSalary());

        // 验证地址是深拷贝
        assertNotSame(employee1.getAddress(), cloned.getAddress());
    }

    @Test
    public void testAddressDeepClone() throws CloneNotSupportedException {
        // 测试地址深拷贝
        Address cloned = address1.deepClone();

        // 验证字段相等
        assertEquals(address1.getProvince(), cloned.getProvince());
        assertEquals(address1.getCity(), cloned.getCity());
        assertEquals(address1.getStreet(), cloned.getStreet());

        // 验证是不同的对象
        assertNotSame(address1, cloned);
    }

    @Test
    public void testShallowCopyVsDeepCopy() throws CloneNotSupportedException {
        // 测试浅拷贝和深拷贝的区别
        Department original = department;
        Department cloned = department.deepClone();

        // 获取原始部门和克隆部门的第一个员工
        Employee originalEmp = original.getEmployees().get(0);
        Employee clonedEmp = cloned.getEmployees().get(0);

        // 验证员工对象是不同的实例
        assertNotSame(originalEmp, clonedEmp);

        // 修改原始员工的姓名
        originalEmp.setName("王五");

        // 验证克隆员工的姓名没有改变（深拷贝）
        assertEquals("张三", clonedEmp.getName());
    }

    @Test
    public void testNestedObjectDeepCopy() throws CloneNotSupportedException {
        // 测试嵌套对象的深拷贝
        Employee cloned = employee1.deepClone();

        // 验证地址对象是深拷贝
        assertNotSame(employee1.getAddress(), cloned.getAddress());

        // 修改原始地址
        employee1.getAddress().setStreet("福田区");

        // 验证克隆地址没有改变
        assertEquals("南山区", cloned.getAddress().getStreet());
    }

    @Test
    public void testDeepCloneIndependence() throws CloneNotSupportedException {
        // 测试深拷贝的独立性
        Department cloned = department.deepClone();

        // 修改原始部门的所有字段
        department.setId(999L);
        department.setName("新部门");
        department.getEmployees().clear();

        // 验证克隆部门不受影响
        assertEquals(1L, cloned.getId());
        assertEquals("技术部", cloned.getName());
        assertEquals(2, cloned.getEmployees().size());
    }

    @Test
    public void testSerializationWithNullAddress() throws IOException, ClassNotFoundException {
        // 测试序列化深拷贝时地址为null的情况
        Employee empWithoutAddress = new Employee(3L, "王五", 20000.0, null);
        department.addEmployee(empWithoutAddress);

        Department cloned = department.deepCloneBySerialization();

        // 验证克隆成功
        assertEquals(3, cloned.getEmployees().size());

        // 验证地址为null的员工也被正确克隆
        Employee clonedEmp = cloned.getEmployees().get(2);
        assertNull(clonedEmp.getAddress());
    }

    @Test
    public void testToString() {
        // 测试toString方法
        String deptStr = department.toString();
        assertTrue(deptStr.contains("技术部"));
        assertTrue(deptStr.contains("2"));

        String empStr = employee1.toString();
        assertTrue(empStr.contains("张三"));

        String addrStr = address1.toString();
        assertTrue(addrStr.contains("广东省"));
        assertTrue(addrStr.contains("深圳市"));
        assertTrue(addrStr.contains("南山区"));
    }

    @Test
    public void testEmptyDepartmentClone() throws CloneNotSupportedException {
        // 测试空部门的深拷贝
        Department emptyDept = new Department();
        emptyDept.setId(100L);
        emptyDept.setName("空部门");

        Department cloned = emptyDept.deepClone();

        assertEquals(emptyDept.getId(), cloned.getId());
        assertEquals(emptyDept.getName(), cloned.getName());
        assertTrue(cloned.getEmployees().isEmpty());
    }

    @Test
    public void testMultipleLevelDeepCopy() throws CloneNotSupportedException {
        // 测试多级深拷贝
        // 创建带有上级部门的部门
        Department parentDept = new Department();
        parentDept.setId(10L);
        parentDept.setName("总公司");

        department.setParentDepartment(parentDept);

        Department cloned = department.deepClone();

        // 验证部门被正确克隆
        assertEquals(department.getId(), cloned.getId());
        assertEquals(2, cloned.getEmployees().size());

        // 注意：上级部门是引用复制，不是深拷贝（避免循环引用）
        assertSame(department.getParentDepartment(), cloned.getParentDepartment());
    }
}
