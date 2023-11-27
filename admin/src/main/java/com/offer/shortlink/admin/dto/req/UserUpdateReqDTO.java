package com.offer.shortlink.admin.dto.req;

import lombok.Data;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27 13:45
 *
 * 用户注册请求参数
 **/
@Data
public class UserUpdateReqDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

}
