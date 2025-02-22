package com.hyn.demos.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author hyn
 * @version 1.0.0
 * @description 阿里云配置
 * @date 2023/11/2
 */
@Component
@Data
@ConfigurationProperties(prefix = "aliyun")
public class AliyunProperties {
    private String accessKeyId;
    private String accessKeySecret;
    private String agentKey;
    private String appId;
}
