package com.hyn.demos.dto.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginReqDTO implements Serializable {
    private static final long serialVersionUID = -4387769227624896232L;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 验证码
     */
    private String verifyCode;
}
