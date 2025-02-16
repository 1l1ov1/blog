package com.blog.auth.controller;


import com.blog.auth.service.AuthService;
import com.blog.auth.utils.JwtUtil;
import com.blog.common.domain.dto.UserDTO;

import com.blog.common.domain.vo.Result;
import com.blog.common.domain.vo.UserVo;
import com.blog.common.utils.RedisUtil;
import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@RequestMapping("/auth")
@Api(value = "认证服务")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private Producer kaptchaProducer;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public Result Login(@Valid @RequestBody UserDTO userDTO, HttpServletRequest request) {
        UserVo userVo = authService.login(userDTO, request);
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

    /**
     * 生成并返回验证码图片
     * 此方法通过GET请求处理，生成一个JPEG图像格式的验证码
     * 它确保验证码图片不会被缓存，并将验证码文本存储在会话中
     *
     * @param request HTTP请求对象，用于获取会话
     * @param response HTTP响应对象，用于设置响应头和输出流
     * @throws IOException 如果在处理输出流时发生I/O错误
     */
    @GetMapping(value = "/captcha", produces = MediaType.IMAGE_JPEG_VALUE)
    @ApiOperation(value = "获取验证码图片")
    public void getCaptchaImage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 禁止缓存验证码图片，以防止安全问题
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        // 设置响应内容类型为JPEG图像
        response.setContentType("image/jpeg");

        // 创建验证码文本
        String capText = kaptchaProducer.createText();
        // 创建验证码令牌
        String capTextKey = JwtUtil.generateToken(capText, 60 * 1000L);
        // 将JWT作为参数返回给前端
        response.setHeader("X-Captcha-Key", capTextKey);
        // 存储到redis中
        RedisUtil redisUtil = new RedisUtil(redisTemplate);
        redisUtil.setStringCacheValueWithExpiration(capTextKey, capText, 60);

        // 根据验证码文本创建验证码图片
        BufferedImage bi = kaptchaProducer.createImage(capText);

        // 获取响应输出流以写入图像数据
        ServletOutputStream out = response.getOutputStream();

        // 将生成的验证码图片以JPEG格式写入输出流
        ImageIO.write(bi, "jpg", out);
        // 尝试刷新输出流以确保所有数据都被写入，最后关闭输出流
        try {
            out.flush();
        } finally {
            out.close();
        }
    }
    
}
