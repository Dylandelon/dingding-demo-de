package com.demo.ading.demo.service;

import com.demo.ading.demo.dto.DingUserDTO;
import com.demo.ading.demo.dto.DingUserIdDTO;

public interface DingUserService {

    DingUserIdDTO getUserId(String access_token, String code);

    DingUserDTO getUserInfo(String access_token, String userid);
}
