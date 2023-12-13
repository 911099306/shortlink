package com.offer.shortlink.project.toolkit;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.offer.shortlink.project.common.constant.ShortLinkConstant;

import java.util.Date;
import java.util.Optional;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/12/13
 *
 * 短链接工具类
 **/
public class LinkUtil {

    /**
     * 返回有效期时间
     * @param validate 有效期时间 永久有效： 空，  临时有效：有效期时间
     * @return 有效期时间戳
     */
    public static long getLinkCacheValidTime(Date validate) {
        // 若有效期，就传入时间。
        // 永久有效，传入的就是null
        return Optional.ofNullable(validate)
                // 不等于空         计算，当前时间，和有效时间的差值，转换成 毫秒
                .map(each -> DateUtil.between(new Date(), each, DateUnit.MS))
                // 等于空          一个月的时间
                .orElse(ShortLinkConstant.DEFAULT_CACHE_VALID_DATE);
    }
}
