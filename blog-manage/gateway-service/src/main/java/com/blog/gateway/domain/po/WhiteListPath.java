package com.blog.gateway.domain.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "白名单路径")
public class WhiteListPath {
    @ApiModelProperty(value = "")
    private Long id;
    @ApiModelProperty(value = "白名单路径", required = true, example = "/auth/**")
    private String path;
}
