# dingding-demo-de
dingding-demo钉钉h5微应用例子
# 概述
 
钉钉在企业应用已经是一件很普遍的事情了，我这讲讲钉钉应用小程序中的h5微应用的开发。本文借鉴了lnexin的文章。主要从两个demo来阐述，第一个是通过依照钉钉官网的描述，html+java实现；第二个是使用vue+java实现。

# 实现逻辑
开发钉钉企业应用，针对企业内部应用。钉钉应用有一个非常好的特性：**当我们配置的首页地址是局域网地址，手机连接在局域网，钉钉是能够访问应用的。**
- 1.获得相应企业管理员权限
- 2.登陆钉钉开发后台，根据钉钉图文创建应用，创建成功后，可以获取到appkey和appSecret，同时在后台首页还可以获取到corpId
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191204192701831.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3N0dWR5NDAzNA==,size_16,color_FFFFFF,t_70)- 3.在创建应用的时候需要填入首页地址，这个首页地址填入我们的页面地址，这个页面中必须包含**钉钉的回调函数，用于获取code.**
# h5微应用-html+java demo

- 1.首页
index.html 页面如下,加入了钉钉的回调函数，主要用于获取钉钉传过来的code,然后传递给后台。
```
<!DOCTYPE html>
<meta charset="UTF-8">
<html>

<head>
    <title>H5微应用开发demo</title>
    <!-- 这个必须引入的啊，钉钉的前端js SDK, 使用框架的请自行参照开发文档 -->
    <script src="https://g.alicdn.com/dingding/dingtalk-jsapi/2.7.13/dingtalk.open.js"></script>
    <!-- 这个jquery 想不想引入自己决定，没什么影响 -->
    <script src="https://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"></script>
</head>

<body>
<hr>
<h1>H5微应用免登demo</h1>
<p>当前页面的url:</p>
<p id="url"></p>
<br>
<p>解析url,获取的corpID:</p>
<p id="corpId"></p>
<br>
<p>SDK初始化获取的code:</p>
<p id="code"></p>
<br>
<p>请求我们服务端,登录返回的结果:</p>
<p id="result"></p>
</body>
<script type="text/javascript">
    $(function () {
        //钉钉sdk 初始化
        // dd.ready参数为回调函数，在环境准备就绪时触发，jsapi的调用需要保证在该回调函数触发后调用，否则无效。
        dd.ready(function () {
            //获取当前网页的url
            //http://ding-web.lnexin.cn/?corpid=ding46a9582af5b7541b35c2f4657eb6378f
            var currentUrl = document.location.toString()
            $("#url").append(currentUrl)

            // 解析url中包含的corpId
            var corpId = '??';
            // var corpId = currentUrl.split("corpid=")[1];
            $("#corpId").append(corpId)

            //使用SDK 获取免登授权码
            dd.runtime.permission.requestAuthCode({
                corpId: corpId,
                onSuccess: function (result) {
                    var code = result.code;
                    $("#code").append(code)
                    //请求我们服务端的登陆地址
                    $.get("http://10.4.94.250:9000/ding/login?code=" + code + "&corpId=" + corpId, function (response) {
                        // 我们服务器返回的信息
                        // 下面代码主要是将返回结果显示出来，可以根据自己的数据结构随便写
                        for (item in response) {
                            $("#result").append("<li>" + item + ":" + response[item] + "</li>")
                        }
                        if (response.user) {
                            for (item in response.user) {
                                $("#result").append("<li>\t[user 属性] " + item + " : " + response.user[item] + "</li>")
                            }
                        }
                    });
                }
            });
        });
    })

</script>

</html>
```
- 2.后台
后台使用springboot框架，后台关键代码如下：

```
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

```

```
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

```

```
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

```
- 3.demo完整地址：
[https://github.com/Dylandelon/dingding-demo-de.git](https://github.com/Dylandelon/dingding-demo-de.git)

# vue+java demo
- 1.vue可以直接引用jsapi或者通过npm安装jsapi,本例子中遵循vue最佳时间，使用npm install dingtalk-jsapi --save 安装依赖库。
- 2.创建dingding.js，主要是用获取钉钉回调过来的code，存入session中或者直接传递给后台。

```
import * as dd from 'dingtalk-jsapi';
dd.ready(function () {
    
    var corpId = '??';
    //使用SDK 获取免登授权码
    dd.runtime.permission.requestAuthCode({
        corpId: corpId,
        onSuccess: function (result) {
            var code = result.code;
            sessionStorage.setItem('code',code)
            alert(code);
        }
    });
});

export default {
    dd
}
```
- 3. main.js中增加引入代码

```
import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import dd from '@/components/util/dingding.js'
Vue.use(ElementUI);
Vue.config.productionTip = false
Vue.prototype.dd = dd

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')

```
- 4.后台使用第一个例子中的后台，vue完成demo地址：

[https://github.com/Dylandelon/dingding-demo-vue.git](https://github.com/Dylandelon/dingding-demo-vue.git)
