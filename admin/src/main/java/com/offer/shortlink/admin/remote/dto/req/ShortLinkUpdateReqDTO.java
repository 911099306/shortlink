 package com.offer.shortlink.admin.remote.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

 /**
  * @author serendipity
  * @version 1.0
  * @date 2023/11/29
  *
  * 短链接修改请求对象
  **/
 @Data
 public class ShortLinkUpdateReqDTO {

     /**
      * 短链接
      */
     private String fullShortUrl;

     /**
      * 域名
      */
     private String domain;

     /**
      * 原始链接
      */
     private String originUrl;

     /**
      * 分组标识
      */
     private String gid;

     /**
      * 有效期类型 0：永久有效 1：用户自定义
      */
     private Integer validDateType;

     /**
      * 有效期
      */
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     private Date validDate;

     /**
      * 描述
      */
     private String describe;
 }
