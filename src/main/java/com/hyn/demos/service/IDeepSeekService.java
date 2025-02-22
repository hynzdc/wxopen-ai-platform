package com.hyn.demos.service;

import com.alibaba.dashscope.common.Message;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author hyn
 * @version 1.0.0
 * @description deepSeek大模型
 * @date 2025/1/7
 */
public interface IDeepSeekService {
    String chatDeepSeek(String content,String fromUser);

    void chatDeepSeekStream(List<Message> userMsgList, SseEmitter emitter);


    String chatDeepSeekView(Model model, HttpServletRequest request);
}
