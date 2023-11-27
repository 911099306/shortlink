package com.offer.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.common.convention.result.Results;
import com.offer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.offer.shortlink.admin.dto.resp.UserActualRespDTO;
import com.offer.shortlink.admin.dto.resp.UserRespDTO;
import com.offer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/26 18:14
 **/
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return
     */
    @GetMapping("/api/short-link/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        // UserRespDTO result = userService.getUserByUsername(username);
        // if (result == null) {
            // 这里在后端查询后判断是否为空，然后抛出异常处理
            // return new Result<UserRespDTO>()
            //         .setCode(UserErrorCodeEnum.USER_NULL.code()).setMessage(UserErrorCodeEnum.USER_NULL.message());
            // return Results.failure(UserErrorCodeEnum.USER_NULL.code(),UserErrorCodeEnum.USER_NULL.message());

        // }
        return Results.success(userService.getUserByUsername(username));
    }


    /**
     * 根据用户名查询用户未脱敏信息
     * @param username
     * @return
     */
    @GetMapping("/api/short-link/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable("username") String username) {

        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), UserActualRespDTO.class));
    }


    @GetMapping("/api/short-link/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        return Results.success(userService.hasUsername(username));
    }


    @PostMapping("/api/short-link/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

}
