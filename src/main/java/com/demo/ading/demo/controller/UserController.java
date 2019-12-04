package com.demo.ading.demo.controller;

import com.demo.ading.demo.dto.DingAccessTokenDTO;
import com.demo.ading.demo.dto.DingUserDTO;
import com.demo.ading.demo.dto.DingUserIdDTO;
import com.demo.ading.demo.service.DingAuthService;
import com.demo.ading.demo.service.DingUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/ding")
public class UserController {

    @Autowired
    private DingAuthService dingAuthService;

    @Autowired
    private DingUserService dingUserService;

    /**
     * 根据前台初始化后获取的免登授权码获取用户信息
     *
     * @param code   免登授权码
     * @param corpId 企业应用corpId
     * @return
     */
    @GetMapping("/login")
    public Map<String,Object> authCodeLogin(@RequestParam("code") String code,
                                            @RequestParam("corpId") String corpId) {
        DingAccessTokenDTO accessTokenDTO = dingAuthService.accessToken();
        DingUserIdDTO userIdDTO = dingUserService.getUserId(accessTokenDTO.getAccess_token(), code);
        DingUserDTO userInfo = dingUserService.getUserInfo(accessTokenDTO.getAccess_token(), userIdDTO.getUserid());


        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", code);
        result.put("token", accessTokenDTO.getAccess_token());

        result.put("user", userInfo);
        result.put("corpId", corpId);

        log.debug("[钉钉] 用户免登, 根据免登授权码code, corpId获取用户信息, code: {}, corpId:{}, result:{}", code, corpId, result);

        return result;
    }
}
