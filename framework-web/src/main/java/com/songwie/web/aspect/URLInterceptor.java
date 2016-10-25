package com.songwie.web.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class URLInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(URLInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            LOGGER.info("URL.Method " + request.getMethod() + " " + request.getRequestURL().toString());
            LOGGER.info("URL.Host " + request.getMethod() + " " + request.getRemoteHost());
             
        }catch(Exception e){
            LOGGER.error("cat 拦截异常：" + e.getMessage());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        try {
            String viewName = modelAndView != null?modelAndView.getViewName():"无";
            LOGGER.info("View " + viewName);
        }catch(Exception e){
            LOGGER.error("cat 拦截异常：" + e.getMessage());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,  HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            String node = request.getParameter("node");
             
        }catch(Exception e){
            LOGGER.error("cat 拦截异常：" + e.getMessage());
        }
    }

}