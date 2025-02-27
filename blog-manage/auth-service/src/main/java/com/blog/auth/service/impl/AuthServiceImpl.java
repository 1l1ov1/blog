package com.blog.auth.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.auth.enums.LoginRelationConstants;
import com.blog.auth.enums.InputType;
import com.blog.auth.mapper.AuthMapper;
import com.blog.auth.service.AuthService;
import com.blog.auth.utils.BCryptPasswordEncoder;
import com.blog.auth.utils.JwtUtil;
import com.blog.auth.utils.ValidatorUtil;
import com.blog.common.enums.ErrorCode;
import com.blog.common.exception.UserException;
import com.blog.common.domain.dto.UserDTO;
import com.blog.common.domain.po.User;
import com.blog.common.domain.vo.UserVo;
import com.blog.common.utils.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AuthServiceImpl extends ServiceImpl<AuthMapper, User> implements AuthService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private AuthMapper authMapper;
    @Override
    public UserVo login(UserDTO userDTO, HttpServletRequest request) {
        // 然后检查验证码
        String captchaKey = request.getHeader(LoginRelationConstants.CAPTCHA_HEADER);
        // 1. 检查用户输入是否正确符合格式
        validatorUserLoginInput(userDTO, captchaKey);

        User user = getUser(userDTO);
        if (ObjectUtil.isNull(user)) {
            throw new UserException(ErrorCode.USER_NOT_EXIST);
        }
        // TODO 还要区分是用户名还是手机号，因为手机号是要验证码的
        if (!BCryptPasswordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new UserException(ErrorCode.USER_PASSWORD_ERROR);
        }

        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        String token = JwtUtil.generateToken(String.valueOf(user.getId()));
        userVo.setToken(token);
        // TODO 返回角色、权限
        // 如果说相等
        return userVo;
    }

    /**
     * 用户登录输入参数校验函数
     *
     * @param userDTO    用户数据传输对象，包含登录类型及对应凭证信息（用户名/密码/验证码/手机号等）
     * @param captchaKey 验证码在缓存中的键名，用于校验验证码时从缓存获取对应值
     * @return boolean 固定返回true（当前实现存在矛盾，建议后续优化返回值设计）
     * @throws UserException 当userDTO参数为空时抛出参数空异常
     */
    private boolean validatorUserLoginInput(UserDTO userDTO, String captchaKey) {
        // 基础参数空校验
        if (ObjectUtil.isNull(userDTO)) {
            throw new UserException(ErrorCode.ARGUMENT_IS_NULL);
        }

        // 根据登录类型进行分支校验
        String loginType = userDTO.getInputType();
        if (InputType.USERNAME.getType().equals(loginType)) {
            // 用户名登录方式校验流程
            String username = userDTO.getUsername();
            String password = userDTO.getPassword();
            String captcha = userDTO.getCaptcha();

            // 链式校验用户名、密码、验证码
            ValidatorUtil.validateUsername(username);
            ValidatorUtil.validatePassword(password);
            ValidatorUtil.validateCaptcha(captcha, captchaKey, redisUtil);

        } else if (InputType.PHONE.getType().equals(loginType)) {
            // TODO 手机号登录校验待实现
            String phone = userDTO.getPhone();
        }

        return true;
    }

    private User getUser(UserDTO userDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername())
                .or()
                .eq(User::getPhone, userDTO.getPhone());
        return getOne(queryWrapper);
    }

    @Override
    public void register(UserDTO userDTO) {
        validateUserRegisterInput(userDTO);

        if (InputType.USERNAME.getType().equals(userDTO.getInputType())) {
            handleUsernameRegistration(userDTO);
        } else if (InputType.PHONE.getType().equals(userDTO.getInputType())) {
            handlePhoneRegistration(userDTO);
        }
    }

    private void handleUsernameRegistration(UserDTO userDTO) {
        User user = getUser(userDTO);
        if (user != null) {
            throw new UserException(ErrorCode.USER_EXIST);
        }
        createNewUser(userDTO);
    }

    private void handlePhoneRegistration(UserDTO userDTO) {
        User user = getUser(userDTO);
        if (user != null) {
            throw new UserException(ErrorCode.USER_EXIST);
        }
        // createNewUser(userDTO);
    }

    /**
     * 创建新用户并保存到数据库
     *
     * 方法执行流程：
     * 1. 将UserDTO对象属性拷贝到User实体
     * 2. 对密码进行加密处理
     * 3. 生成唯一昵称
     * 4. 持久化用户对象
     *
     * @param userDTO 用户数据传输对象，包含用户注册信息（需包含非空密码字段）
     */
    private void createNewUser(UserDTO userDTO) {
        // 转换DTO到实体对象
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        // 使用BCrypt算法加密原始密码（会覆盖DTO中的原始密码值）
        user.setPassword(BCryptPasswordEncoder.encode(user.getPassword()));
        String nickName;
        // 生成系统唯一的默认昵称
        do {
            nickName = generateUniqueNickname();
        } while (checkNicknameExists(nickName));
        user.setNickName(nickName);
        // TODO 设置角色、权限
        // 持久化用户实体到数据库
        save(user);
    }


    private static final String NICKNAME_PREFIX = "Zw_";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final String RANDOM_CHAR_POOL = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final int RANDOM_LENGTH = 8;
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private static final int COUNTER_MAX = 9999;

    /**
     * 生成唯一的用户昵称字符串
     *
     * 本函数通过多组件拼接方式构造全局唯一昵称，组件包含（按顺序）：
     * 1. 固定前缀：由类常量NICKNAME_PREFIX定义
     * 2. 高精度时间戳：格式为yyyyMMddHHmmssSSS的17位字符串（精确到豪秒级）
     * 3. 循环序列号：线程安全的原子计数器，格式化为4位数字（0000-9999循环）
     * 4. 随机字符后缀：从预设字符池RANDOM_CHAR_POOL中选取指定长度RANDOM_LENGTH的随机字符
     *
     * 实现特性：
     * - 序列号计数器超过COUNTER_MAX（建议9999）时自动归零重置，确保长期运行的稳定性
     * - 使用String.format("%04d")强制4位数字补零，避免短位数场景
     * - 通过RandomUtil工具类生成密码学安全的随机字符串
     *
     * @return 结构稳定的唯一昵称字符串，总长度计算公式：
     *         NICKNAME_PREFIX长度 + 14（时间戳） + 4（序列号） + RANDOM_LENGTH（随机串）
     */
    private String generateUniqueNickname() {
        // 初始化精确容量的StringBuilder（总长度31 = 前缀长度 + 时间戳14 + 序列号4 + 随机5）

        /* 组件构建流程（总长度计算示例）：
         * - NICKNAME_PREFIX: 假设长度8（如"User_"）
         * - 时间戳: 固定14位（yyyyMMddHHmmss）
         * - 序列号: 固定4位数字
         * - 随机字符串: 假设长度5
         */

        return NICKNAME_PREFIX +
                LocalDateTime.now().format(TIMESTAMP_FORMATTER) +  // 时间戳组件（代码中实际精确到秒）
                String.format("%04d", COUNTER.getAndUpdate(prev ->  // 原子操作保证多线程环境序列号唯一性
                        (prev >= COUNTER_MAX) ? 0 : prev + 1)) +   // 循环计数逻辑，避免数值溢出
                RandomUtil.randomString(RANDOM_CHAR_POOL, RANDOM_LENGTH);
    }

    /**
     * 检查昵称是否存在于数据库中
     * @param nickname 随机生成的昵称
     * @return boolean 存在返回true，不存在返回false
     */
    private boolean checkNicknameExists(String nickname) {
        return authMapper.exists(new LambdaQueryWrapper<User>()
                .eq(User::getNickName, nickname));
    }

    /**
     * 验证用户注册输入参数合法性
     *
     * @param userDTO 用户数据传输对象，包含注册所需的各种字段
     * @return boolean 验证通过返回true，不通过会直接抛出异常
     * @throws UserException 当输入参数为null时抛出
     */
    private boolean validateUserRegisterInput(UserDTO userDTO) {
        if (ObjectUtil.isNull(userDTO)) {
            throw new UserException(ErrorCode.ARGUMENT_IS_NULL);
        }

        // 根据输入类型进行分支验证
        String inputType = userDTO.getInputType();
        if (InputType.USERNAME.getType().equals(inputType)) {
            // 用户名注册方式验证逻辑
            String username = userDTO.getUsername();
            String password = userDTO.getPassword();
            String rePassword = userDTO.getRePassword();

            // 执行字段格式及一致性验证
            ValidatorUtil.validateUsername(username);
            ValidatorUtil.validatePassword(password);
            ValidatorUtil.validatePassword(rePassword);
            ValidatorUtil.validateEquals(password, rePassword);
        } else if (InputType.PHONE.getType().equals(inputType)) {
            // TODO 校验手机
            // 手机注册验证逻辑（待实现）
            String phone = userDTO.getPhone();
        }

        return true;
    }

    @Override
    public void UserUpdate(UserDTO userDTO) {

    }


    /* @Override
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
            String captchaToken = request.getHeader(CaptchaHeader.captchaHeader);
            String captcha = (String) redisUtil.getStringCacheValue(captchaToken);
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

    *//**
     * 检查userDTO是否为空还有账号密码为空
     *
     * @param userDTO userDTO
     * @return 返回是否为空 空为true 不空为false
     *//*
    private boolean checkUserDTO(UserDTO userDTO) {
        // 如果userDTO为空
        if (ObjectUtil.isEmpty(userDTO)) {
            return true;
        }
        String loginType = userDTO.getLoginType();
        if (LoginType.USERNAME.getType().equals(loginType)) {
            return isNullOrEmpty(userDTO.getUsername()) || isNullOrEmpty(userDTO.getPassword()) || isNullOrEmpty(userDTO.getCaptcha());
        }


        return isNullOrEmpty(userDTO.getPhone()) || isNullOrEmpty(userDTO.getPassword());

    }

    *//**
     * 检查字符串是否为空字符串或者为null
     *
     * @param str 要判断的字符串
     * @return 是否为空字符串或者null
     *//*
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
*/

}
