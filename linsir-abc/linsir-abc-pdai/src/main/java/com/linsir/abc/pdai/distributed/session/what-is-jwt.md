# 什么是JWT？

## JWT简介

JWT（JSON Web Token）是一种开放标准（RFC 7519），定义了一种紧凑的、自包含的方式，用于在各方之间以JSON对象安全地传输信息。

## JWT的结构

### 1. JWT的组成

JWT由三部分组成，用点（.）分隔：

```
JWT = Header.Payload.Signature

eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

#### 1.1 Header（头部）

Header通常由两部分组成：令牌的类型（即JWT）和所使用的签名算法（如HMAC SHA256或RSA）。

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

#### 1.2 Payload（负载）

Payload是声明，声明是关于实体（通常是用户）和其他数据的声明。

```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "iat": 1516239022
}
```

#### 1.3 Signature（签名）

Signature是用于验证消息在传输过程中未被篡改的签名。

```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

### 2. JWT的声明

#### 2.1 注册声明

注册声明是一组预定义的声明，不是强制性的，但是推荐的。

| 声明 | 名称 | 描述 |
|------|------|------|
| iss | issuer | 签发者 |
| sub | subject | 主题 |
| aud | audience | 接收方 |
| exp | expiration time | 过期时间 |
| nbf | not before | 生效时间 |
| iat | issued at | 签发时间 |
| jti | JWT ID | 唯一标识 |

#### 2.2 公共声明

公共声明是使用JWT的人可以随意定义的声明。

```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "admin": true
}
```

#### 2.3 私有声明

私有声明是双方同意共享的声明，既不是注册声明，也不是公共声明。

```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "customData": "custom value"
}
```

## JWT的工作原理

### 1. JWT的生成流程

```
1. 客户端发起登录请求
   ↓
2. 服务器验证用户名和密码
   ↓
3. 服务器生成JWT
   ↓
4. 服务器将JWT发送给客户端
   ↓
5. 客户端保存JWT
```

### 2. JWT的验证流程

```
1. 客户端发起请求（携带JWT）
   ↓
2. 服务器验证JWT的签名
   ↓
3. 服务器验证JWT的过期时间
   ↓
4. 服务器从JWT中读取用户信息
   ↓
5. 服务器返回请求的数据
```

## JWT的实现

### 1. 使用Java实现JWT

#### 1.1 生成JWT

```java
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JWTUtil {
    
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 3600000;
    
    public static String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }
}
```

#### 1.2 验证JWT

```java
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

public class JWTUtil {
    
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    public static String validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
```

### 2. 使用Spring Security实现JWT

#### 2.1 JWT过滤器

```java
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = request.getHeader("Authorization");
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            
            String username = JWTUtil.validateToken(token);
            
            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

#### 2.2 Spring Security配置

```java
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter());
    }
}
```

## JWT的优缺点

### 1. 优点

#### 1.1 无状态

- **服务器无状态**：服务器不需要存储Session
- **易于扩展**：可以轻松扩展服务器实例
- **易于部署**：部署简单，无需额外配置

```
┌─────────────┐
│   负载均衡    │
└──────┬──────┘
       │
       ├──────┬──────┐
       │      │      │
┌──────▼──┐ ┌─▼────┐ ┌▼────┐
│ Server 1│ │Server2│ │Server3│
└─────────┘ └──────┘ └─────┘
       ↑      ↑      ↑
       │      │      │
   携带JWT 携带JWT 携带JWT
```

#### 1.2 跨域支持

- **支持跨域**：支持跨域访问
- **移动端友好**：适合移动端应用
- **API友好**：适合API应用

```
http://api.example.com/user
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

http://www.example.com/user
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### 1.3 性能高

- **无网络开销**：无需访问外部存储
- **响应快速**：请求直接处理
- **资源占用少**：无需额外的资源

```
Server 1: 验证JWT → 返回数据
Server 2: 验证JWT → 返回数据
Server 3: 验证JWT → 返回数据

无需访问外部存储
```

### 2. 缺点

#### 2.1 Token无法撤销

- **无法撤销**：Token一旦签发，无法撤销
- **无法失效**：Token一旦签发，无法失效
- **无法更新**：Token一旦签发，无法更新

```
JWT签发后，无法撤销、失效、更新
```

#### 2.2 Token大小

- **Token较大**：Token大小相对较大
- **网络传输**：每次请求都携带Token，增加网络传输量
- **带宽占用**：Token占用带宽

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
         ↑
         Token较大
```

#### 2.3 安全性

- **容易被窃取**：Token容易被窃取
- **容易被篡改**：Token容易被篡改（如果签名算法不安全）
- **容易被伪造**：Token容易被伪造（如果密钥泄露）

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
         ↑
         可以被窃取、篡改、伪造
```

## JWT的适用场景

### 1. 需要无状态

**适用场景**：
- 需要无状态
- 需要跨域访问
- 移动端应用

### 2. API应用

**适用场景**：
- RESTful API
- GraphQL API
- 微服务API

### 3. 单页应用

**适用场景**：
- React应用
- Vue应用
- Angular应用

## JWT的最佳实践

### 1. 使用HTTPS

```java
// 确保JWT通过HTTPS传输
String token = request.getHeader("Authorization");
```

### 2. 设置过期时间

```java
public static String generateToken(String username) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
    
    return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SECRET_KEY)
            .compact();
}
```

### 3. 使用强签名算法

```java
private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
```

### 4. 验证Token

```java
public static String validateToken(String token) {
    try {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    } catch (Exception e) {
        return null;
    }
}
```

### 5. 使用刷新Token

```java
public static String refreshToken(String token) {
    try {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        
        String username = claims.getSubject();
        return generateToken(username);
    } catch (Exception e) {
        return null;
    }
}
```

## JWT与其他方案的对比

| 方案 | 存储位置 | 状态 | 跨域支持 | 性能 | 安全性 |
|------|----------|------|----------|------|--------|
| JWT | 客户端 | 无状态 | 支持 | 高 | 中 |
| Session | 服务器 | 有状态 | 不支持 | 高 | 高 |
| Cookie Based Session | 客户端 | 无状态 | 支持 | 中 | 低 |
| 分布式Session | 共享存储 | 有状态 | 不支持 | 中 | 高 |

## 总结

JWT是一种开放标准（RFC 7519），定义了一种紧凑的、自包含的方式，用于在各方之间以JSON对象安全地传输信息。

**优点**：
- 无状态
- 跨域支持
- 性能高

**缺点**：
- Token无法撤销
- Token大小
- 安全性

**适用场景**：
- 需要无状态
- 需要跨域访问
- 移动端应用
- API应用
- 单页应用

选择JWT需要考虑以下因素：

- **应用场景**：单机应用、分布式应用、微服务应用等
- **功能需求**：是否需要无状态、跨域访问等
- **性能要求**：是否可以接受Token大小
- **安全要求**：是否可以接受Token被窃取
- **学习成本**：团队的技术栈和学习能力
- **维护成本**：方案的维护成本和复杂度

无论选择哪种方案，都需要考虑安全性、可靠性、可维护性、可扩展性等因素，确保会话技术的稳定运行。
