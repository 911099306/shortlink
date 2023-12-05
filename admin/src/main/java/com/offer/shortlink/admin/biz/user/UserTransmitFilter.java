package com.offer.shortlink.admin.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.offer.shortlink.admin.common.convention.exception.ClientException;
import com.offer.shortlink.admin.common.convention.result.Results;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static com.offer.shortlink.admin.common.constant.UserConstant.USER_TOKEN;
import static com.offer.shortlink.admin.common.constant.UserConstant.USER_USERNAME;
import static com.offer.shortlink.admin.common.enums.UserErrorCodeEnum.USER_TOKEN_FAIL;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27
 **/
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;
    // 添加需要排除的URL
    // private static final String EXCLUDED_URL = "/api/short-link/admin/v1/user/login";

    private static final List<String> EXCLUDED_URL = Lists.newArrayList(
            "/api/short-link/admin/v1/user/login",
            "/api/short-link/admin/v1/user/has-username"
    );
    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        // 获取请求的URL
        String requestURL = httpServletRequest.getRequestURI();

        // 检查是否是需要排除的URL，如果是，直接放行
        String method = httpServletRequest.getMethod();
        if (EXCLUDED_URL.contains(requestURL)
                || ("/api/short-link/admin/v1/user".equals(requestURL) && "POST".equals(method))) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 从 Header 中获得携带的信息
        String username = httpServletRequest.getHeader(USER_USERNAME);
        String token = httpServletRequest.getHeader(USER_TOKEN);

        if (!StrUtil.isAllNotBlank(username, token)) {
            returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(USER_TOKEN_FAIL))));
            return;
        }


        Object userInfoJsonStr = null;
        try {
            // 避免redis不存在，报错
            userInfoJsonStr = stringRedisTemplate.opsForHash().get("login_" + username, token);
            if (userInfoJsonStr == null) {
                returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(USER_TOKEN_FAIL))));
                return;
            }
        } catch (Exception ex) {
            returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(USER_TOKEN_FAIL))));
            return;
        }

        // 若 redis 中存在该用户信息，存入 ThreadLocal 内
        UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);

        UserContext.setUser(userInfoDTO);


        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }



    private void returnJson(HttpServletResponse response, String json) throws Exception {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);

        } catch (IOException e) {
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }


}
