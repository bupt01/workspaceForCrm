package com.bjpowernode.crm.settings.web.filter;


import com.bjpowernode.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        System.out.println("进入到验证有没有登陆的过滤器");
        HttpServletRequest request=(HttpServletRequest) req;
        HttpServletResponse response=(HttpServletResponse)resp;

        String path=request.getServletPath();
        //不应该被拦截的放行
        if ("/login.jsp".equals(path)||"/settings/user/login.do".equals(path)){
            chain.doFilter(req,resp);
            //其他资源必须验证
        }else{
            HttpSession session=request.getSession();
            User user=(User)session.getAttribute("user");
            //如果user不为空，放行
            if (user!=null){
                chain.doFilter(req,resp);
            }else {
                //重定向到登陆页
                /**
                 * 重定向的路径是什么？怎么写？
                 * 在实际项目的开发中对于路径的使用，无论是前端还是后端，应该一律使用绝对路径
                 * 关于转发和重定向的写法如下：
                 * 转发：
                 *      使用的是一种特殊的绝对路径的使用方式，这种绝对路径前面不加项目名，这种路径也称之为内部路径
                 *      /login.jsp
                 * 重定向：
                 *      使用的是传统的绝对路径的写法，前面必须以项目名开头，后面跟着具体的资源路径
                 *      /crm/login.jsp
                 * 为什么使用重定向？使用转发不行吗？
                 * 转发之后，路径会停留在老路径上，而不是跳转最新的资源路径，我们应该为用户跳转到登陆页的同时，将
                 * 游览器的地址栏应该自动设置为当前的登陆页的路径
                 */
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }
        }
    }
}
