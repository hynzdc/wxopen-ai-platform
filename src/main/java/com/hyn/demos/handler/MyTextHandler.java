package com.hyn.demos.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.hyn.demos.config.MyRedissonConfig;
import com.hyn.demos.entity.ShareResource;
import com.hyn.demos.service.ShareResourceService;
import com.hyn.demos.service.TalkToAiService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * @author hyn
 * @version 1.0.0
 * @description 消息处理器
 * @date 2023/11/1
 */
@Component
@Slf4j
public class MyTextHandler implements WxMpMessageHandler {
    @Resource
    TalkToAiService talkToAiService;
    @Resource
    private ShareResourceService shareResourceService;
    @Resource
    private MyRedissonConfig redissonConfig;
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> map, WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        //获取发送消息的用户
        String fromUser = wxMessage.getFromUser();
        // 接收消息内容
        String inContent = wxMessage.getContent();
        // 生成返回内容
        String contactOnlyFlag = fromUser + "/" + wxMessage.getToUser();
        String outContent = handOutContent(inContent, contactOnlyFlag);
        // 构造响应消息对象
        return WxMpXmlOutMessage.TEXT().content(outContent).fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser()).build();
    }

    /**
     * 处理输出内容
     *
     * @param inContent
     * @return
     */
    public String handOutContent(String inContent, String contactOnlyFlag) {
        String outContent;
        log.info("用户唯一标识:{}",contactOnlyFlag);
        RedissonClient redissonClient = redissonConfig.redissonClient();
        String toUser = (String)redissonClient.getBucket(contactOnlyFlag.split("/")[1]).get();
        if (StringUtils.isNotBlank(inContent)) {
            //deepSeek 模型
            //outContent = deepSeekService.chatDeepSeek(inContent, fromUser);
            //获取资源列表
            if (inContent.equals("资源列表")) {
                log.info("资源toUser:{}",toUser);
                List<ShareResource> list = shareResourceService.lambdaQuery()
                        .eq(ShareResource::getIsShow, 1)
                        .eq(ShareResource::getUserId,toUser)
                        .list();
                if (CollectionUtil.isNotEmpty(list)) {
                    outContent = formatShareResources(list);
                } else {
                    outContent = "资源列表为空";
                }
            } else {
                //先查询数据库
                ShareResource resource = shareResourceService.lambdaQuery()
                        .eq(ShareResource::getExchangeCode, inContent)
                        .eq(ShareResource::getUserId,toUser)
                        .one();
                if (resource == null) {
                    outContent = talkToAiService.talkToAi(inContent, contactOnlyFlag);
                } else {
                    outContent = resource.getResourceUrl();
                }
            }
        } else {
            outContent = "请不要发送空的内容";
        }
        return outContent;
    }

    /**
     * 格式化资源列表
     *
     * @param list
     * @return
     */
    public String formatShareResources(List<ShareResource> list) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            ShareResource resource = list.get(i);
            result.append((i + 1))
                    .append(". ")
                    .append(resource.getResourceName())
                    .append(" - 回复 ")
                    .append(resource.getExchangeCode())
                    .append(" 获取资源")
                    .append("\n\n"); // 添加一个空行（回车隔开）
        }

        return result.toString();
    }
}
