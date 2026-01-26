# 使用JWT的流程？

## JWT使用流程简介

JWT的使用流程包括JWT的生成、JWT的验证、JWT的刷新等环节。

## JWT的完整流程

### 1. 用户登录流程

```
1. 用户输入用户名和密码
   ↓
2. 客户端发送登录请求到服务器
   ↓
3. 服务器验证用户名和密码
   ↓
4. 服务器生成JWT
   ↓
5. 服务器将JWT发送给客户端
   ↓
6. 客户端保存JWT
```

### 2. 访问受保护资源流程

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

### 3. JWT刷新流程

```
1. 客户端发起刷新Token请求（携带JWT）
   ↓
2. 服务器验证JWT的签名
   ↓
3. 服务器验证JWT的过期时间
   ↓
4. 服务器生成新的JWT
   ↓
5. 服务器将新的JWT发送给客户端
   ↓
6. 客户端保存新的JWT
```

## JWT的详细实现

### 1. 用户登录实现

#### 1.1 登录接口

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.findByUsername(loginRequest.getUsername());
        
        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码错误");
        }
        
        String token = JWTUtil.generateToken(user.getUsername());
        
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
```

#### 1.2 登录请求

```java
public class LoginRequest {
    private String username;
    private String password;
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
```

#### 1.3 登录响应

```java
public class AuthResponse {
    private String token;
    
    public AuthResponse(String token) {
        this.token = token;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
}
```

### 2. JWT验证实现

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
    
    @Autowired
    private UserDetailsService userDetailsService;
    
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

#### 2.2 JWT验证工具类

```java
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
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
}
```

### 3. 访问受保护资源实现

#### 3.1 受保护资源接口

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(new UserInfoResponse(userDetails.getUsername()));
    }
}
```

#### 3.2 用户信息响应

```java
public class UserInfoResponse {
    private String username;
    
    public UserInfoResponse(String username) {
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
}
```

### 4. JWT刷新实现

#### 4.1 刷新Token接口

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token无效");
        }
        
        String token = authorization.substring(7);
        String newToken = JWTUtil.refreshToken(token);
        
        if (newToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token刷新失败");
        }
        
        return ResponseEntity.ok(new AuthResponse(newToken));
    }
}
```

### 5. Spring Security配置

#### 5.1 Security配置类

```java
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private JWTAuthenticationEntryPoint unauthorizedHandler;
    
    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }
    
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(jwtAuthenticationFilter(), authenticationManager());
    }
}
```

#### 5.2 认证入口点

```java
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权");
    }
}
```

## JWT的前端实现

### 1. 登录实现

#### 1.1 登录表单

```html
<!DOCTYPE html>
<html>
<head>
    <title>登录</title>
</head>
<body>
    <form id="loginForm">
        <input type="text" id="username" placeholder="用户名">
        <input type="password" id="password" placeholder="密码">
        <button type="submit">登录</button>
    </form>
    
    <script>
        document.getElementById('loginForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });
            
            const data = await response.json();
            
            if (response.ok) {
                localStorage.setItem('token', data.token);
                window.location.href = '/index.html';
            } else {
                alert(data.message);
            }
        });
    </script>
</body>
</html>
```

### 2. 访问受保护资源实现

#### 2.1 受保护资源页面

```html
<!DOCTYPE html>
<html>
<head>
    <title>用户信息</title>
</head>
<body>
    <div id="userInfo"></div>
    
    <script>
        const token = localStorage.getItem('token');
        
        if (!token) {
            window.location.href = '/login.html';
        }
        
        fetch('/api/user/info', {
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
        .then(response => response.json())
        .then(data => {
            document.getElementById('userInfo').innerHTML = '用户名：' + data.username;
        })
        .catch(error => {
            console.error('Error:', error);
            localStorage.removeItem('token');
            window.location.href = '/login.html';
        });
    </script>
</body>
</html>
```

### 3. JWT刷新实现

#### 3.1 刷新Token脚本

```javascript
async function refreshToken() {
    const token = localStorage.getItem('token');
    
    if (!token) {
        return;
    }
    
    const response = await fetch('/api/auth/refresh', {
        headers: {
            'Authorization': 'Bearer ' + token
        }
    });
    
    const data = await response.json();
    
    if (response.ok) {
        localStorage.setItem('token', data.token);
    } else {
        localStorage.removeItem('token');
        window.location.href = '/login.html';
    }
}

// 定时刷新Token
setInterval(refreshToken, 30 * 60 * 1000);
```

## JWT的最佳实践

### 1. 使用HTTPS

```javascript
fetch('/api/user/info', {
    headers: {
        'Authorization': 'Bearer ' + token
    }
})
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

### 3. 使用刷新Token

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

### 5. 使用强签名算法

```java
private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
```

## JWT的安全注意事项

### 1. 保护密钥

```java
private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
```

### 2. 使用HTTPS

```javascript
fetch('https://api.example.com/api/user/info', {
    headers: {
        'Authorization': 'Bearer ' + token
    }
})
```

### 3. 设置过期时间

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

## 总结

JWT的使用流程包括JWT的生成、JWT的验证、JWT的刷新等环节。

**JWT生成流程**：
1. 用户输入用户名和密码
2. 客户端发送登录请求到服务器
3. 服务器验证用户名和密码
4. 服务器生成JWT
5. 服务器将JWT发送给客户端
6. 客户端保存JWT

**JWT验证流程**：
1. 客户端发起请求（携带JWT）
2. 服务器验证JWT的签名
3. 服务器验证JWT的过期时间
4. 服务器从JWT中读取用户信息
5. 服务器返回请求的数据

**JWT刷新流程**：
1. 客户端发起刷新Token请求（携带JWT）
2. 服务器验证JWT的签名
3. 服务器验证JWT的过期时间
4. 服务器生成新的JWT
5. 服务器将新的JWT发送给客户端
6. 客户端保存新的JWT

使用JWT需要注意以下事项：

- **保护密钥**：确保密钥的安全
- **使用HTTPS**：确保JWT通过HTTPS传输
- **设置过期时间**：设置合理的过期时间
- **验证Token**：每次请求都验证Token
- **使用刷新Token**：使用刷新Token机制

无论选择哪种方案，都需要考虑安全性、可靠性、可维护性、可扩展性等因素，确保会话技术的稳定运行。
