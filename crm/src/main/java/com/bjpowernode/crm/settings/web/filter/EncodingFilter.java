package com.bjpowernode.crm.settings.web.filter;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        System.out.println("浸入过滤字符编码的过滤器");
        req.setCharacterEncoding("UTF-8");
        //过滤向应流响应中文乱码
        resp.setContentType("text/html;charset=utf-8");
        //将请求放行
        chain.doFilter(req,resp);

    }
}
