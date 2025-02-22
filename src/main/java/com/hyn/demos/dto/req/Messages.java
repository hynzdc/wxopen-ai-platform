package com.hyn.demos.dto.req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hyn
 * @version 1.0.0
 * @description 消息请求
 * @date 2025/1/7
 */
@Data
public class Messages implements Serializable {
    private static final long serialVersionUID = -6603463524978559412L;
    private List<MessageDTO> messages;
    private String model = "deepseek-chat";
}
