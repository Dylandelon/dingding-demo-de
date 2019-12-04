package com.demo.ading.demo.service.impl;

import com.demo.ading.demo.constant.DingCodeEnum;
import com.demo.ading.demo.constant.DingProperties;
import com.demo.ading.demo.dto.DingUserDTO;
import com.demo.ading.demo.dto.DingUserIdDTO;
import com.demo.ading.demo.service.DingUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DingUserServiceImpl implements DingUserService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DingProperties dingProperties;

    public DingUserIdDTO getUserId(String access_token, String code){
        long starTime = System.nanoTime();
        DingUserIdDTO res = null;
        String url = dingProperties.getDingGetUserId();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map map = new HashMap();
        map.put("access_token", access_token);
        map.put("code", code);
        ResponseEntity<DingUserIdDTO> response = restTemplate.getForEntity(url, DingUserIdDTO.class,map);
        if(response.getStatusCode().is2xxSuccessful()){
            if(response.getBody()!=null && response.getBody().getErrcode().equals(DingCodeEnum.success.getCode())){
                res = response.getBody();
            }
        }
        if(res == null){
            log.warn("[2.1.1]获取钉钉userid失败,地址:{},参数:{}",url,map);
        }

        log.info("[2.1.2]获取钉钉userid结束,耗时:{}ms",(System.nanoTime()-starTime)/1000);
        return res;

    }

    public DingUserDTO getUserInfo(String access_token, String userid){
        long starTime = System.nanoTime();
        DingUserDTO res = null;
        String url = dingProperties.getDingGetUser();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map map = new HashMap();
        map.put("access_token", access_token);
        map.put("userid", userid);
        ResponseEntity<DingUserDTO> response = restTemplate.getForEntity(url, DingUserDTO.class,map);
        if(response.getStatusCode().is2xxSuccessful()){
            if(response.getBody()!=null && response.getBody().getErrcode().equals(DingCodeEnum.success.getCode())){
                res = response.getBody();
            }
        }
        if(res == null){
            log.warn("[2.2.1]获取钉钉user失败,地址:{},参数:{}",url,map);
        }

        log.info("[2.2.2]获取钉钉user结束,耗时:{}ms",(System.nanoTime()-starTime)/1000);
        return res;

    }
}
