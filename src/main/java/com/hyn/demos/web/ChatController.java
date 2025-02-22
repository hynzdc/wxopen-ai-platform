package com.hyn.demos.web;

import com.alibaba.dashscope.common.Message;
import com.hyn.demos.service.IDeepSeekService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.Executors;


@Controller
@RequestMapping("/stream")
public class ChatController {
    @Resource
    private IDeepSeekService deepSeekService;

    @PostMapping("/sse")
    public SseEmitter streamData(@RequestBody List<Message> userMsgList, HttpServletResponse response) {
        SseEmitter emitter = new SseEmitter(600000L); // 超时时间设为60秒
        response.setHeader("Content-Type", "text/event-stream");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        // 使用线程池异步处理任务
        Executors.newSingleThreadExecutor().submit(() -> {
            deepSeekService.chatDeepSeekStream(userMsgList, emitter);
        });
        return emitter;
    }


}