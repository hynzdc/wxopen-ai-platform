package com.hyn.demos.web;

import com.hyn.demos.config.MyRedissonConfig;
import com.hyn.demos.config.WxMpDynamicsConfiguration;
import com.hyn.demos.entity.WxUserConfig;
import com.hyn.demos.service.IDeepSeekService;
import com.hyn.demos.service.WxUserConfigService;
import com.hyn.demos.service.impl.TalkToAiServiceImpl;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author hyn
 * @version 1.0.0
 * @description 微信请求controller
 * @date 2023/11/1
 */
@RestController
@RequestMapping("/wx")
@Slf4j
public class WxController {
//    @Resource
//    private WxMpService wxMpService;

    @Resource
    private WxMpMessageRouter wxMpMessageRouter;

    @Resource
    private IDeepSeekService deepSeekService;

    @Resource
    private WxUserConfigService wxUserConfigService;

    @Resource
    private WxMpDynamicsConfiguration.WxMpServiceFactory wxMpServiceFactory;

    @Resource
    private MyRedissonConfig redissonConfig;

    /**
     * @description 验证消息来自微信的服务器 开发者通过检验signature来进行校验，若确认此次get请求是来自微信服务器，请原样返回echostr参数内容，则接入生效
     * @author hyn
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @date 2023-11-01 14:27
     * @return java.lang.String
     */
    @GetMapping("/send/{fromIdStr}")
    public String configAccess(String signature, String timestamp, String nonce, String echostr, @PathVariable(name = "fromIdStr") String fromIdStr) {
        try {
            WxMpService wxMpService = getWxService(fromIdStr);
            // 校验签名
            if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
                log.info("标识来源:{}", fromIdStr);
                log.error("签名校验 ===》 非法请求");
                // 消息签名不正确，说明不是公众平台发过来的消息
                return null;
            }
            log.info("签名校验 ===》 验证成功");
        } catch (Exception e) {
            log.info("切换通信用户:{}", fromIdStr);
        }
        // 返回echostr
        return echostr;
    }

    /**
     * @description 微信接受与回复消息，后续接入chatGpt，进行智能问答
     * @author hyn
     * @param requestBody
     * @param signature
     * @param timestamp
     * @param nonce
     * @date 2023-11-01 15:55
     * @return java.lang.String
     */
    @RequestMapping("/send/{fromIdStr}")
    public String send(@RequestBody String requestBody, @RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce,@PathVariable(name = "fromIdStr") String fromIdStr) {
        // 校验签名
        WxMpService wxService = getWxService(fromIdStr);
        RedissonClient redissonClient = redissonConfig.redissonClient();
        if (!wxService.checkSignature(timestamp, nonce, signature)) {
            log.error("签名校验 ===》 非法请求");
            // 消息签名不正确，说明不是公众平台发过来的消息
            return null;
        }
        log.info("签名校验 ===》 验证成功");
        // 解析消息体，封装为对象
        WxMpXmlMessage xmlMessage = WxMpXmlMessage.fromXml(requestBody);
        WxMpXmlOutMessage outMessage = null;
        try {
            // 将消息路由给对应的处理器，获取响应
            RBucket<Object> bucket = redissonClient.getBucket(xmlMessage.getToUser());
            bucket.set(fromIdStr);
            outMessage = wxMpMessageRouter.route(xmlMessage);
        } catch (Exception e) {
            log.error("消息路由异常", e);
        }
        // 将响应消息转换为xml格式返回
        return outMessage == null ? null : outMessage.toXml();
    }

    @GetMapping("/addRedis")
    public void addRedis(@RequestParam String key, @RequestParam String value) {
        RedissonClient redissonClient = redissonConfig.redissonClient();
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(value);
        log.info("增加后的值:{}", bucket.get());
    }

    /**
     * 获取微信service
     *
     * @param fromIdStr
     * @return
     */
    private WxMpService getWxService(String fromIdStr) {
        WxUserConfig wxUserConfig = wxUserConfigService.lambdaQuery().eq(WxUserConfig::getFormId, fromIdStr).one();
        if (wxUserConfig == null) {
            log.error("用户:{}未配置信息", fromIdStr);
            return null;
        }
        return wxMpServiceFactory.buildWxMpService(wxUserConfig.getAppId(), wxUserConfig.getAppSecret(), wxUserConfig.getToken(), wxUserConfig.getEncodingKey());
    }


    @GetMapping("/talkToDeepSeek")
    public String talkToDeepSeek(String user,String content){
        return deepSeekService.chatDeepSeek(content, user);
    }

    @Resource
    private TalkToAiServiceImpl talkToAiService;

    @GetMapping("/testTalkAi")
    public String testTalkAi(String user,String content){
        return talkToAiService.callAsyncAnswerFromAi(content,user);
    }

}
