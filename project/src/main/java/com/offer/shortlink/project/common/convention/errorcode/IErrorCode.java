package com.offer.shortlink.project.common.convention.errorcode;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/26 20:39
 * 平台错误码
 **/
public interface IErrorCode {
    /**
     * 错误码
     */
    String code();

    /**
     * 错误信息
     */
    String message();
}
