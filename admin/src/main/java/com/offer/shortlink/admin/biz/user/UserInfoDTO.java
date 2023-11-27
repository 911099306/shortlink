package com.offer.shortlink.admin.biz.user;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {


    /**
     * 用户id
     */
    @JSONField(name = "id")
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 用户 Token
     */
    private String token;
}
