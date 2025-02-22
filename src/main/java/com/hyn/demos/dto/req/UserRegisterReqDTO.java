package com.hyn.demos.dto.req;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserRegisterReqDTO implements Serializable {

    private static final long serialVersionUID = 4954568171712901197L;
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
    /**
     * 再次确认密码
     */
    private String passwordAgain;
}
