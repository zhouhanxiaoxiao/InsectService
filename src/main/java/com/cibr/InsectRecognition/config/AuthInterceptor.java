package com.cibr.InsectRecognition.config;

import com.cibr.InsectRecognition.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisUtil redisUtil;

    private Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        if (request.getMethod().equals("OPTIONS")){
            response.setStatus(200);
            return true;
        }
        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            response.setStatus(401);
            return false;
        }
        Object loginStatus = redisUtil.get(token);
        if(Objects.isNull(loginStatus)){
            response.setStatus(401);
            return false;
        }
        redisUtil.set(token,loginStatus);
        logger.info(loginStatus.toString());
        logger.info(request.getRequestURI());
        request.setAttribute("user", loginStatus);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
