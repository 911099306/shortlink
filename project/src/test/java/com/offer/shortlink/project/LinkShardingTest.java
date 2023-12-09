package com.offer.shortlink.project;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/29
 **/
public class LinkShardingTest {

    // public static final String SQL =
    //        "CREATE TABLE `t_link_%d`  (\n" +
    //                "  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',\n" +
    //                "  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '域名',\n" +
    //                "  `short_uri` varchar(8) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '短链接',\n" +
    //                "  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '完整短链接',\n" +
    //                "  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原始链接',\n" +
    //                "  `click_num` int NULL DEFAULT 0 COMMENT '点击量',\n" +
    //                "  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分组标识',\n" +
    //                "  `favicon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '短链接图标信息',\n" +
    //                "  `enable_status` tinyint(1) NULL DEFAULT NULL COMMENT '启用标识 0：启用 1：未启用',\n" +
    //                "  `created_type` tinyint(1) NULL DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台创建',\n" +
    //                "  `valid_date_type` tinyint(1) NULL DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',\n" +
    //                "  `valid_date` datetime NULL DEFAULT NULL COMMENT '有效期',\n" +
    //                "  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',\n" +
    //                "  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',\n" +
    //                "  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',\n" +
    //                "  `del_flag` tinyint(1) NULL DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',\n" +
    //                "  PRIMARY KEY (`id`) USING BTREE,\n" +
    //                "  UNIQUE INDEX `idx_unique_full_short_url`(`full_short_url` ASC) USING BTREE\n" +
    //                ") ENGINE = InnoDB AUTO_INCREMENT = 1729838178111713282 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;\n" +
    //                "\n" +
    //                "SET FOREIGN_KEY_CHECKS = 1;";


    public static final String SQL = "CREATE TABLE `t_group_%d` (\n" +
            "  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',\n" +
            "  `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',\n" +
            "  `name` varchar(64) DEFAULT NULL COMMENT '分组名称',\n" +
            "  `username` varchar(256) DEFAULT NULL COMMENT '创建分组用户名',\n" +
            "  `sort_order` int DEFAULT NULL COMMENT '分组排序',\n" +
            "  `create_time` datetime DEFAULT NULL COMMENT '创建时间',\n" +
            "  `update_time` datetime DEFAULT NULL COMMENT '修改时间',\n" +
            "  `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `idx_unique_username_gid` (`gid`,`username`) USING BTREE\n" +
            ") ENGINE=InnoDB AUTO_INCREMENT=1731962212949553155 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL) + "%n", i);
            System.out.println();
        }
    }
}