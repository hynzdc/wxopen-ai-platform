package com.hyn.demos.dto.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class ForgotPasswordReqDTO implements Serializable {
    private static final long serialVersionUID = -5206236564822062102L;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 验证码
     */
    private String verifyCode;
}
