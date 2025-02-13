package com.blog.common.domain.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "用户实体")
public class User {
    @ApiModelProperty("用户id")
    private Long id;
    @ApiModelProperty("用户名字")
    private String  username;
    @ApiModelProperty("用户密码")
    private String  password;
    @ApiModelProperty("用户昵称")
    private String  nickName;
    @ApiModelProperty("用户账号状态")
    private Integer  accountStatus;
    @ApiModelProperty("用户手机")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号校验")
    private String  phone;
    @ApiModelProperty("用户邮箱")
    @Email(message = "邮箱校验")
    private String  email;
    @ApiModelProperty("用户性别")
    private String  gender;
    @ApiModelProperty("用户头像路径")
    private String avatarPath;
    @ApiModelProperty("用户创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty("用户修改时间")
    private LocalDateTime updateTime;

}
