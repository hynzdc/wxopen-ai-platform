package com.hyn.demos.service;

import com.hyn.demos.dto.req.*;
import com.hyn.demos.entity.ShareResource;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

public interface IUserService {
    /**
     * 登录
     *
     * @param reqDTO
     * @param model
     * @param request
     * @return
     */
    String login(UserLoginReqDTO reqDTO, Model model, HttpServletRequest request);

    /**
     * @param reqDTO
     * @param model
     * @return
     */
    String register(UserRegisterReqDTO reqDTO, Model model);

    /**
     * 发送验证码到邮箱
     *
     * @param email
     */
    void sendVerifyCodeToEmail(String email);

    /**
     * 设置自动回复内容
     * @param reqDTO
     * @param model
     * @param request
     * @return
     */
    String settingReply(ReplySetReqDTO reqDTO, Model model, HttpServletRequest request);

    /**
     * 缓存的设置内容
     * @param model
     * @param request
     * @return
     */
    String toSettingReply(Model model, HttpServletRequest request);

    /**
     * 获取用户资源列表
     * @param model
     * @param request
     * @return
     */
    String showResourceList(Model model, HttpServletRequest request);

    /**
     * 保存更新资源列表
     *
     * @param shareResource
     * @param model
     * @param request
     * @return
     */
    String saveResource(ShareResource shareResource, Model model, HttpServletRequest request);

    /**
     * 更新资源列表
     * @param id
     * @param model
     * @param request
     * @return
     */
    String updateResource(Long id, Model model, HttpServletRequest request);

    /**
     * 删除资源
     * @param id
     * @param model
     * @param request
     * @return
     */
    String deleteResource(Long id, Model model, HttpServletRequest request);

    /**
     * 忘记密码
     * @param model
     * @param reqDTO
     * @return
     */
    String forgotPassword(Model model, ForgotPasswordReqDTO reqDTO);

    /**
     * 重置密码
     *
     * @param model
     * @param reqDTO
     * @return
     */
    String reSetPassword(Model model, ReSetPasswordReqDTO reqDTO);

    /**
     * 验证码登录
     * @param reqDTO
     * @param model
     * @param request
     * @return
     */
    String userLoginWithCode(UserLoginReqDTO reqDTO, Model model, HttpServletRequest request);
}
