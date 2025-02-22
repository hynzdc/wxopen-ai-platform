package com.hyn.demos.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hyn.demos.entity.WxUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WxUserMapper extends BaseMapper<WxUser> {
}