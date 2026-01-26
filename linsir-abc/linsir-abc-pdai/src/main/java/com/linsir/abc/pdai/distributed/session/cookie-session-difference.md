# Cookie和Session有什么区别？

## Cookie和Session简介

Cookie和Session是Web开发中用于保持用户状态的两种重要机制。HTTP协议本身是无状态的，Cookie和Session的出现解决了这个问题，使得Web应用能够记住用户的状态。

## Cookie详解

### 1. Cookie的定义

Cookie是服务器发送到用户浏览器并保存在浏览器上的一小块数据，浏览器会在下次请求时将Cookie数据发送回服务器。

### 2. Cookie的工作原理

```
1. 客户端发起请求
   ↓
2. 服务器创建Cookie
   ↓
3. 服务器在响应头中设置Set-Cookie
   ↓
4. 浏览器保存Cookie
   ↓
5. 客户端再次发起请求（携带Cookie）
   ↓
6. 服务器读取Cookie
```

### 3. Cookie的属性

```java
// Cookie示例
Cookie cookie = new Cookie("username", "zhangsan");
cookie.setMaxAge(60 * 60); // 设置有效期为1小时
cookie.setPath("/"); // 设置路径
cookie.setDomain(".example.com"); // 设置域名
cookie.setHttpOnly(true); // 设置HttpOnly
cookie.setSecure(true); // 设置Secure
response.addCookie(cookie);
```

#### 3.1 Name和Value

- **Name**：Cookie的名称
- **Value**：Cookie的值

#### 3.2 Max-Age

- **定义**：Cookie的有效期，单位为秒
- **默认值**：-1，表示Cookie在浏览器关闭时失效
- **示例**：`setMaxAge(60 * 60)`表示Cookie有效期为1小时

#### 3.3 Path

- **定义**：Cookie的路径，只有访问该路径及其子路径时才会发送Cookie
- **默认值**：当前请求的路径
- **示例**：`setPath("/")`表示所有路径都会发送Cookie

#### 3.4 Domain

- **定义**：Cookie的域名，只有访问该域名及其子域名时才会发送Cookie
- **默认值**：当前请求的域名
- **示例**：`setDomain(".example.com")`表示example.com及其子域名都会发送Cookie

#### 3.5 HttpOnly

- **定义**：是否只通过HTTP协议传输，禁止JavaScript访问
- **默认值**：false
- **作用**：防止XSS攻击窃取Cookie

#### 3.6 Secure

- **定义**：是否只通过HTTPS协议传输
- **默认值**：false
- **作用**：防止Cookie在HTTP传输中被窃取

### 4. Cookie的优缺点

#### 4.1 优点

- **简单易用**：使用简单，无需额外配置
- **跨请求共享**：可以在多个请求之间共享数据
- **持久化**：可以设置有效期，实现数据持久化

#### 4.2 缺点

- **大小限制**：每个Cookie的大小限制为4KB
- **数量限制**：每个域名下的Cookie数量限制为20-50个
- **安全性低**：容易被窃取和篡改
- **性能影响**：每次请求都会携带Cookie，增加网络传输量

## Session详解

### 1. Session的定义

Session是服务器端保存用户状态的一种机制，每个用户在服务器端都有一个对应的Session对象。

### 2. Session的工作原理

```
1. 客户端发起请求
   ↓
2. 服务器创建Session
   ↓
3. 服务器生成Session ID
   ↓
4. 服务器将Session ID通过Cookie发送给客户端
   ↓
5. 浏览器保存Session ID
   ↓
6. 客户端再次发起请求（携带Session ID）
   ↓
7. 服务器根据Session ID查找Session
   ↓
8. 服务器返回Session中的数据
```

### 3. Session的属性

```java
// Session示例
HttpSession session = request.getSession();
session.setAttribute("username", "zhangsan");
session.setMaxInactiveInterval(60 * 60); // 设置有效期为1小时
session.invalidate(); // 销毁Session
```

#### 3.1 Session ID

- **定义**：Session的唯一标识符
- **作用**：用于在服务器端查找对应的Session对象
- **生成方式**：通常使用UUID或随机数生成

#### 3.2 MaxInactiveInterval

- **定义**：Session的最大不活动时间，单位为秒
- **默认值**：通常为30分钟
- **作用**：超过该时间未访问，Session将失效

#### 3.3 Attributes

- **定义**：Session中存储的数据
- **作用**：用于在多个请求之间共享数据
- **示例**：`setAttribute("username", "zhangsan")`

### 4. Session的优缺点

#### 4.1 优点

- **安全性高**：数据存储在服务器端，不易被窃取
- **大小无限制**：可以存储大量数据
- **类型丰富**：可以存储任意类型的对象

#### 4.2 缺点

- **服务器压力大**：Session数据存储在服务器端，增加服务器压力
- **分布式困难**：在分布式环境下，Session共享比较困难
- **依赖Cookie**：通常需要通过Cookie传递Session ID

## Cookie和Session的区别

### 1. 存储位置

| 特性 | Cookie | Session |
|------|--------|---------|
| 存储位置 | 客户端（浏览器） | 服务器端 |
| 数据大小 | 限制为4KB | 无限制 |
| 数据类型 | 只能存储字符串 | 可以存储任意类型的对象 |

### 2. 安全性

| 特性 | Cookie | Session |
|------|--------|---------|
| 安全性 | 低，容易被窃取和篡改 | 高，数据存储在服务器端 |
| 防XSS攻击 | 需要设置HttpOnly | 天然防XSS攻击 |
| 防CSRF攻击 | 需要设置SameSite | 需要设置SameSite |

### 3. 性能

| 特性 | Cookie | Session |
|------|--------|---------|
| 网络传输 | 每次请求都会携带Cookie | 只携带Session ID |
| 服务器压力 | 无服务器压力 | 有服务器压力 |
| 存储容量 | 限制为4KB | 无限制 |

### 4. 生命周期

| 特性 | Cookie | Session |
|------|--------|---------|
| 有效期 | 可以设置有效期 | 可以设置有效期 |
| 失效方式 | 浏览器关闭或过期 | 超时或服务器重启 |
| 持久化 | 可以持久化 | 不能持久化 |

### 5. 适用场景

| 特性 | Cookie | Session |
|------|--------|---------|
| 适用场景 | 存储少量数据、需要持久化 | 存储大量数据、需要安全性 |
| 典型应用 | 记住用户名、个性化设置 | 用户登录状态、购物车 |

## Cookie和Session的使用示例

### 1. Cookie使用示例

```java
@WebServlet("/cookie")
public class CookieServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 设置Cookie
        Cookie cookie = new Cookie("username", "zhangsan");
        cookie.setMaxAge(60 * 60); // 1小时
        cookie.setPath("/");
        response.addCookie(cookie);
        
        // 读取Cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("username".equals(c.getName())) {
                    System.out.println("Cookie值：" + c.getValue());
                }
            }
        }
        
        response.getWriter().write("Cookie操作成功");
    }
}
```

### 2. Session使用示例

```java
@WebServlet("/session")
public class SessionServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 获取Session
        HttpSession session = request.getSession();
        
        // 设置Session属性
        session.setAttribute("username", "zhangsan");
        session.setAttribute("loginTime", new Date());
        
        // 读取Session属性
        String username = (String) session.getAttribute("username");
        Date loginTime = (Date) session.getAttribute("loginTime");
        
        System.out.println("用户名：" + username);
        System.out.println("登录时间：" + loginTime);
        
        // 移除Session属性
        session.removeAttribute("loginTime");
        
        // 销毁Session
        // session.invalidate();
        
        response.getWriter().write("Session操作成功");
    }
}
```

### 3. Cookie和Session结合使用示例

```java
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // 验证用户名和密码
        if ("admin".equals(username) && "123456".equals(password)) {
            // 登录成功，创建Session
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setAttribute("loginTime", new Date());
            
            // 设置Cookie记住用户名
            Cookie cookie = new Cookie("rememberUsername", username);
            cookie.setMaxAge(60 * 60 * 24 * 7); // 7天
            cookie.setPath("/");
            response.addCookie(cookie);
            
            response.getWriter().write("登录成功");
        } else {
            response.getWriter().write("登录失败");
        }
    }
}
```

## Cookie和Session的最佳实践

### 1. Cookie最佳实践

#### 1.1 设置HttpOnly

```java
Cookie cookie = new Cookie("sessionId", "abc123");
cookie.setHttpOnly(true); // 防止JavaScript访问
response.addCookie(cookie);
```

#### 1.2 设置Secure

```java
Cookie cookie = new Cookie("sessionId", "abc123");
cookie.setSecure(true); // 只通过HTTPS传输
response.addCookie(cookie);
```

#### 1.3 设置SameSite

```java
Cookie cookie = new Cookie("sessionId", "abc123");
cookie.setHttpOnly(true);
response.setHeader("Set-Cookie", 
    cookie.getName() + "=" + cookie.getValue() + 
    "; Path=/; HttpOnly; Secure; SameSite=Strict");
```

#### 1.4 敏感数据加密

```java
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CookieUtil {
    
    private static final String KEY = "1234567890123456";
    
    public static String encrypt(String data) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    public static String decrypt(String encrypted) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return new String(decrypted);
    }
}
```

### 2. Session最佳实践

#### 2.1 设置Session超时时间

```java
// 在web.xml中配置
<session-config>
    <session-timeout>30</session-timeout>
</session-config>

// 或者在代码中设置
session.setMaxInactiveInterval(60 * 30); // 30分钟
```

#### 2.2 及时销毁Session

```java
// 用户登出时销毁Session
public void logout(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
        session.invalidate();
    }
}
```

#### 2.3 避免在Session中存储大量数据

```java
// 不推荐：在Session中存储大量数据
session.setAttribute("largeData", largeDataList);

// 推荐：只存储必要的少量数据
session.setAttribute("userId", userId);
```

#### 2.4 使用Session监听器

```java
@WebListener
public class SessionListener implements HttpSessionListener {
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("Session创建：" + se.getSession().getId());
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("Session销毁：" + se.getSession().getId());
    }
}
```

## 总结

Cookie和Session是Web开发中用于保持用户状态的两种重要机制，它们各有优缺点：

- **Cookie**：存储在客户端，简单易用，但安全性低，适合存储少量非敏感数据
- **Session**：存储在服务器端，安全性高，但服务器压力大，适合存储大量敏感数据

在实际开发中，通常将Cookie和Session结合使用，通过Cookie传递Session ID，在服务器端通过Session ID查找对应的Session对象，实现用户状态的保持。

选择使用Cookie还是Session，需要根据具体的业务场景来决定：
- 存储少量非敏感数据：选择Cookie
- 存储大量敏感数据：选择Session
- 需要持久化：选择Cookie
- 需要高安全性：选择Session
