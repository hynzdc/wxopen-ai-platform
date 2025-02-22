package com.hyn.demos.dto.req;

import lombok.Data;

import java.io.Serializable;
@Data
public class WxUserConfigReqDTO implements Serializable {
    private static final long serialVersionUID = -1252590777259528855L;
    /**
     * 用户名
     */
    private String username;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 公众号名称
     */
    private String officialAccount;
    /**
     * 开发者id
     */
    private String appId;
    /**
     * 开发者密钥(AppSecret)
     */
    private String appSecret;
    /**
     * 令牌(Token)
     */
    private String token;
    /**
     * 消息加解密密钥
     */
    private String encodingAESKey;
}
