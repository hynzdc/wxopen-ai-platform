package com.hyn.demos.handler;

import com.hyn.demos.service.TalkToAiService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author hyn
 * @version 1.0.0
 * @description
 * @date 2023/11/6
 */
@Component
@Slf4j
public class VoiceHandler implements WxMpMessageHandler {
    @Resource
    private TalkToAiService talkToAiService;
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map, WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        //获取发送语音消息用户
        String fromUser = wxMpXmlMessage.getFromUser();
        //语音消息的内容
        String recognition = wxMpXmlMessage.getRecognition();
        String outContent;
        log.info("用户语音发送的内容:{}",recognition);
        if (StringUtils.isNotBlank(recognition)){
            outContent = talkToAiService.talkToAi(recognition,fromUser);
        }else {
            outContent = "抱歉我并没有识别出你的语音消息可以尝试重新发送";
        }
        return WxMpXmlOutMessage.TEXT().content(outContent).fromUser(wxMpXmlMessage.getToUser())
                .toUser(wxMpXmlMessage.getFromUser()).build();
    }
}
