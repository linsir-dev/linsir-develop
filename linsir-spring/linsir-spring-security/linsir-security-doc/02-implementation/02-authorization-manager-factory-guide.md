# AuthorizationManagerFactory 使用指南

## 概述

`AuthorizationManagerFactory` 是 Spring Security 7.0 引入的工厂接口，用于创建通用的 `AuthorizationManager` 实例。它提供了一种统一、便捷的方式来创建各种授权管理器，支持基于角色、权限、认证状态等多种授权场景。

---

## 核心接口

### AuthorizationManagerFactory

```java
public interface AuthorizationManagerFactory<T> {
    // 允许所有
    AuthorizationManager<T> permitAll();
    
    // 拒绝所有
    AuthorizationManager<T> denyAll();
    
    // 检查角色（自动添加 ROLE_ 前缀）
    AuthorizationManager<T> hasRole(String role);
    
    // 检查任一角色
    AuthorizationManager<T> hasAnyRole(String... roles);
    
    // 检查所有角色
    AuthorizationManager<T> hasAllRoles(String... roles);
    
    // 检查权限（不添加前缀）
    AuthorizationManager<T> hasAuthority(String authority);
    
    // 检查任一权限
    AuthorizationManager<T> hasAnyAuthority(String... authorities);
    
    // 检查所有权限
    AuthorizationManager<T> hasAllAuthorities(String... authorities);
    
    // 检查是否已认证（包括 remember-me）
    AuthorizationManager<T> authenticated();
    
    // 检查是否完全认证（不包括 remember-me）
    AuthorizationManager<T> fullyAuthenticated();
    
    // 检查是否是 remember-me 认证
    AuthorizationManager<T> rememberMe();
    
    // 检查是否是匿名
    AuthorizationManager<T> anonymous();
}
```

---

## 配置方式

### 1. 基础配置

```java
@Configuration
public class AuthorizationManagerFactoryConfig {

    @Bean
    public AuthorizationManagerFactory<Object> authorizationManagerFactory() {
        // 使用默认实现
        DefaultAuthorizationManagerFactory<Object> factory = 
            new DefaultAuthorizationManagerFactory<>();
        
        return factory;
    }
}
```

### 2. 自定义角色前缀

```java
@Configuration
public class AuthorizationManagerFactoryConfig {

    @Bean
    public AuthorizationManagerFactory<Object> authorizationManagerFactory() {
        DefaultAuthorizationManagerFactory<Object> factory = 
            new DefaultAuthorizationManagerFactory<>();
        
        // 自定义角色前缀（默认为 "ROLE_"）
        factory.setRolePrefix("MY_ROLE_");
        
        return factory;
    }
}
```

### 3. 配置角色层次结构

```java
@Configuration
public class AuthorizationManagerFactoryConfig {

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
            .role("ADMIN").implies("MANAGER")
            .role("MANAGER").implies("USER")
            .build();
    }

    @Bean
    public AuthorizationManagerFactory<Object> authorizationManagerFactory(
            RoleHierarchy roleHierarchy) {
        DefaultAuthorizationManagerFactory<Object> factory = 
            new DefaultAuthorizationManagerFactory<>();
        
        // 配置角色层次结构
        factory.setRoleHierarchy(roleHierarchy);
        
        return factory;
    }
}
```

### 4. 配置认证信任解析器

```java
@Configuration
public class AuthorizationManagerFactoryConfig {

    @Bean
    public AuthorizationManagerFactory<Object> authorizationManagerFactory() {
        DefaultAuthorizationManagerFactory<Object> factory = 
            new DefaultAuthorizationManagerFactory<>();
        
        // 配置认证信任解析器
        factory.setTrustResolver(new AuthenticationTrustResolverImpl());
        
        return factory;
    }
}
```

---

## 使用场景

### 场景 1：Web 请求授权

```java
@Configuration
public class SecurityConfig {

    @Autowired
    private AuthorizationManagerFactory<Object> factory;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            // 允许所有
            .requestMatchers("/public/**").manager(factory.permitAll())
            
            // 角色检查
            .requestMatchers("/admin/**").manager(factory.hasRole("ADMIN"))
            .requestMatchers("/manager/**").manager(factory.hasAnyRole("MANAGER", "ADMIN"))
            
            // 权限检查
            .requestMatchers("/api/user/create").manager(factory.hasAuthority("USER_CREATE"))
            .requestMatchers("/api/user/delete").manager(factory.hasAuthority("USER_DELETE"))
            
            // 认证状态检查
            .requestMatchers("/user/**").manager(factory.authenticated())
            .requestMatchers("/remember-me/**").manager(factory.rememberMe())
            .requestMatchers("/fully-protected/**").manager(factory.fullyAuthenticated())
            
            // 拒绝所有
            .requestMatchers("/forbidden/**").manager(factory.denyAll())
            
            // 其他所有请求需要认证
            .anyRequest().manager(factory.authenticated())
        );
        
        return http.build();
    }
}
```

### 场景 2：方法级别授权

```java
@Service
public class UserService {

    @Autowired
    private AuthorizationManagerFactory<Object> factory;

    /**
     * 使用拦截器进行方法授权
     */
    public void createUser(User user) {
        AuthorizationManager<Object> manager = factory.hasAuthority("USER_CREATE");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (!manager.authorize(() -> authentication, user).isGranted()) {
            throw new AccessDeniedException("没有创建用户的权限");
        }
        
        // 执行创建逻辑
        // ...
    }

    /**
     * 使用注解方式（需要 @EnableMethodSecurity）
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        // 删除用户逻辑
    }

    @PostAuthorize("returnObject.owner == authentication.name")
    public Document getDocument(Long id) {
        // 获取文档逻辑
        return new Document(id, "owner");
    }

    @PreFilter("filterObject.owner == authentication.name")
    public void saveDocuments(List<Document> documents) {
        // 保存文档列表
    }

    @PostFilter("filterObject.owner == authentication.name")
    public List<Document> findAllDocuments() {
        // 返回过滤后的文档列表
        return documents;
    }
}
```

### 场景 3：服务层权限检查

```java
@Service
public class AuthorizationService {

    private final AuthorizationManagerFactory<Object> factory;

    public AuthorizationService(AuthorizationManagerFactory<Object> factory) {
        this.factory = factory;
    }

    /**
     * 检查用户是否有指定角色
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthorizationManager<Object> manager = factory.hasRole(role);
        return manager.authorize(() -> authentication, new Object()).isGranted();
    }

    /**
     * 检查用户是否有任一指定角色
     */
    public boolean hasAnyRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthorizationManager<Object> manager = factory.hasAnyRole(roles);
        return manager.authorize(() -> authentication, new Object()).isGranted();
    }

    /**
     * 检查用户是否有指定权限
     */
    public boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthorizationManager<Object> manager = factory.hasAuthority(authority);
        return manager.authorize(() -> authentication, new Object()).isGranted();
    }

    /**
     * 检查用户是否已认证
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthorizationManager<Object> manager = factory.authenticated();
        return manager.authorize(() -> authentication, new Object()).isGranted();
    }

    /**
     * 检查用户是否完全认证
     */
    public boolean isFullyAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthorizationManager<Object> manager = factory.fullyAuthenticated();
        return manager.authorize(() -> authentication, new Object()).isGranted();
    }
}
```

### 场景 4：组合授权条件

```java
@Component
public class CompositeAuthorizationManager {

    private final AuthorizationManagerFactory<Object> factory;

    public CompositeAuthorizationManager(AuthorizationManagerFactory<Object> factory) {
        this.factory = factory;
    }

    /**
     * 组合条件：需要同时满足多个条件
     */
    public AuthorizationManager<Object> adminAndDeletePermission() {
        AuthorizationManager<Object> adminManager = factory.hasRole("ADMIN");
        AuthorizationManager<Object> deleteManager = factory.hasAuthority("USER_DELETE");
        
        // 使用 AuthorizationManagers 组合
        return AuthorizationManagers.allOf(adminManager, deleteManager);
    }

    /**
     * 组合条件：满足任一条件即可
     */
    public AuthorizationManager<Object> adminOrManager() {
        AuthorizationManager<Object> adminManager = factory.hasRole("ADMIN");
        AuthorizationManager<Object> managerManager = factory.hasRole("MANAGER");
        
        return AuthorizationManagers.anyOf(adminManager, managerManager);
    }

    /**
     * 组合条件：所有条件都不满足时才允许
     */
    public AuthorizationManager<Object> notBanned() {
        AuthorizationManager<Object> bannedManager = factory.hasRole("BANNED");
        
        return AuthorizationManagers.noneOf(bannedManager);
    }
}
```

---

## 实战示例

### 示例 1：RESTful API 权限控制

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取用户列表 - 需要 USER 角色
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    /**
     * 创建用户 - 需要 USER_CREATE 权限
     */
    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 删除用户 - 需要 ADMIN 角色和 USER_DELETE 权限
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') and hasAuthority('USER_DELETE')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 更新用户 - 需要是用户本人或管理员
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查是否是本人或管理员
        boolean isOwner = existingUser.getUsername().equals(currentUsername);
        boolean isAdmin = authorizationService.hasRole("ADMIN");
        
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("无权修改此用户");
        }
        
        User updated = userRepository.save(user);
        return ResponseEntity.ok(updated);
    }
}
```

### 示例 2：数据级权限控制

```java
@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private AuthorizationService authorizationService;

    /**
     * 获取文档 - 需要是文档所有者或管理员
     */
    @PreAuthorize("@documentService.isOwner(#id) or hasRole('ADMIN')")
    public Document getDocument(Long id) {
        return documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("文档不存在"));
    }

    /**
     * 判断是否是文档所有者
     */
    public boolean isOwner(Long documentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        Document document = documentRepository.findById(documentId).orElse(null);
        return document != null && document.getOwner().equals(currentUsername);
    }

    /**
     * 删除文档 - 需要是所有者且有 DELETE 权限
     */
    @PreAuthorize("@documentService.isOwner(#id) and hasAuthority('DOCUMENT_DELETE')")
    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }
}
```

### 示例 3：动态权限检查

```java
@Component
public class DynamicPermissionChecker {

    @Autowired
    private AuthorizationManagerFactory<Object> factory;

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * 动态检查权限
     */
    public boolean checkPermission(String resourceType, Long resourceId, String operation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 1. 检查是否是管理员
        AuthorizationManager<Object> adminManager = factory.hasRole("ADMIN");
        if (adminManager.authorize(() -> authentication, new Object()).isGranted()) {
            return true;
        }
        
        // 2. 检查数据库中的动态权限
        String username = authentication.getName();
        boolean hasPermission = permissionRepository.hasPermission(
            username, resourceType, resourceId, operation
        );
        
        return hasPermission;
    }
}
```

---

## 最佳实践

### 1. 统一使用工厂创建管理器

```java
// ✅ 推荐：使用工厂
@Autowired
private AuthorizationManagerFactory<Object> factory;

AuthorizationManager<Object> manager = factory.hasRole("ADMIN");

// ❌ 不推荐：手动创建
AuthorizationManager<Object> manager = AuthorityAuthorizationManager.hasRole("ADMIN");
```

### 2. 使用角色层次结构简化配置

```java
@Bean
public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.withDefaultRolePrefix()
        .role("ADMIN").implies("MANAGER")
        .role("MANAGER").implies("USER")
        .build();
}
```

### 3. 组合使用多个管理器

```java
// 多重保护
AuthorizationManager<Object> manager = AuthorizationManagers.allOf(
    factory.hasRole("ADMIN"),
    factory.hasAuthority("USER_DELETE"),
    factory.fullyAuthenticated()
);
```

### 4. 使用注解简化代码

```java
// 使用 @PreAuthorize 注解
@PreAuthorize("hasRole('ADMIN') and hasAuthority('USER_DELETE')")
public void deleteUser(Long userId) {
    // ...
}

// 比手动检查更简洁
```

### 5. 缓存权限检查结果

```java
@Service
public class CachedAuthorizationService {

    @Cacheable(value = "authorizations", key = "#username + ':' + #permission")
    public boolean hasPermission(String username, String permission) {
        // 执行权限检查
        // ...
    }
}
```

---

## 测试验证

### 单元测试

```java
@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationManagerFactoryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorizationManagerFactory<Object> factory;

    @Test
    public void testHasRole() throws Exception {
        // 测试角色检查
        mockMvc.perform(get("/api/authorization/check-role?role=ADMIN")
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hasRole").value(true));
    }

    @Test
    public void testAdminOnlyEndpoint() throws Exception {
        // 测试管理员专属接口
        mockMvc.perform(get("/api/authorization/admin-only")
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk());

        // 普通用户访问应被拒绝
        mockMvc.perform(get("/api/authorization/admin-only")
                .with(user("user").roles("USER")))
            .andExpect(status().isForbidden());
    }
}
```

### 集成测试

使用提供的 PowerShell 测试脚本：

```powershell
# 运行测试脚本
cd "d:\dev\2026\1.3 code\develop\linsir-develop\linsir-spring\linsir-spring-security\linsir-spring-security-server"
.\test-authorization-manager-factory.ps1
```

---

## 常见问题

### Q1: `hasRole()` 和 `hasAuthority()` 的区别？

```java
// hasRole("ADMIN") 
// 会自动添加 ROLE_ 前缀，实际检查 "ROLE_ADMIN"

// hasAuthority("ADMIN")
// 直接检查 "ADMIN"，不添加前缀

// 推荐：
// - 使用 roles() 配置角色，配合 hasRole() 检查
// - 使用 authorities() 配置权限，配合 hasAuthority() 检查
```

### Q2: 如何自定义角色前缀？

```java
@Bean
public AuthorizationManagerFactory<Object> authorizationManagerFactory() {
    DefaultAuthorizationManagerFactory<Object> factory = 
        new DefaultAuthorizationManagerFactory<>();
    factory.setRolePrefix("MY_PREFIX_");
    return factory;
}
```

### Q3: 如何实现动态权限？

```java
@Service
public class DynamicPermissionService {
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    public boolean hasPermission(String username, String resource, String operation) {
        // 查询数据库中的动态权限配置
        return permissionRepository.hasPermission(username, resource, operation);
    }
}

// 使用
@PreAuthorize("@dynamicPermissionService.hasPermission(authentication.name, 'document', 'read')")
public Document getDocument(Long id) {
    // ...
}
```

### Q4: 如何组合多个条件？

```java
// 使用 AuthorizationManagers 工具类
AuthorizationManager<Object> manager = AuthorizationManagers.allOf(
    factory.hasRole("ADMIN"),
    factory.hasAuthority("USER_DELETE")
);

// 或使用 SpEL 表达式
@PreAuthorize("hasRole('ADMIN') and hasAuthority('USER_DELETE')")
public void deleteUser(Long id) {
    // ...
}
```

---

## 参考资料

- [Spring Security 官方文档 - AuthorizationManagerFactory](https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html#authorization-manager-factory)
- [Spring Security 官方文档 - Method Security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html)
- [Spring Security 官方文档 - Web Security](https://docs.spring.io/spring-security/reference/servlet/authorization/web.html)

---

**版本信息：**
- Spring Security 7.0+
- 最后更新：2026-04-05
