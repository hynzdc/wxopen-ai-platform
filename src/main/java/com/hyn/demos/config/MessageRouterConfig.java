package com.hyn.demos.config;

import com.hyn.demos.handler.GetResourceButtonHandler;
import com.hyn.demos.handler.MyTextHandler;
import com.hyn.demos.handler.SubResourceButtonHandler;
import com.hyn.demos.handler.SubscribeHandler;
import com.hyn.demos.handler.UnSubscribeHandler;
import com.hyn.demos.handler.VoiceHandler;
import com.hyn.demos.intercepter.MyTextInterceptor;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author hyn
 * @version 1.0.0
 * @description 消息路由配置
 * @date 2023/11/1
 */
@Configuration
public class MessageRouterConfig {
    @Resource
    private WxMpService wxMpService;
    @Resource
    private MyTextHandler myTextHandler;
    @Resource
    private MyTextInterceptor myTextInterceptor;
    @Resource
    private SubscribeHandler subscribeHandler;
    @Resource
    private UnSubscribeHandler unSubscribeHandler;
    @Resource
    private VoiceHandler voiceHandler;
    @Resource
    private SubResourceButtonHandler subResourceButtonHandler;
    @Resource
    private GetResourceButtonHandler getResourceButtonHandler;

    @Bean
    public WxMpMessageRouter messageRouter() {
        // 创建消息路由
        final WxMpMessageRouter router = new WxMpMessageRouter(wxMpService);
        // 添加一个同步处理文本消息的路由规则 同时添加interceptor、handler
        router.rule().async(false).msgType(WxConsts.XmlMsgType.TEXT).interceptor(myTextInterceptor).handler(myTextHandler).end();
        // 关注handler 订阅事件
        router.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.EventType.SUBSCRIBE).handler(subscribeHandler).end();
        // 取消订阅事件
        router.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.EventType.UNSUBSCRIBE).handler(unSubscribeHandler).end();
        // 语音回复事件
        router.rule().async(false).msgType(WxConsts.XmlMsgType.VOICE).handler(voiceHandler).end();
        // 贡献资源按钮回复事件
        router.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.EventType.CLICK).eventKey("1").handler(subResourceButtonHandler).end();
        // 获取资源列表按钮事件
        router.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.EventType.CLICK).eventKey("2").handler(getResourceButtonHandler).end();
        return router;
    }
}
