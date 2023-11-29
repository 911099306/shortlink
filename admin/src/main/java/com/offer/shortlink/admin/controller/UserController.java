package com.offer.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.common.convention.result.Results;
import com.offer.shortlink.admin.dto.req.UserLoginReqDTO;
import com.offer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.offer.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.offer.shortlink.admin.dto.resp.UserActualRespDTO;
import com.offer.shortlink.admin.dto.resp.UserLoginRespDTO;
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
    @GetMapping("/api/short-link/admin/v1/user/{username}")
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
    @GetMapping("/api/short-link/admin/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable("username") String username) {

        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), UserActualRespDTO.class));
    }


    @GetMapping("/api/short-link/admin/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        return Results.success(userService.hasUsername(username));
    }


    @PostMapping("/api/short-link/admin/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    /**
     * 根据用户名修改用户信息
     * @param requestParam 用户修改信息请求参数
     */
    @PutMapping("/api/short-link/admin/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam) {
        userService.update(requestParam);
        return Results.success();
    }

    /**
     * 用户登录
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/admin/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {

        UserLoginRespDTO userLoginRespDTO = userService.login(requestParam);
        return Results.success(userLoginRespDTO);
    }

    /**
     * 用户退出登录
     * @param token 唯一uuid
     * @param username 用户名
     */
    @DeleteMapping("/api/short-link/admin/v1/user/logout")
    public Result<Void> logout(@RequestParam("token") String token,@RequestParam("username") String username ) {
        userService.logout(username, token);
        return Results.success();
    }


    /**
     * 查看用户登录情况
     * @param token 唯一uuid
     * @param username 用户名
     */
    @GetMapping("/api/short-link/admin/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("token") String token,@RequestParam("username") String username) {
        return Results.success(userService.checkLogin(username,token));
    }



}
