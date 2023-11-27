package com.offer.shortlink.admin.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27
 *
 * 用户登录接口返回参数
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRespDTO {

    /**
     * 用户请求信息
     */
    private String token;
}
