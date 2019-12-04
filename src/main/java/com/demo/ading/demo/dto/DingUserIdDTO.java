package com.demo.ading.demo.dto;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DingUserIdDTO {
    private Integer errcode;
    private String userid;
    private Integer sys_level;
    private String errmsg;
    private Boolean is_sys;
}
