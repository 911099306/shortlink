package com.offer.shortlink.admin.toolkit;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27
 *
 * 分组 ID 随机生成器
 **/
public final class RandomGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new SecureRandom();


    /**
     * 生成长度为6的字符串
     */
    public static String generateRandomString() {

        return generateRandomString(6);
    }

    /**
     * 随机生成指定长度的字符串
     * @param length 指定字符串长度
     */
    public static String generateRandomString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(randomIndex));
        }

        return stringBuilder.toString();
    }
}
