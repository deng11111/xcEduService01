package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName AuthService
 * @Author 邓元粮
 * @Date 2019/9/12 7:56
 * @Version 1.0
 **/
@Service
public class AuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**客戶端id*/
    @Value("${auth.clientId}")
    private String clientId;

    /**客戶端密碼*/
    @Value("${auth.clientSecret}")
    private String clientSecret;

    /*域名**/
    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    /*域名**/
    @Value("${auth.cookieMaxAge}")
    private Integer cookieMaxAge;

    /**redis过期时间*/
    @Value("${auth.tokenValiditySeconds}")
    private Long tokenValiditySeconds;

    public LoginResult login(LoginRequest loginRequest, HttpServletResponse response) {
        if (loginRequest == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String username = loginRequest.getUsername();
        if (StringUtils.isBlank(username)) {
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }
        String password = loginRequest.getPassword();
        if (StringUtils.isBlank(password)){
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }
        AuthToken authToken =  applyToken(username,password,clientId, clientSecret);
        //数据存储到redis
        saveRedisToken(authToken.getAccess_token(), JSON.toJSONString(authToken),tokenValiditySeconds);
        //存储到cookie
        CookieUtil.addCookie(response,cookieDomain,"/","uid",authToken.getAccess_token(),cookieMaxAge,false);
        return new LoginResult(CommonCode.SUCCESS, authToken.getAccess_token());
    }

    /*
     *@Description 保存cookie到redis
     * @Author 邓元粮
     * @MethodName saveRedisToken
     * @Datetime 22:20 2019/9/12
     * @Param [authToken]
     * @return void
     **/
    private boolean saveRedisToken(String access_token,String content,long ttl) {
        //令牌key
        String name = "user_token:" + access_token;
        //令牌值
        stringRedisTemplate.boundValueOps(name).set(content,ttl, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.getExpire(name);
        return expire > 0;
    }

    /*
     *@Description 申请令牌
     * @Author 邓元粮
     * @MethodName applyToken
     * @Datetime 22:04 2019/9/12
     * @Param [username, password, clientId, clientSecret]
     * @return com.xuecheng.framework.domain.ucenter.ext.AuthToken
     **/
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        ServiceInstance authService = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        if (authService == null){
            ExceptionCast.cast(AuthCode.AUTH_SERVICE_NOT_EXISTS);
        }
        URI uri = authService.getUri();
        String authUrl = uri + "/auth/oauth/token";
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        //账号
        body.add("username",username);
        //密码
        body.add("password", password);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", getAuthorization(clientId,clientSecret));
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400 || response.getRawStatusCode() != 401){
                    super.handleError(response);
                }
            }
        });
        ResponseEntity<Map> responseEntity = restTemplate.exchange(authUrl, HttpMethod.POST, entity, Map.class);
        Map map = responseEntity.getBody();
        if(map == null ||
                map.get("access_token") == null ||
                map.get("refresh_token") == null ||
                map.get("jti") == null){//jti是jwt令牌的唯一标识作为用户身份令牌
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        AuthToken authToken = new AuthToken();
        //访问令牌(jwt)
        String jwt_token = (String) map.get("access_token");
        //刷新令牌(jwt)
        String refresh_token = (String) map.get("refresh_token");
        //jti，作为用户的身份标识
        String access_token = (String) map.get("jti");
        authToken.setJwt_token(jwt_token);
        authToken.setAccess_token(access_token);
        authToken.setRefresh_token(refresh_token);
        return authToken;
    }

    /*
     *@Description 获取Authorization 值
     * @Author 邓元粮
     * @MethodName getAuthorization
     * @Datetime 22:44 2019/9/11
     * @Param [xcWebApp, xcWebApp1]
     * @return java.lang.String
     **/
    private String getAuthorization(String clientId, String clientPass) {
        String clientInfo = clientId + ":" + clientPass;
        byte[] encodeClientInfo = Base64Utils.encode(clientInfo.getBytes());
        return "Basic "+new String(encodeClientInfo);
    }
}
