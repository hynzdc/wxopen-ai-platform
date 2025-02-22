package com.hyn.demos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 表名：wx_user_config
*/
@Data
@TableName(value = "wx_user_config")
public class WxUserConfig implements Serializable {
    /**
     * 主键
     */
    @TableId(value="id", type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 用户标识来源id
     */
    @TableField(value = "form_id")
    private String formId;

    /**
     * 微信公众号名称
     */
    @TableField(value = "wx_open_name")
    private String wxOpenName;

    /**
     * 用户邮箱
     */
    @TableField(value = "user_email")
    private String userEmail;

    /**
     * 开发者id
     */
    @TableField(value = "app_id")
    private String appId;

    /**
     * 开发者密钥
     */
    @TableField(value = "app_secret")
    private String appSecret;

    /**
     * 消息加密解密密钥
     */
    @TableField(value = "encoding_key")
    private String encodingKey;

    /**
     * 创建时间
     */
    @TableField(value = "creation_time")
    private Date creationTime;

    /**
     * 修改时间
     */
    @TableField(value = "modify_time")
    private Date modifyTime;

    /**
     * token
     */
    @TableField(value = "token")
    private String token;

    /**
     * 服务器地址
     */
    @TableField(value = "url")
    private String url;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    private static final long serialVersionUID = 1L;
}