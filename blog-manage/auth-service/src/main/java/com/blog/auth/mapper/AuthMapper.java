package com.blog.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.common.domain.po.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper extends BaseMapper<User> {
}
