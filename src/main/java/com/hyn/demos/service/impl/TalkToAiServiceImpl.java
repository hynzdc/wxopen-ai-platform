package com.hyn.demos.service.impl;

import com.aliyun.broadscope.bailian.sdk.AccessTokenClient;
import com.aliyun.broadscope.bailian.sdk.ApplicationClient;
import com.aliyun.broadscope.bailian.sdk.models.BaiLianConfig;
import com.aliyun.broadscope.bailian.sdk.models.CompletionsRequest;
import com.aliyun.broadscope.bailian.sdk.models.CompletionsResponse;
import com.hyn.demos.config.MyRedissonConfig;
import com.hyn.demos.properties.AliyunProperties;
import com.hyn.demos.service.TalkToAiService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @author hyn
 * @version 1.0.0
 * @description
 * @date 2023/11/6
 */
@Service
@Slf4j
public class TalkToAiServiceImpl implements TalkToAiService {
    private static final String AI_TOKEN = "AI_TOKEN";
    @Resource
    private MyRedissonConfig redissonConfig;
    @Resource
    private AliyunProperties aliyunProperties;
    private static final String outTimeAnswer = "正在调度资源思考你的问题，你的号码牌是:%s，稍等几秒，回复这个号码牌领取答案！";

    @SneakyThrows
    @Override
    public String talkToAi(String inContent, String contactOnlyFlag) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("用户:{}提出的问题:{}", contactOnlyFlag, inContent);
        RedissonClient redissonClient = redissonConfig.redissonClient();
        String outContent;
        RLock lock = redissonClient.getLock(contactOnlyFlag);
        boolean isLock = lock.tryLock(2, 30, TimeUnit.SECONDS);
        try {
            if (isLock) {
                //outContent = getAnswerFromAi(inContent, fromUser);
                outContent = callAsyncAnswerFromAi(inContent,contactOnlyFlag);
                log.info("ai数据:{}", outContent);
                //数据入库
                //extracted(fromUser, inContent, outContent);
                stopWatch.stop();
                log.info("回复用时:{}s,微信回复的消息:{}", stopWatch.getTotalTimeSeconds(), outContent);
                outContent = filterOutText(outContent);
            } else {
                outContent = "问的太快啦！！！，让我一个一个回答！";
            }
        } finally {
            //释放锁资源 考虑失败的情况,否则这里会报异常
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return outContent;
    }


    /**
     * 针对微信公众号超时5s无法做出应答的方案，采用线程等待机制处理这个问题
     * @param inContent
     * @param contactOnlyFlag
     * @return
     */
    public String callAsyncAnswerFromAi(String inContent, String contactOnlyFlag) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        String tokenNum = generateRandomFourDigitNumber();
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            // Simulate a long-running task (e.g., 5 seconds)
            RedissonClient redissonClient = redissonConfig.redissonClient();
            String answer = (String) redissonClient.getBucket(contactOnlyFlag + ":" + inContent).get();
            if (StringUtils.isNotBlank(answer)) {
                return answer;
            } else {
                String inContentMore  = inContent + "，回答要抓住重点讲，不需要太长";
                RBucket<Object> bucket = redissonClient.getBucket(contactOnlyFlag + ":" + tokenNum);
                String answerFromAi = getAnswerFromAi(inContentMore, contactOnlyFlag);
                bucket.set(answerFromAi, 1, TimeUnit.HOURS);
                return answerFromAi;
            }
        }, executor);
        try {
            // Wait for the result with a timeout of 3 seconds
            return future.get(4800, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            return String.format(outTimeAnswer, tokenNum); // Return "hello" if timeout occurs
        } catch (InterruptedException | ExecutionException e) {
            return String.format(outTimeAnswer, tokenNum); // Return "hello" in case of an exception
        } finally {
            executor.shutdown(); // Shutdown the executor
        }
    }

    private String getAnswerFromAi(String inContent, String fromUser) {
        BaiLianConfig config = new BaiLianConfig().setApiKey(getToken());
        CompletionsRequest request = new CompletionsRequest()
                .setAppId(aliyunProperties.getAppId())
                .setPrompt(inContent)
                .setSessionId(fromUser);
        ApplicationClient client = new ApplicationClient(config);
        CompletionsResponse response = client.completions(request);
        return response.getData().getText();
    }

    /**
     * 令牌生成
     */
    public String generateRandomFourDigitNumber() {
        Random random = new Random();
        // 生成一个1000到9999之间的随机数
        return String.valueOf(1000 + random.nextInt(9000));
    }

    /**
     * @return java.lang.String
     * @description 获取aiToken
     * @author hyn
     * @date 2023-11-04 13:02
     */
    public String getToken() {
        RedissonClient redissonClient = redissonConfig.redissonClient();
        RBucket<Object> token = redissonClient.getBucket(AI_TOKEN);
        String aiToken = (String) token.get();
        return aiToken == null ? refreshToken() : aiToken;
    }

    /**
     * @return java.lang.String
     * @description 刷新token
     * @author hyn
     * @date 2023-11-04 13:02
     */
    public String refreshToken() {
        RedissonClient redissonClient = redissonConfig.redissonClient();
        RBucket<Object> token = redissonClient.getBucket(AI_TOKEN);
        AccessTokenClient accessTokenClient = new AccessTokenClient(aliyunProperties.getAccessKeyId(), aliyunProperties.getAccessKeySecret(), aliyunProperties.getAgentKey());
        String tokenAI = accessTokenClient.getToken();
        token.set(tokenAI, 23, TimeUnit.HOURS);
        return (String) token.get();
    }

    /**
     * @param text
     * @return java.lang.String
     * @description 过滤回车字符
     * @author hyn
     * @date 2023-11-04 13:02
     */
    public String filterOutText(String text) {
        return text.replaceAll("[*#-]", "");
    }
}
