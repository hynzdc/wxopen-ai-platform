package com.hyn.demos.enums;

/**
 * @author hyn
 * @version 1.0.0
 * @description 用户中心服务枚举
 * @date 2022/10/29
 */
public enum UserCenterServiceEnum {
    USER_REGISTER_DETAIL_NO_BLANK(100,"用户注册信息不可为空"),
    USER_ACCOUNT_LESS_THEN_FOUR(101,"用户帐户小于4位"),
    USER_PASSWORD_LESS_THEN_EIGHT(102,"用户密码小于8位"),
    USER_ACCOUNT_HAVE_USE(103,"该账户已被使用"),
    USER_ACCOUNT_HAVE_SPECIAL_CHARACTERS(104,"用户名含有特殊字符"),
    USER_TWO_DIFFERENT_PASSWORDS(105,"两次输入的密码不同"),
    USER_FAIL_REGISTER(106,"用户注册失败"),
    USER_LOGIN_DETAIL_NO_BLANK(107,"用户登录信息不可以为空"),
    USER_NOT_FOUND(108,"帐户不存在"),
    USERNAME_OR_PASSWORD_ERROR(108,"用户名或者密码错误"),
    USER_LOGIN_STATE(109,"userLoginState"),
    PERMISSION_DENIED(110,"没有权限访问"),
    USER_TAGS_EMPTY(111,"用户标签为空"),
    USER_NOT_LOGIN(112, "用户未登录"),
    PARAMS_ERROR(113,"请求参数错误"),
    ;

    UserCenterServiceEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    private Integer code;
    private String msg;
}
