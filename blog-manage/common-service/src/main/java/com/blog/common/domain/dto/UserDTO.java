package com.blog.common.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
@ApiModel()
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    @ApiModelProperty("用户手机")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号校验")
    private String  phone;
    @ApiModelProperty("用户邮箱")
    @Email(message = "邮箱校验")
    private String  email;

}
