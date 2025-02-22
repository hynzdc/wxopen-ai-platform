package com.hyn.demos.dto.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReplySetReqDTO implements Serializable {
    private static final long serialVersionUID = 1278584321458240102L;
    /**
     * 用户名
     */
    private String username;
    /**
     * 回复内容
     */
    private String replyContent;
}
