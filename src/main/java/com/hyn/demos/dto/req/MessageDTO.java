package com.hyn.demos.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hyn
 * @version 1.0.0
 * @description
 * @date 2025/1/7
 */
@Data
public class MessageDTO implements Serializable {
    private static final long serialVersionUID = -4541360653923200004L;
    /**
     * 角色
     */
    private String role;
    /**
     * 内容
     */
    private String content;
}
