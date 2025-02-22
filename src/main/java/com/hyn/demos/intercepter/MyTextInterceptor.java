package com.hyn.demos.intercepter;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageInterceptor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author hyn
 * @version 1.0.0
 * @description 微信消息处理拦截器
 * @date 2023/11/1
 */
@Component
public class MyTextInterceptor implements WxMpMessageInterceptor {
    @Override
    public boolean intercept(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map, WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        String msg = wxMpXmlMessage.getContent();
        String msgType = wxMpXmlMessage.getMsgType();
        if (msgType.equals("text") && msg.contains("混蛋")) {
            wxMpXmlMessage.setContent("***");
            return true;
        }
        return true;
    }
}
