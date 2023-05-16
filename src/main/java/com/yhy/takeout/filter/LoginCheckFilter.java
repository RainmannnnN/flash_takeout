package com.yhy.takeout.filter;

import com.alibaba.fastjson.JSON;
import com.yhy.takeout.common.Result;
import com.yhy.takeout.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 检查用户是否登录的过滤器
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 获取URI
        String requestURI = request.getRequestURI();

        // 定义不需要处理的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg", // 短信验证码
                "/user/login" // 用户登录
        };

        // 判断这次请求是否要处理
        boolean check = check(urls, requestURI);

        // 不要处理，直接放行
        if(check){
            filterChain.doFilter(request, response);
            return;
        }

        // 判断员工登录状态，如果登录则可以放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("user already login!");
            // 在当前线程存储id，给MP的元数据填充处理器
            Long empId = (Long) request.getSession().getAttribute("employee");
            ThreadLocalUtil.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }

        // 判断用户登录状态，如果登录则可以放行
        if (request.getSession().getAttribute("user") != null) {
            // 在当前线程存储id，给MP的元数据填充处理器
            Long userId = (Long) request.getSession().getAttribute("user");
            ThreadLocalUtil.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }

        // 如果没登录则返回未登陆结果，通过输出流的方式向客户端页面返回数据
        log.info("u have to login please!");
        PrintWriter out = response.getWriter();
        out.write(JSON.toJSONString(Result.error("NOTLOGIN")));
        return;

    }

    /**
     * 匹配路径检查是否要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }

}
