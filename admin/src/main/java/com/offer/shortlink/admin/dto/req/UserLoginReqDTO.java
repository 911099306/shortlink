package com.offer.shortlink.admin.dto.req;

import lombok.Data;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27
 *
 * 用户登录 请求参数
 **/
@Data
public class UserLoginReqDTO {
    
    private String username;
    private String password;
}
