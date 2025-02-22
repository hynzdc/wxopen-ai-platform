/**
* @mbg.generated
* generator on Thu Feb 13 21:48:51 CST 2025
*/
package com.hyn.demos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyn.demos.dto.req.WxUserConfigReqDTO;
import com.hyn.demos.entity.WxUserConfig;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

public interface WxUserConfigService extends IService<WxUserConfig> {
    /**
     * 保存或者更新微信公众号配置信息
     * @param reqDTO
     * @param model
     * @param request
     * @return
     */
    String saveOrUpdateWxUserConfig(WxUserConfigReqDTO reqDTO, Model model, HttpServletRequest request);

    /**
     * 编辑信息
     * @param model
     * @param request
     * @return
     */
    String modifyUserConfig(Model model, HttpServletRequest request);
}