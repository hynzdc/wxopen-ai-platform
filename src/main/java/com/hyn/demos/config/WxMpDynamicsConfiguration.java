package com.hyn.demos.config;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class WxMpDynamicsConfiguration {
    /**
     * 微信配置工厂（静态方法版）
     */
    public static class WxMpConfigFactory {

        public static WxMpConfigStorage createConfigStorage(String appId,
                                                            String secret,
                                                            String token,
                                                            String aesKey) {
            WxMpDefaultConfigImpl configStorage = new WxMpDefaultConfigImpl();
            configStorage.setAppId(appId);
            configStorage.setSecret(secret);
            configStorage.setToken(token);
            configStorage.setAesKey(aesKey);
            return configStorage;
        }

        public static WxMpService createWxMpService(WxMpConfigStorage configStorage) {
            WxMpService wxMpService = new WxMpServiceImpl();
            wxMpService.setWxMpConfigStorage(configStorage);
            return wxMpService;
        }

        // 链式调用快捷方法
        public static WxMpService createWxMpService(String appId,
                                                    String secret,
                                                    String token,
                                                    String aesKey) {
            return createWxMpService(createConfigStorage(appId, secret, token, aesKey));
        }
    }

    /**
     * 非静态工厂版本（可配合Spring容器使用）
     */
    @Component
    public static class WxMpServiceFactory {

        public WxMpConfigStorage buildConfigStorage(String appId,
                                                    String secret,
                                                    String token,
                                                    String aesKey) {
            return WxMpConfigFactory.createConfigStorage(appId, secret, token, aesKey);
        }

        public WxMpService buildWxMpService(String appId,
                                            String secret,
                                            String token,
                                            String aesKey) {
            return WxMpConfigFactory.createWxMpService(appId, secret, token, aesKey);
        }
    }
}
