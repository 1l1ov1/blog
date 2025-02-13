package com.blog.auth.controller;


import com.blog.auth.service.AuthService;
import com.blog.common.domain.dto.UserDTO;

import com.blog.common.domain.vo.Result;
import com.blog.common.domain.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Api(value = "认证服务")
public class AuthController {

    @Autowired
    private AuthService authService;
    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public Result Login(@Valid @RequestBody UserDTO userDTO) {
        UserVo userVo = authService.login(userDTO);
        return Result.success(userVo);
    }

    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public Result Register(@Valid @RequestBody UserDTO userDTO) {
        authService.register(userDTO);
        return Result.success(null);
    }
    @PostMapping("/user-update")
    @ApiOperation(value = "修改个人消息")
    public Result Update(@Valid @RequestBody UserDTO userDTO){
        authService.UserUpdate(userDTO);
        return Result.success(null);
    }
    
}
