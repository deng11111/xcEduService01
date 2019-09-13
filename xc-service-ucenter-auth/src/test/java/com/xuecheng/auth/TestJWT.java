package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @ClassName TestJWT
 * @Author 邓元粮
 * @Date 2019/9/10 22:50
 * @Version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJWT {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Test
    public void testJWT(){
        //指定秘钥库文件
        String keystore = "xc.keystore";
        //秘钥库密码
        String keystore_password = "xuechengkeystore";
        ClassPathResource classPathResource = new ClassPathResource(keystore);
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource, keystore_password.toCharArray());
        //秘钥别名
        String alias = "xckey";
        //秘钥访问密码
        String keypass = "xuecheng";
        //获取秘钥对,公钥,私钥
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, keypass.toCharArray());
        //私钥对象
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, String> body = new HashMap<>();
        body.put("name", "deng");
        String bodyString = JSON.toJSONString(body);
        //生成jwt令牌
        Jwt jwt = JwtHelper.encode(bodyString, new RsaSigner(rsaPrivateKey));
        String token = jwt.getEncoded();
        //获取jwt令牌
        System.out.println(token);
    }

    @Test
    public void testVerify(){
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoiZGVuZyJ9.IMXImveSH1Q6JzHytkLLHkRmwxhoB6cInHE54jqMHVd9vJqIgetIzIIfKxD8uG0ieIYNVDzWEryxbQF4yPUNJpsmO_yRIqDWTJdRGD1whYs7lX45BHy_zVJYBVK3-ueg4fuOid2p448LsTCiCldkq8LZ3NIBLYMa1BSsiTA5n1K8Iud7LTzQj2QV4fT7F9A5qgCTMrK42pKq91x7tW7smRVWXuEV6BDuStbxMhUz-sVwDwqHYdfvcmkCx4Ij8y6wkadYOzhXVzJUkRgTV4rmf7-h-hqqEZld9G8BDfG_MfGhHXQbPxdo1K0RjLujIbeE-JUNRAUbUY1Ob8ypKQjB1Q";
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnASXh9oSvLRLxk901HANYM6KcYMzX8vFPnH/To2R+SrUVw1O9rEX6m1+rIaMzrEKPm12qPjVq3HMXDbRdUaJEXsB7NgGrAhepYAdJnYMizdltLdGsbfyjITUCOvzZ/QgM1M4INPMD+Ce859xse06jnOkCUzinZmasxrmgNV3Db1GtpyHIiGVUY0lSO1Frr9m5dpemylaT0BV3UwTQWVW9ljm6yR3dBncOdDENumT5tGbaDVyClV0FEB1XdSKd7VjiDCDbUAUbDTG1fm3K9sx7kO1uMGElbXLgMfboJ963HEJcU01km7BmFntqI5liyKheX+HBUCD4zbYNPw236U+7QIDAQAB-----END PUBLIC KEY-----";
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));
        //获取原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
//        JwtHelper
    }

    @Test
    public void testApplyToken(){
        ServiceInstance authServiceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        //http://ip:port
        URI authUri = authServiceInstance.getUri();
        String url = authUri + "/auth/oauth/token";
        //String url, HttpMethod method, @Nullable HttpEntity<?> requestEntity,Class<T> responseType
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type","password");
        body.add("username","itcast");
        body.add("password","123");
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String,String>();
        String authorization = getAuthorization("XcWebApp","XcWebApp");
        headers.add("Authorization", authorization);
        //处理错误请求400,401
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() !=401){
                    super.handleError(response);
                }
            }
        });
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<MultiValueMap<String, String>>(body, headers);
        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        Map resultBody = exchange.getBody();
        String jsonString = JSON.toJSONString(resultBody);
        System.out.println(jsonString);
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

    @Test
    public void testClient(){
//采用客户端负载均衡，从eureka获取认证服务的ip 和端口
        ServiceInstance serviceInstance =
                loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = serviceInstance.getUri();
        String authUrl = uri+"/auth/oauth/token";

        //URI url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType
        // url就是 申请令牌的url /oauth/token
        //method http的方法类型
        //requestEntity请求内容
        //responseType，将响应的结果生成的类型
        //请求的内容分两部分
        //1、header信息，包括了http basic认证信息
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        String httpbasic = httpbasic("XcWebApp", "XcWebApp");
        //"Basic WGNXZWJBcHA6WGNXZWJBcHA="
        headers.add("Authorization", httpbasic);
        //2、包括：grant_type、username、passowrd
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type","password");
        body.add("username","itcast");
        body.add("password","123");
        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new
                HttpEntity<MultiValueMap<String, String>>(body, headers);
        //指定 restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                //当响应的值为400或401时候也要正常响应，不要抛出异常
                if(response.getRawStatusCode()!=400 && response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });
        //远程调用申请令牌
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST,
                multiValueMapHttpEntity, Map.class);
        Map body1 = exchange.getBody();
        System.out.println(body1);
    }
    private String httpbasic(String clientId,String clientSecret){
        //将客户端id和客户端密码拼接，按“客户端id:客户端密码”
        String string = clientId+":"+clientSecret;
        //进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic "+new String(encode);
    }
}
