package com.offer.shortlink.admin.dto.resp;

import lombok.Data;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/26 18:16
 * @description 用户返回参数响应
 **/
@Data
public class UserRespDTO {

    /**
     * 雪花算法id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;


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
