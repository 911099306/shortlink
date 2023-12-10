package com.offer.shortlink.admin.test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author serendipity
 * @version 1.0
 * @date 2023/11/27 16:13
 **/

public class UserTableShardingTest {

    public static final String SQL = "CREATE TABLE `t_user_%d`  (\n" +
            "  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '雪花算法id',\n" +
            "  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',\n" +
            "  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',\n" +
            "  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实姓名',\n" +
            "  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',\n" +
            "  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',\n" +
            "  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间',\n" +
            "  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',\n" +
            "  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',\n" +
            "  `del_flag` tinyint(1) NULL DEFAULT NULL COMMENT '删除标识：\\r\\n\\r\\n0： 未删除\\r\\n\\r\\n1： 已删除',\n" +
            "  PRIMARY KEY (`id`, `username`) USING BTREE,\n" +
            "  UNIQUE INDEX `idx_unique_username`(`username` ASC) USING BTREE\n" +
            ") ENGINE = InnoDB AUTO_INCREMENT = 1729021130074775560 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;\n";
    // public static void main(String[] args) {
    //     for (int i = 0; i < 16; i++) {
    //         System.out.printf((SQL) + "%n",i);
    //     }
    //
    //     HashMap<Integer, Integer> mao = new HashMap<>();
    // }


    public static void main(String[] args) {
        reverseWords("the sky is blue");
    }

    public static String reverseWords(String s) {

        char[] c = s.toCharArray();
        int begin = 0;
        int end = c.length - 1;
        while(begin < c.length && c[begin] == ' '){
            begin++;
        }

        while(end >= 0 && c[end] == ' '){
            end--;
        }
        StringBuilder sb = new StringBuilder();
        while(begin <= end){
            char temp = c[begin];
            if(temp == ' '){
                if(begin > 0 && c[begin -1 ] != ' '){
                    sb.append(temp);
                }
            }else{
                sb.append(temp);
            }
            begin++;
        }

        begin = 0;
        end = sb.length()-1;

        // 遍历开始 取反
        while(begin < end){
            char temp = c[begin];
            c[begin] = c[end];
            c[end] = temp;
            end --;
            begin ++;
        }

        // 每找到一个 空格 前面进行翻转
        begin = 0;
        for(int i = 0; i < sb.length() ; i++){
            if(c[i] ==' '){
                rever(c, begin, i);
                begin = i + 1;
            }
        }

        // 最后一组进行翻转
        rever(c, begin, sb.length());



        return sb.toString();
    }

    public static void rever(char[] c, int begin, int end){

        end --;
        while(begin < end){
            char temp = c[begin];
            c[begin] = c[end];
            c[end] = temp;
            end--;
            begin++;
        }
    }

    public static List<List<Integer>> fourSum(int[] nums, int target) {

        List<List<Integer>> answer = new LinkedList<>();
        StringBuilder stringBuilder = new StringBuilder();

        Arrays.sort(nums);

        for(int first =0; first < nums.length -3; ){

            for(int second = first+1; second < nums.length-2; ){

                int left = second + 1;
                int right = nums.length -1;

                while(left < right){
                    if(nums[first] + nums[second] + nums[left] + nums[right] == target){
                        answer.add(Arrays.asList(nums[first], nums[second], nums[left], nums[right]));
                        left ++;
                        // 去重
                        while(left < right && nums[left] == nums[left-1]){
                            left++;
                        }

                    }else if(nums[first] + nums[second] + nums[left] + nums[right] > target){
                        left++;
                    }else{
                        right--;
                    }
                }

                // 去重
                second++;
                while(second < nums.length -2 && nums[second] == nums[second -1]){
                    second++;
                }
            }

            first++;
            while(first < nums.length -3 && nums[first] == nums[first -1]){
                first++;
            }
        }
        return answer;
    }

}
