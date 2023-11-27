package com.offer.shortlink.admin.biz.user;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;

import static com.offer.shortlink.admin.common.constant.UserConstant.USER_TOKEN;
import static com.offer.shortlink.admin.common.constant.UserConstant.USER_USERNAME;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27
 **/
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;
    // 添加需要排除的URL
    private static final String EXCLUDED_URL = "http://127.0.0.1:8888/api/short-link/v1/user/login";
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        // 获取请求的URL
        String requestURL = httpServletRequest.getRequestURL().toString();

        // 检查是否是需要排除的URL，如果是，直接放行
        if (EXCLUDED_URL.equals(requestURL)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 从 Header 中获得携带的信息
        String username = httpServletRequest.getHeader(USER_USERNAME);
        String token = httpServletRequest.getHeader(USER_TOKEN);

        Object userInfoJsonStr = stringRedisTemplate.opsForHash().get("login_" + username, token);
        if (userInfoJsonStr != null) {
            // 若 redis 中存在该用户信息，存入 ThreadLocal 内
            UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);

            UserContext.setUser(userInfoDTO);
        }


        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }


}
