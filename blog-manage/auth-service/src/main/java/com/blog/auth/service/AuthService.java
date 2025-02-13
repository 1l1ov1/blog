package com.blog.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.common.domain.dto.UserDTO;
import com.blog.common.domain.po.User;
import com.blog.common.domain.vo.UserVo;

public interface AuthService extends IService<User> {
    /**
     * 登录
     * @param userDTO 传递用户dto
     * @return 返回用户vo
     */
    UserVo login(UserDTO userDTO);

    void register(UserDTO user);

    void UserUpdate(UserDTO userDTO);
}
