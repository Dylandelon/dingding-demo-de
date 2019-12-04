package com.demo.ading.demo.service.impl;

import com.demo.ading.demo.constant.DingCodeEnum;
import com.demo.ading.demo.constant.DingProperties;
import com.demo.ading.demo.dto.DingAccessTokenDTO;
import com.demo.ading.demo.service.DingAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DingAuthServiceImpl implements DingAuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DingProperties dingProperties;

    public DingAccessTokenDTO accessToken(){
        long starTime = System.nanoTime();
        DingAccessTokenDTO dingAccessToken = null;
        String url = dingProperties.getDingAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map map = new HashMap();
        map.put("appkey", dingProperties.getAppKey());
        map.put("appsecret", dingProperties.getAppSecret());
        ResponseEntity<DingAccessTokenDTO> response = restTemplate.getForEntity(url, DingAccessTokenDTO.class,map);
        if(response.getStatusCode().is2xxSuccessful()){
            if(response.getBody()!=null && response.getBody().getErrcode().equals(DingCodeEnum.success.getCode())){
                dingAccessToken = response.getBody();
            }
        }
        if(dingAccessToken == null){
            log.warn("[1.1.1]获取钉钉token失败,地址:{},参数:{}",url,map);
        }

        log.info("[1.1.2]获取钉钉token结束,耗时:{}ms",(System.nanoTime()-starTime)/1000);
        return dingAccessToken;

    }

}
