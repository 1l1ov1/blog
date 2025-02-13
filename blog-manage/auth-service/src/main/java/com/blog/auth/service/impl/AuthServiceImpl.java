package com.blog.auth.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.nacos.common.utils.MD5Utils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.auth.enums.AccountStatus;
import com.blog.auth.mapper.AuthMapper;
import com.blog.auth.service.AuthService;
import com.blog.auth.utils.JwtUtil;
import com.blog.common.enums.ErrorCode;
import com.blog.common.exception.UserException;
import com.blog.common.domain.dto.UserDTO;
import com.blog.common.domain.po.User;
import com.blog.common.domain.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthServiceImpl extends ServiceImpl<AuthMapper, User> implements AuthService {
    @Override
    public UserVo login(UserDTO userDTO) {
        // 1. 校验对象是否为空
        if (checkUserDTO(userDTO)) {
            //如果说为空或者说账号密码为空
            throw new UserException(ErrorCode.USER_PASSWORD_ERROR);
        }
        // TODO 完成密码的解密
        // 如果说不空
        // 2. 先根据账号和密码看是否存在该用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.nested(wrapper -> wrapper.eq(User::getUsername, userDTO.getUsername())
                .or().eq(User::getPhone, userDTO.getPhone())
                .or().eq(User::getEmail, userDTO.getEmail()))
                .eq(User::getPassword, userDTO.getPassword());

        User user = getOne(queryWrapper);
        // 3.1 如果说不存在，就抛出错误
        if (ObjectUtil.isEmpty(user)) {
            // 如果说为null
            throw new UserException(ErrorCode.USER_NOT_EXIST);
        }
        // 3.2 如果说存在，就生成token
        String token = JwtUtil.generateToken(user.getId());
        // 4. 然后将User转成UserVo
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        // 5. 给userVo设置token
        userVo.setToken(token);
        return userVo;
    }
    @Override
    public void register(UserDTO userDTO){
        //1.注册角色，输入该有手机，邮箱，账号，密码，二次密码

        //2.检查输入的是否为空，检查该账号、手机、邮箱是否已经注册过
        if (checkUserDTO(userDTO)) {
            //如果说为空或者说账号密码为空
            throw new UserException(ErrorCode.USER_PASSWORD_ERROR);
        }
        //2.1如果输入为空或者已经注册，抛出异常
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.nested(wrapper -> wrapper.eq(User::getUsername, userDTO.getUsername())
                .or().eq(User::getPhone, userDTO.getPhone())
                .or().eq(User::getEmail, userDTO.getEmail()));

        User user = getOne(queryWrapper);
        // 3.1 如果说不存在，就抛出错误
        if (!ObjectUtil.isEmpty(user)) {
            // 如果说为ture，说明数据库中存在这个邮箱，或者，电话，账号中的一个
            throw new UserException(ErrorCode.INVALID_USER_ID);
        }
        user = new User();
        BeanUtils.copyProperties(userDTO, user);
        // TODO 加密密码
        // TODO 修改这个随机的昵称
        String uuid = UUID.randomUUID().toString();
        String nickName = uuid.replace("-", "").substring(0,15);
        user.setNickName(nickName);
        //4.将得到的user传入数据库
        save(user);
    }
    /**
     * 检查userDTO是否为空还有账号密码为空
     * @param userDTO userDTO
     * @return 返回是否为空 空为true 不空为false
     */
    public void UserUpdate(UserDTO userDTO){
        //通过传过来的user_id找到那个人
        //将得到的新user给到通过id给到那个修改的那个人
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.nested(wrapper -> wrapper.eq(User::getId, userDTO.getId()));
        User user = getOne(queryWrapper);//
        BeanUtils.copyProperties(userDTO, user);
        save(user);
    }
    private boolean checkUserDTO(UserDTO userDTO) {
        // 如果说userDTO为空
        if(ObjectUtil.isEmpty(userDTO)) {
            return false;
        }

        // 如果说不空，检查账号和密码
        String username = userDTO.getUsername();
        String phone = userDTO.getPhone();
        String email = userDTO.getEmail();
        String password = userDTO.getPassword();

        return (isNUllOrEmpty(username) ||
                isNUllOrEmpty(phone) ||
                isNUllOrEmpty(email)) &&
                isNUllOrEmpty(password);
    }

    /**
     * 检查字符串是否为空字符串或者为null
     * @param str 要判断的字符串
     * @return 是否为空字符串或者null
     */
    private boolean isNUllOrEmpty(String str) {
        return "".equals(str) || str == null;
    }
}
