package com.blog.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.common.domain.dto.UserDTO;
import com.blog.common.domain.po.User;
import com.blog.common.domain.vo.UserVo;

import javax.servlet.http.HttpServletRequest;

public interface AuthService extends IService<User> {
    /**
     * 登录
     * @param userDTO 传递用户dto
     * @param request 请求
     * @return 返回用户vo
     */
    UserVo login(UserDTO userDTO, HttpServletRequest request);

    void register(UserDTO userDTO);

    void UserUpdate(UserDTO userDTO);
}
