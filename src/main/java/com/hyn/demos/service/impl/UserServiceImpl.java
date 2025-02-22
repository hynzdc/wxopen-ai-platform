package com.hyn.demos.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.hyn.demos.dto.req.*;
import com.hyn.demos.entity.ShareResource;
import com.hyn.demos.entity.WxSubscribeConfig;
import com.hyn.demos.entity.WxUser;
import com.hyn.demos.entity.WxUserConfig;
import com.hyn.demos.enums.UserCenterServiceEnum;
import com.hyn.demos.exception.ErrorCode;
import com.hyn.demos.exception.ThrowUtils;
import com.hyn.demos.service.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {
    @Resource
    private WxUserService wxUserService;
    @Resource
    private WxUserConfigService wxUserConfigService;
    @Resource
    private WxSubscribeConfigService wxSubscribeConfigService;
    @Resource
    private ShareResourceService shareResourceService;
    @Resource
    private EmailService emailService;
    @Value("${spring.mail.nickname}")
    private String nickName;
    @Value("${spring.mail.username}")
    private String from;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public String login(UserLoginReqDTO reqDTO, Model model, HttpServletRequest request) {
        try {
            ThrowUtils.throwIf(reqDTO == null, ErrorCode.PARAMS_ERROR);
            String username = reqDTO.getUsername();
            String password = reqDTO.getPassword();
            WxUser wxUser = wxUserService.lambdaQuery().eq(WxUser::getUsername, username).or().eq(WxUser::getEmail, username).one();
            ThrowUtils.throwIf(wxUser == null, ErrorCode.FORBIDDEN_ERROR, "账户不存在！");
            String wxUserPassword = wxUser.getPassword();
            ThrowUtils.throwIf(!wxUserPassword.equals(password), ErrorCode.FORBIDDEN_ERROR, "登录密码错误！");
            //记录用户登录态
            request.getSession().setAttribute(UserCenterServiceEnum.USER_LOGIN_STATE.getMsg(), wxUser);
            model.addAttribute("username", username);
            model.addAttribute("email", wxUser.getEmail());
            WxUserConfig wxUserConfig = wxUserConfigService.lambdaQuery().eq(WxUserConfig::getUserId, wxUser.getId()).one();
            if (wxUserConfig != null) {
                model.addAttribute("user",wxUser);
                model.addAttribute("config", wxUserConfig);
                return "userConfigDetail";
            } else {
                return "userConfig";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @Override
    public String register(UserRegisterReqDTO reqDTO, Model model) {
        try {
            ThrowUtils.throwIf(reqDTO == null, ErrorCode.PARAMS_ERROR);
            String username = reqDTO.getUsername();
            String email = reqDTO.getEmail();
            boolean passwordEqual  = reqDTO.getPassword().equals(reqDTO.getPasswordAgain());
            WxUser haveRecordUser = wxUserService.lambdaQuery().eq(WxUser::getUsername, username).one();
            ThrowUtils.throwIf(haveRecordUser != null, ErrorCode.PARAMS_ERROR, "用户名已存在");
            WxUser emailRecordUser = wxUserService.lambdaQuery().eq(WxUser::getEmail, email).one();
            ThrowUtils.throwIf(emailRecordUser != null, ErrorCode.PARAMS_ERROR, "该邮箱已绑定过其他用户");
            ThrowUtils.throwIf(!passwordEqual, ErrorCode.PARAMS_ERROR, "两次密码不一致，请重新输入");
            RBucket<Object> bucket = redissonClient.getBucket(reqDTO.getEmail());
            String verifyCode = (String) bucket.get();
            ThrowUtils.throwIf(verifyCode == null, ErrorCode.PARAMS_ERROR, "验证码无效或已过期，请重新获取");
            ThrowUtils.throwIf(!verifyCode.equals(reqDTO.getVerifyCode()), ErrorCode.PARAMS_ERROR, "验证码不正确");
            WxUser wxUser = new WxUser();
            wxUser.setId(IdUtil.getSnowflakeNextId());
            wxUser.setUsername(reqDTO.getUsername());
            wxUser.setPassword(reqDTO.getPassword());
            wxUser.setEmail(reqDTO.getEmail());
            wxUser.setCreationTime(new Date());
            wxUser.setModifyTime(new Date());
            wxUserService.save(wxUser);
            //异步邮件通知
            CompletableFuture.runAsync(()->{
                emailService.sendTemplateMail(Collections.singletonList(reqDTO.getEmail()), "公众号AI助手注册成功!", "registration-success", nickName, username);
                emailService.sendEmailToSomeone(from, "用户:" + wxUser.getUsername() + ",成功注册", "平台新增用户通知！");
            });
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
        return "userLogin";
    }

    @Override
    public void sendVerifyCodeToEmail(String email) {
        try {
            RBucket<Object> bucket = redissonClient.getBucket(email);
            String verifyCode = String.valueOf(RandomUtil.randomInt(100000, 999999));
            bucket.set(verifyCode, 15, TimeUnit.MINUTES);
            emailService.sendRegisterTemplateMail(Collections.singletonList(email), "WxOpenAI 验证码", "verify-success", "微信公众号AI助手", email, verifyCode);
        } catch (Exception e) {
            log.error("发送验证码邮箱异常", e);
        }
    }


    @Override
    public String settingReply(ReplySetReqDTO reqDTO, Model model, HttpServletRequest request) {
        try {
            ThrowUtils.throwIf(reqDTO == null, ErrorCode.PARAMS_ERROR, "参数不能为空");
            WxUser wxUser = (WxUser) request.getSession().getAttribute(UserCenterServiceEnum.USER_LOGIN_STATE.getMsg());
            ThrowUtils.throwIf(wxUser == null, ErrorCode.LOGIN_EXPIRED);
            WxUserConfig wxUserConfig = wxUserConfigService.lambdaQuery().eq(WxUserConfig::getUserId, wxUser.getId()).one();
            ThrowUtils.throwIf(wxUserConfig == null, ErrorCode.FORBIDDEN_ERROR, "请先填写资源配置");
            WxSubscribeConfig wxSubscribeConfig = new WxSubscribeConfig();
            WxSubscribeConfig one = wxSubscribeConfigService.lambdaQuery().eq(WxSubscribeConfig::getUserId, wxUser.getId()).one();
            wxSubscribeConfig.setId(one != null ? one.getId() : IdUtil.getSnowflakeNextId());
            wxSubscribeConfig.setReplyText(reqDTO.getReplyContent());
            wxSubscribeConfig.setWxUserName(wxUser.getUsername());
            wxSubscribeConfig.setUserId(wxUser.getId());
            wxSubscribeConfigService.saveOrUpdate(wxSubscribeConfig);
            model.addAttribute("config", wxUserConfig);
            model.addAttribute("user",wxUser);
            return "userConfigDetail";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @Override
    public String toSettingReply(Model model, HttpServletRequest request) {
        try {
            WxUser wxUser = (WxUser) request.getSession().getAttribute(UserCenterServiceEnum.USER_LOGIN_STATE.getMsg());
            ThrowUtils.throwIf(wxUser == null,  ErrorCode.LOGIN_EXPIRED);
            WxSubscribeConfig one = wxSubscribeConfigService.lambdaQuery().eq(WxSubscribeConfig::getUserId, wxUser.getId()).one();
            if (one != null) {
                model.addAttribute("replyContent", one.getReplyText());
                model.addAttribute("username", wxUser.getUsername());
            }
            return "replySet";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @Override
    public String showResourceList(Model model, HttpServletRequest request) {
        try {
            WxUser wxUser = (WxUser) request.getSession().getAttribute(UserCenterServiceEnum.USER_LOGIN_STATE.getMsg());
            ThrowUtils.throwIf(wxUser == null,  ErrorCode.LOGIN_EXPIRED);
            List<ShareResource> list = shareResourceService.lambdaQuery().eq(ShareResource::getUserId, wxUser.getUsername()).list();
            if (CollectionUtil.isNotEmpty(list)) {
                model.addAttribute("resources", list);
                model.addAttribute("username", wxUser.getUsername());
            }
            return "resource";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @Override
    public String saveResource(ShareResource shareResource, Model model, HttpServletRequest request) {
        try {
            WxUser wxUser = (WxUser) request.getSession().getAttribute(UserCenterServiceEnum.USER_LOGIN_STATE.getMsg());
            ThrowUtils.throwIf(wxUser == null,  ErrorCode.LOGIN_EXPIRED);
            WxUserConfig wxUserConfig = wxUserConfigService.lambdaQuery().eq(WxUserConfig::getUserId, wxUser.getId()).one();
            ThrowUtils.throwIf(wxUserConfig == null, ErrorCode.FORBIDDEN_ERROR, "请先填写资源配置");
            if (shareResource.getId() != null){
                //更新逻辑
                shareResource.setUserId(wxUser.getUsername());
                shareResource.setIsShow(1);
                shareResourceService.updateById(shareResource);
            }else {
                //纯保存逻辑
                shareResource.setId(IdUtil.getSnowflakeNextId());
                shareResource.setUserId(wxUser.getUsername());
                shareResource.setIsShow(1);
                shareResourceService.save(shareResource);
            }
            List<ShareResource> list = shareResourceService.lambdaQuery().eq(ShareResource::getUserId, wxUser.getUsername()).list();
            if (CollectionUtil.isNotEmpty(list)) {
                model.addAttribute("resources", list);
            }
            return "resource";
        }catch (Exception e){
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @Override
    public String updateResource(Long id, Model model, HttpServletRequest request) {
        try {
            WxUser wxUser = (WxUser) request.getSession().getAttribute(UserCenterServiceEnum.USER_LOGIN_STATE.getMsg());
            ThrowUtils.throwIf(wxUser == null,  ErrorCode.LOGIN_EXPIRED);
            ShareResource byId = shareResourceService.getById(id);
            if (byId != null) {
                model.addAttribute("resource", byId);
            }
            return "operateResource";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @Override
    public String deleteResource(Long id, Model model, HttpServletRequest request) {
        try {
            WxUser wxUser = (WxUser) request.getSession().getAttribute(UserCenterServiceEnum.USER_LOGIN_STATE.getMsg());
            ThrowUtils.throwIf(wxUser == null,  ErrorCode.LOGIN_EXPIRED);
            shareResourceService.removeById(id);
            List<ShareResource> list = shareResourceService.lambdaQuery().eq(ShareResource::getUserId, wxUser.getUsername()).list();
            if (CollectionUtil.isNotEmpty(list)) {
                model.addAttribute("resources", list);
            }
            return "resource";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @Override
    public String forgotPassword(Model model, ForgotPasswordReqDTO reqDTO) {
        try {
            WxUser user = wxUserService.lambdaQuery().eq(WxUser::getEmail, reqDTO.getEmail()).one();
            ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "该邮箱未在此平台注册过！");
            RBucket<Object> bucket = redissonClient.getBucket(reqDTO.getEmail());
            String verifyCode = (String) bucket.get();
            ThrowUtils.throwIf(verifyCode == null, ErrorCode.PARAMS_ERROR, "验证码无效或已过期，请重新获取");
            ThrowUtils.throwIf(!verifyCode.equals(reqDTO.getVerifyCode()), ErrorCode.PARAMS_ERROR, "验证码不正确");
            model.addAttribute("user", user);
            return "reSetPassword";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @Override
    public String reSetPassword(Model model, ReSetPasswordReqDTO reqDTO) {
        try {
            Long userId =Long.valueOf(reqDTO.getUserId()) ;
            ThrowUtils.throwIf(!reqDTO.getPassword().equals(reqDTO.getPasswordAgain()), ErrorCode.PARAMS_ERROR, "两次密码输入不一致");
            WxUser user = wxUserService.getById(userId);
            user.setPassword(reqDTO.getPassword());
            wxUserService.updateById(user);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @Override
    public String userLoginWithCode(UserLoginReqDTO reqDTO, Model model, HttpServletRequest request) {
        try {
            String email = reqDTO.getEmail();
            String verifyCode = reqDTO.getVerifyCode();
            WxUser wxUser = wxUserService.lambdaQuery().eq(WxUser::getEmail, email).one();
            ThrowUtils.throwIf(wxUser == null, ErrorCode.PARAMS_ERROR, "该邮箱未在此平台注册过！");
            RBucket<Object> bucket = redissonClient.getBucket(email);
            String code = (String) bucket.get();
            ThrowUtils.throwIf(code == null, ErrorCode.PARAMS_ERROR, "验证码无效或已过期，请重新获取");
            ThrowUtils.throwIf(!code.equals(verifyCode), ErrorCode.PARAMS_ERROR, "验证码不正确");
            request.getSession().setAttribute(UserCenterServiceEnum.USER_LOGIN_STATE.getMsg(), wxUser);
            model.addAttribute("username", wxUser.getUsername());
            model.addAttribute("email", wxUser.getEmail());
            WxUserConfig wxUserConfig = wxUserConfigService.lambdaQuery().eq(WxUserConfig::getUserId, wxUser.getId()).one();
            if (wxUserConfig != null) {
                model.addAttribute("user", wxUser);
                model.addAttribute("config", wxUserConfig);
                return "userConfigDetail";
            } else {
                return "userConfig";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

}
