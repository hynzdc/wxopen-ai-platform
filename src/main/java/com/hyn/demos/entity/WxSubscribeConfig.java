package com.hyn.demos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 表名：wx_subscribe_config
*/
@Data
@TableName(value = "wx_subscribe_config")
public class WxSubscribeConfig implements Serializable {
    /**
     * 主键
     */
    @TableId(value="id", type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 回复文本
     */
    @TableField(value = "reply_text")
    private String replyText;

    /**
     * 微信用户名
     */
    @TableField(value = "wx_user_name")
    private String wxUserName;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;
    private static final long serialVersionUID = 1L;
}