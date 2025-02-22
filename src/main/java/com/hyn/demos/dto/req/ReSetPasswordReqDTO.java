package com.hyn.demos.dto.req;

import lombok.Data;

import java.io.Serializable;
@Data
public class ReSetPasswordReqDTO implements Serializable {
    private static final long serialVersionUID = 2947363490206307405L;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 新密码
     */
    private String password;
    /**
     * 确认密码
     */
    private String passwordAgain;
}
