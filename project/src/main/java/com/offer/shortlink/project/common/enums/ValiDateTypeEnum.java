package com.offer.shortlink.project.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/12/10
 *
 * 有效期类型
 **/
@RequiredArgsConstructor
public enum ValiDateTypeEnum {

    /**
     * 永久有效期
     */
    PERMANENT(0),

    /**
     * 自定义有效期
     */
    CUSTOM(1);

    @Getter
    private final int type;

}
