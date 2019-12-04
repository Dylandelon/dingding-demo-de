package com.demo.ading.demo.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "ding.demo")
public class DingProperties {

    private String appKey;
    private String appSecret;
    private String corpId;
    public String dingAccessToken;
    public String dingGetUserId;
    public String dingGetUser;
}
