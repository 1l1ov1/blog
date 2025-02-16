package com.blog.auth.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.nacos.common.utils.MD5Utils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.auth.enums.AccountStatus;
import com.blog.auth.enums.LoginType;
import com.blog.auth.mapper.AuthMapper;
import com.blog.auth.service.AuthService;
import com.blog.auth.utils.BCryptPasswordEncoder;
import com.blog.auth.utils.JwtUtil;
import com.blog.common.enums.ErrorCode;
import com.blog.common.exception.UserException;
import com.blog.common.domain.dto.UserDTO;
import com.blog.common.domain.po.User;
import com.blog.common.domain.vo.UserVo;
import com.blog.common.utils.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Service
public class AuthServiceImpl extends ServiceImpl<AuthMapper, User> implements AuthService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public UserVo login(UserDTO userDTO, HttpServletRequest request) {
        // 1. 校验对象是否为空以及账号密码是否为空
        if (checkUserDTO(userDTO)) {
            throw new UserException(ErrorCode.ARGUMENT_IS_NULL);
        }

        // 2. 根据账号或手机号查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername())
                .or()
                .eq(User::getPhone, userDTO.getPhone());

        User user = getOne(queryWrapper);
        // 3.1 如果用户不存在，抛出异常
        if (user == null) {
            throw new UserException(ErrorCode.USER_NOT_EXIST);
        }

        // 3.2 验证密码是否匹配
        if (!BCryptPasswordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new UserException(ErrorCode.USER_PASSWORD_ERROR);
        }

        // 如果密码正确，判断登录方式是否为用户名
        if (LoginType.USERNAME.getType().equals(userDTO.getLoginType())) {
            // 如果是，就检查验证码
            RedisUtil redisUtil = new RedisUtil(redisTemplate);
            String sessionId =  request.getSession().getId();
            String captcha = (String) redisUtil.getStringCacheValue(sessionId);
            if (captcha == null) {
                throw new UserException(ErrorCode.CAPTCHA_EXPIRE);
            }
            // 如果说验证码没有过期
            if (!captcha.equals(userDTO.getCaptcha())) {
                throw new UserException(ErrorCode.CAPTCHA_ERROR);
            }
        } else if (LoginType.PHONE.getType().equals(userDTO.getLoginType())) {
            // 如果说是手机号登录

        }

        // 3.3 生成token并设置到UserVo
        String token = JwtUtil.generateToken(String.valueOf(user.getId()));
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        userVo.setToken(token);

        return userVo;
    }

    @Override
    public void register(UserDTO userDTO) {
        // 1. 注册角色，输入该有手机，邮箱，账号，密码，二次密码

        // 2. 检查输入的是否为空，检查该账号、手机、邮箱是否已经注册过
        if (checkUserDTO(userDTO)) {
            throw new UserException(ErrorCode.USER_PASSWORD_ERROR);
        }

        // 2.1 如果输入为空或者已经注册，抛出异常
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername())
                .or()
                .eq(User::getPhone, userDTO.getPhone())
                .or()
                .eq(User::getEmail, userDTO.getEmail());

        User user = getOne(queryWrapper);
        // 3.1 如果用户已存在，抛出异常
        if (user != null) {
            throw new UserException(ErrorCode.INVALID_USER_ID);
        }

        user = new User();
        BeanUtils.copyProperties(userDTO, user);

        // 4. 加密密码
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // 5. 修改这个随机的昵称
        String uuid = UUID.randomUUID().toString();
        String nickName = uuid.replace("-", "").substring(0, 15);
        user.setNickName(nickName);

        // 6. 将得到的user传入数据库
        save(user);
    }

    /**
     * 检查userDTO是否为空还有账号密码为空
     *
     * @param userDTO userDTO
     * @return 返回是否为空 空为true 不空为false
     */
    private boolean checkUserDTO(UserDTO userDTO) {
        // 如果userDTO为空
        if (ObjectUtil.isEmpty(userDTO)) {
            return true;
        }

        // 如果不空，检查账号和密码
        String username = userDTO.getUsername();
        String phone = userDTO.getPhone();
        String password = userDTO.getPassword();

        return isNullOrEmpty(username) || isNullOrEmpty(phone) || isNullOrEmpty(password);
    }

    /**
     * 检查字符串是否为空字符串或者为null
     *
     * @param str 要判断的字符串
     * @return 是否为空字符串或者null
     */
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    @Override
    public void UserUpdate(UserDTO userDTO) {
        // 1. 通过传过来的user_id找到那个人
        if (userDTO == null || userDTO.getId() == null) {
            throw new UserException(ErrorCode.INVALID_USER_ID);
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, userDTO.getId());
        User user = getOne(queryWrapper);

        // 2. 如果用户不存在，抛出异常
        if (user == null) {
            throw new UserException(ErrorCode.USER_NOT_EXIST);
        }

        // 3. 将得到的新user给到通过id给到那个修改的那个人
        BeanUtils.copyProperties(userDTO, user);
        save(user);
    }



}
