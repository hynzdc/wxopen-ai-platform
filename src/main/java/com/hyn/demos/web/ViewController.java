package com.hyn.demos.web;

import com.alibaba.fastjson.JSONObject;
import com.hyn.demos.dto.req.*;
import com.hyn.demos.entity.ShareResource;
import com.hyn.demos.entity.WxUser;
import com.hyn.demos.enums.UserCenterServiceEnum;
import com.hyn.demos.exception.ErrorCode;
import com.hyn.demos.exception.ThrowUtils;
import com.hyn.demos.service.IDeepSeekService;
import com.hyn.demos.service.IUserService;
import com.hyn.demos.service.WxUserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @author hyn
 * @version 1.0.0
 * @description
 * @date 2025/2/14
 */
@Controller
@Slf4j
@RequestMapping("/view")
public class ViewController {
    @Resource
    private WxUserConfigService wxUserConfigService;
    @Resource
    private IUserService userService;
    @Resource
    private IDeepSeekService deepSeekService;
    // 跳转白名单
    private static final List<String> targetWhiteList = Arrays.asList("forgotPassword", "readme", "userRegister", "error","reSetPassword","userLogin");

    @PostMapping("/userLogin")
    public String userLogin(UserLoginReqDTO reqDTO, Model model, HttpServletRequest request) {
        return userService.login(reqDTO, model, request);
    }

    @PostMapping("/userLoginWithCode")
    public String userLoginWithCode(UserLoginReqDTO reqDTO, Model model, HttpServletRequest request){
        return userService.userLoginWithCode(reqDTO, model, request);
    }

    @PostMapping("/settingReply")
    public String settingReply(ReplySetReqDTO reqDTO, Model model, HttpServletRequest request) {
        return userService.settingReply(reqDTO, model, request);
    }

    @GetMapping("/toSettingReply")
    public String toSettingReply(Model model, HttpServletRequest request) {
        return userService.toSettingReply(model, request);
    }

    @GetMapping("/modifyUserConfig")
    public String modifyUserConfig(Model model, HttpServletRequest request) {
        return wxUserConfigService.modifyUserConfig(model,request);
    }

    @GetMapping("/target/{target}")
    public String toTargetHtml(@PathVariable(name = "target") String target, HttpServletRequest request, Model model) {
        try {
            // 判断是否登录(如果未登录，则跳转到登录页面
            WxUser wxUser = (WxUser) request.getSession().getAttribute(UserCenterServiceEnum.USER_LOGIN_STATE.getMsg());
            if (wxUser == null && targetWhiteList.contains(target)) {
                return target;
            }
            ThrowUtils.throwIf(wxUser == null && !targetWhiteList.contains(target), ErrorCode.FORBIDDEN_ERROR, "请先登录");
            if (wxUser != null) {
                request.setAttribute("username", wxUser.getUsername());
                return target;
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
        return target;
    }

    @PostMapping("/saveOrUpdateWxUserConfig")
    public String saveOrUpdateWxUserConfig(WxUserConfigReqDTO reqDTO, Model model,HttpServletRequest request) {
        log.info("用户提交的微信config:{}", JSONObject.toJSONString(reqDTO));
        return wxUserConfigService.saveOrUpdateWxUserConfig(reqDTO,model,request);
    }

    @PostMapping("/userRegister")
    public String userRegister(UserRegisterReqDTO reqDTO,Model model){
        return userService.register(reqDTO,model);
    }

    @GetMapping("/showResourceList")
    public String showResourceList(Model model,HttpServletRequest request){
        return userService.showResourceList(model,request);
    }

    @PostMapping("/saveResource")
    public String saveResource(ShareResource shareResource, Model model, HttpServletRequest request) {
        return userService.saveResource(shareResource,model, request);
    }

    @GetMapping("/updateResource/{id}")
    public String updateResource(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        return userService.updateResource(id, model, request);
    }

    @GetMapping("/deleteResource/{id}")
    public String deleteResource(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        return userService.deleteResource(id, model, request);
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute(UserCenterServiceEnum.USER_LOGIN_STATE.getMsg());
        return "redirect:/";
    }

    @GetMapping("/sendVerifyCodeToEmail/{email}")
    public void sendVerifyCodeToEmail(@PathVariable("email") String email){
        userService.sendVerifyCodeToEmail(email);
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(Model model, ForgotPasswordReqDTO reqDTO){
        return userService.forgotPassword(model,reqDTO);
    }

    @PostMapping("/reSetPassword")
    public String reSetPassword(Model model,ReSetPasswordReqDTO reqDTO){
        return userService.reSetPassword(model,reqDTO);
    }

    @GetMapping("/chatDeepSeek")
    public String chatDeepSeek(Model model, HttpServletRequest request){
        return deepSeekService.chatDeepSeekView(model,request);
    }
}