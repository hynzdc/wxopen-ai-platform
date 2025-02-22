package com.hyn.demos.handler;

import com.hyn.demos.config.MyRedissonConfig;
import com.hyn.demos.entity.WxSubscribeConfig;
import com.hyn.demos.service.WxSubscribeConfigService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.redisson.api.RBucket;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author hyn
 * @version 1.0.0
 * @description 关注、取消关注Handler
 * @date 2023/11/3
 */
@Component
@Slf4j
public class SubscribeHandler implements WxMpMessageHandler {
    @Resource
    private MyRedissonConfig redissonConfig;
    @Resource
    private WxSubscribeConfigService wxSubscribeConfigService;
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map, WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        RBucket<Object> bucket = redissonConfig.redissonClient().getBucket(wxMpXmlMessage.getToUser());
        String userName = (String)bucket.get();
        WxSubscribeConfig wxSubscribeConfig = wxSubscribeConfigService.lambdaQuery().eq(WxSubscribeConfig::getWxUserName, userName).one();
        return WxMpXmlOutMessage.TEXT().fromUser(wxMpXmlMessage.getToUser()).toUser(wxMpXmlMessage.getFromUser())
                .content(wxSubscribeConfig.getReplyText()).build();
    }
}
