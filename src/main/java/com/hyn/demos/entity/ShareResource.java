package com.hyn.demos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 表名：share_resource
 */
@Data
@TableName(value = "share_resource")
public class ShareResource implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 交换code
     */
    @TableField(value = "exchange_code")
    private String exchangeCode;

    /**
     * 资源地址
     */
    @TableField(value = "resource_url")
    private String resourceUrl;

    /**
     * 资源名称
     */
    @TableField(value = "resource_name")
    private String resourceName;

    /**
     * 是否展示 0 不展示 1 展示
     */
    @TableField(value = "is_show")
    private Integer isShow;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private String userId;

    private static final long serialVersionUID = 1L;
}