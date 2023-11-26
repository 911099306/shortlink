package com.offer.shortlink.admin.controller;

import com.offer.shortlink.admin.common.convention.result.Result;
import com.offer.shortlink.admin.common.convention.result.Results;
import com.offer.shortlink.admin.dto.resp.UserRespDTO;
import com.offer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/26 18:14
 **/
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username) {
        // UserRespDTO result = userService.getUserByUsername(username);
        // if (result == null) {
            // return new Result<UserRespDTO>()
            //         .setCode(UserErrorCodeEnum.USER_NULL.code()).setMessage(UserErrorCodeEnum.USER_NULL.message());
            // return Results.failure(UserErrorCodeEnum.USER_NULL.code(),UserErrorCodeEnum.USER_NULL.message());

        // }
        return Results.success(userService.getUserByUsername(username));
    }
}
