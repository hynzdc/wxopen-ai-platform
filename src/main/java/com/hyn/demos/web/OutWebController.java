package com.hyn.demos.web;

import com.hyn.demos.entity.WxUser;
import com.hyn.demos.entity.WxUserConfig;
import com.hyn.demos.enums.UserCenterServiceEnum;
import com.hyn.demos.service.WxUserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class OutWebController {
    @Resource
    private WxUserConfigService wxUserConfigService;

    @GetMapping("")
    public String login(Model model, HttpServletRequest request) {
        WxUser wxUser = (WxUser) request.getSession().getAttribute(UserCenterServiceEnum.USER_LOGIN_STATE.getMsg());
        if (wxUser != null) {
            WxUserConfig config = wxUserConfigService.lambdaQuery().eq(WxUserConfig::getUserId, wxUser.getId()).one();
            model.addAttribute("username", wxUser.getUsername());
            model.addAttribute("email", wxUser.getEmail());
            model.addAttribute("user", wxUser);
            model.addAttribute("config", config);
            return config != null && StringUtils.isNotBlank(config.getUrl()) ? "userConfigDetail" : "userConfig";
        } else {
            return "userLogin";
        }
    }
}
