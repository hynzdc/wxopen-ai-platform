package com.hyn.demos.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.hyn.demos.entity.WxUser;
import com.hyn.demos.enums.UserCenterServiceEnum;
import com.hyn.demos.exception.ErrorCode;
import com.hyn.demos.exception.ThrowUtils;
import com.hyn.demos.service.IDeepSeekService;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hyn
 * @version 1.0.0
 * @description
 * @date 2025/1/7
 */
@Service
@Slf4j
public class DeepSeekServiceImpl implements IDeepSeekService {
    @Override
    public String chatDeepSeek(String content, String fromUser) {
//        OkHttpClient client = new OkHttpClient().
//                newBuilder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .build();
//        MessageDTO messageDTO = new MessageDTO();
//        messageDTO.setRole("user");
//        messageDTO.setContent(content);
//        //Messages messages = new Messages();
////        messages.setMessages(Collections.singletonList(messageDTO));
//        MediaType mediaType = MediaType.parse("application/json");
//        //RequestBody body = RequestBody.create(mediaType, JSONObject.toJSONString(messages));
//        Request request = new Request.Builder()
//                .url("https://api.deepseek.com/chat/completions")
//                .method("POST", body)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Accept", "application/json")
//                .addHeader("Authorization", "Bearer sk-fe58f452ebce4989b8312a76ef5fc2b9")
//                .build();
//        Response response;
//        try {
//            response = client.newCall(request).execute();
//            if (response.isSuccessful()) {
//                assert response.body() != null;
//                String res = response.body().string();
//                JSONObject jsonObject = JSONObject.parseObject(res);
//                JSONObject choices = jsonObject.getJSONArray("choices").getJSONObject(0);
//                JSONObject message = choices.getJSONObject("message");
//                return message.getString("content");
//            } else {
//                return "DeepSeek模型出现了故障,请稍后调用！";
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }

    @Override
    public void chatDeepSeekStream(List<Message> userMsgList, SseEmitter emitter) {
        streamCallDeepSeekWithMessage(userMsgList, emitter);
    }

    @Override
    public String chatDeepSeekView(Model model, HttpServletRequest request) {
        try {
            WxUser wxUser = (WxUser) request.getSession().getAttribute(UserCenterServiceEnum.USER_LOGIN_STATE.getMsg());
            ThrowUtils.throwIf(wxUser == null, ErrorCode.LOGIN_EXPIRED);
            model.addAttribute("username", wxUser.getUsername());
            return "aiTalk";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }


    private static StringBuilder reasoningContent = new StringBuilder();
    private static StringBuilder finalContent = new StringBuilder();
    private static boolean isFirstPrint = true;

    private static ConcurrentHashMap<SseEmitter, EmitterState> stateMap = new ConcurrentHashMap<>();

    private static class EmitterState {
        StringBuilder reasoningContent = new StringBuilder();
        StringBuilder finalContent = new StringBuilder();
        boolean isFirstPrint = true;
    }

    /**
     * @description 处理回答
     * @author hyn
     * @param message
     * @date 2025-02-20 16:01
     */
    private static void handleGenerationResultSafe(GenerationResult message, SseEmitter emitter) throws IOException {
        // 获取或创建与当前emitter关联的状态
        EmitterState state = stateMap.computeIfAbsent(emitter, k -> {
            // 注册清理回调
            emitter.onCompletion(() -> stateMap.remove(emitter));
            emitter.onError(e -> stateMap.remove(emitter));
            return new EmitterState();
        });

        String reasoning = message.getOutput().getChoices().get(0).getMessage().getReasoningContent();
        String content = message.getOutput().getChoices().get(0).getMessage().getContent();

        // 处理思考内容
        if (!reasoning.isEmpty()) {
            state.reasoningContent.append(reasoning);
            if (state.isFirstPrint) {
                System.out.println("====================思考过程====================");
                emitter.send("<think>");
                state.isFirstPrint = false;
            }
            emitter.send(reasoning);
            System.out.print(reasoning);
        }

        // 处理最终回复
        if (!content.isEmpty()) {
            state.finalContent.append(content);
            if (!state.isFirstPrint) {
                System.out.println("\n====================完整回复====================");
                emitter.send("</think>");
                emitter.send("<reply>");
                // 重置标记以保证后续请求的初始状态
                state.isFirstPrint = true;
            }
            System.out.print(content);
            emitter.send(content);
        }
    }

    /**
     * @description 构造问题参数
     * @author hyn
     * @param userMsgList
     * @date 2025-02-20 16:00
     * @return com.alibaba.dashscope.aigc.generation.GenerationParam
     */
    private static GenerationParam buildGenerationParam(List<Message> userMsgList) {
        return GenerationParam.builder()
                .model("deepseek-r1")
                .messages(userMsgList)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .incrementalOutput(true)
                .build();
    }

    /**
     * @param userMsgList
     * @param emitter
     * @description 问题流式回答
     * @author hyn
     * @date 2025-02-20 16:01
     */
    public static void streamCallDeepSeekWithMessage(List<Message> userMsgList, SseEmitter emitter) {
        Generation gen = new Generation();
        try {
            GenerationParam generationParam = buildGenerationParam(userMsgList);
            Flowable<GenerationResult> result = gen.streamCall(generationParam);
            result.blockingForEach(res -> handleGenerationResultSafe(res, emitter));
            emitter.send("</reply>");
            emitter.complete();
        } catch (Exception e) {
            emitter.completeWithError(e); // 发送错误
        }
    }

    /**
     * @description 处理回答
     * @author hyn
     * @param message
     * @date 2025-02-20 16:01
     */
    private static void handleGenerationResult(GenerationResult message,SseEmitter emitter) throws IOException {
        String reasoning = message.getOutput().getChoices().get(0).getMessage().getReasoningContent();
        String content = message.getOutput().getChoices().get(0).getMessage().getContent();

        if (!reasoning.isEmpty()) {
            reasoningContent.append(reasoning);
            if (isFirstPrint) {
                System.out.println("====================思考过程====================");
                emitter.send("<think>");
                isFirstPrint = false;
            }
            emitter.send(reasoning);
            System.out.print(reasoning);
        }

        if (!content.isEmpty()) {
            finalContent.append(content);
            if (!isFirstPrint) {
                System.out.println("\n====================完整回复====================");
                emitter.send("</think>");
                emitter.send("<reply>");
                isFirstPrint = true;
            }
            System.out.print(content);
            emitter.send(content);
        }
    }
}
