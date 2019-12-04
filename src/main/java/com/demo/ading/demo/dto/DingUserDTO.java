package com.demo.ading.demo.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DingUserDTO {

    private Integer errcode;
    private String unionid;
    private String remark;
    private String userid;
    private String isLeaderInDepts;
    private Boolean isBoss;
    private Long hiredDate;
    private Boolean isSenior;
    private String tel;
//    private String department;
    private String workPlace;
    private String email;
//    private String orderInDepts;
    private String mobile;
    private String errmsg;
    private Boolean active;
    private String avatar;
    private Boolean isAdmin;
    private Boolean isHide;
    private String jobnumber;
    private String name;
//    private String extattr;
    private String stateCode;
    private String position;
//    private String roles;

}
