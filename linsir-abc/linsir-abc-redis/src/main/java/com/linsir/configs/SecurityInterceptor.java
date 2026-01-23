package com.linsir.configs;

import com.linsir.components.Listener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SecurityInterceptor  implements HandlerInterceptor {

    private final static Logger log = LoggerFactory.getLogger(Listener.class);

    // URL白名单，不需要登录验证的路径
    private final List<String> whiteList = new ArrayList<>();

    public SecurityInterceptor() {
        // 初始化白名单
        whiteList.add("/index");
        whiteList.add("/login/");
        whiteList.add("/redis/data-type/");
        whiteList.add("/redis/hot-data/");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // 检查是否在白名单中
        for (String whitePath : whiteList) {
            if (requestURI.equals(whitePath) || requestURI.startsWith(whitePath)) {
                log.info("session拦截器，URL={}在白名单中，直接通过", requestURI);
                return true;
            }
        }
        
        // 不在白名单中，检查session
        HttpSession session = request.getSession();
        // 验证当前session是否存在，存在返回true true代表能正常处理业务逻辑
        if (session.getAttribute("user") != null) {
            log.info("session拦截器，session={},{}，验证通过", session.getId(), session.getAttribute("user"));
            return true;
        }
        // session不存在，返回false，并提示请重新登录。
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write("请登录！！！！！");
        log.info("session拦截器，session={}，验证失败", session.getId());
        return false;
    }

}
