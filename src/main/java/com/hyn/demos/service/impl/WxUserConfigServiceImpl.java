/**
* @mbg.generated
* generator on Thu Feb 13 21:48:51 CST 2025
*/
package com.hyn.demos.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyn.demos.dto.req.WxUserConfigReqDTO;
import com.hyn.demos.entity.WxUser;
import com.hyn.demos.entity.WxUserConfig;
import com.hyn.demos.enums.UserCenterServiceEnum;
import com.hyn.demos.exception.ErrorCode;
import com.hyn.demos.exception.ThrowUtils;
import com.hyn.demos.mapper.WxUserConfigMapper;
import com.hyn.demos.service.WxUserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
@Slf4j
public class WxUserConfigServiceImpl extends ServiceImpl<WxUserConfigMapper, WxUserConfig> implements WxUserConfigService {
    private final static String URL_RESOURCES = "http://1.94.225.102/wx/send/%s";

    @Override
    public String saveOrUpdateWxUserConfig(WxUserConfigReqDTO reqDTO, Model model, HttpServletRequest request) {
        try {
            WxUser wxUser = (WxUser) request.getSession().getAttribute(UserCenterServiceEnum.USER_LOGIN_STATE.getMsg());
            ThrowUtils.throwIf(wxUser == null,  ErrorCode.LOGIN_EXPIRED);
            Long userId = wxUser.getId();
            WxUserConfig one = this.lambdaQuery().eq(WxUserConfig::getUserId, userId).one();
            WxUserConfig wxUserConfig = new WxUserConfig();
            wxUserConfig.setId(one != null ? one.getId() : IdUtil.getSnowflakeNextId());
            wxUserConfig.setFormId(wxUser.getUsername());
            wxUserConfig.setWxOpenName(reqDTO.getOfficialAccount());
            wxUserConfig.setUserEmail(wxUser.getEmail());
            wxUserConfig.setAppId(reqDTO.getAppId());
            wxUserConfig.setAppSecret(reqDTO.getAppSecret());
            wxUserConfig.setEncodingKey(reqDTO.getEncodingAESKey());
            wxUserConfig.setCreationTime(new Date());
            wxUserConfig.setModifyTime(new Date());
            wxUserConfig.setToken(reqDTO.getToken());
            wxUserConfig.setUrl(String.format(URL_RESOURCES, wxUser.getUsername()));
            wxUserConfig.setUserId(userId);
            model.addAttribute("config", wxUserConfig);
            model.addAttribute("user",wxUser);
            model.addAttribute("username",wxUser.getUsername());
            this.saveOrUpdate(wxUserConfig);
            return "userConfigDetail";
        } catch (Exception e) {
            log.error("保存用户资源信息发生异常", e);
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @Override
    public String modifyUserConfig(Model model, HttpServletRequest request) {
        try {
            WxUser wxUser = (WxUser) request.getSession().getAttribute(UserCenterServiceEnum.USER_LOGIN_STATE.getMsg());
            ThrowUtils.throwIf(wxUser == null,  ErrorCode.LOGIN_EXPIRED);
            Long userId = wxUser.getId();
            WxUserConfig config = this.lambdaQuery().eq(WxUserConfig::getUserId, userId).one();
            model.addAttribute("username", wxUser.getUsername());
            model.addAttribute("email", wxUser.getEmail());
            model.addAttribute("config", config);
            return "userConfig";
        } catch (Exception e) {
            log.error("编辑用户公众号信息发成异常", e);
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}