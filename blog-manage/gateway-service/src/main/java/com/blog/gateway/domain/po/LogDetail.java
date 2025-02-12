package com.blog.gateway.domain.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "日志详情")
public class LogDetail {
    @ApiModelProperty(value = "详情id")
    private Long id;
    @ApiModelProperty(value = "请求ip地址")
    private String ipAddress;
    @ApiModelProperty(value = "请求url地址")
    private String requestUrl;
    @ApiModelProperty(value = "请求方法")
    private String requestMethod;
    @ApiModelProperty(value = "请求参数")
    private String requestParams;
    @ApiModelProperty(value = "响应结果")
    private String responseBody;
    @ApiModelProperty(value = "请求头")
    private String requestHeaders;
    @ApiModelProperty(value = "主机名")
    private String hostName;
    @ApiModelProperty(value = "mac地址")
    private String macAddress;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}
