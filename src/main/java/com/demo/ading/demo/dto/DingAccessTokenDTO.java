package com.demo.ading.demo.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DingAccessTokenDTO {

    private Integer errcode;
    private String access_token;
    private String errmsg;
    private Long expires_in;
}
