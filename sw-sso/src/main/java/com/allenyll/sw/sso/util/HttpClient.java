package com.allenyll.sw.sso.util;

import com.alibaba.fastjson.JSONObject;
import com.allenyll.sw.sso.config.RestTemplateConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;


/**
 * 封装常用的RestTemplate请求
 * @ClassName: com.allenyll.sw.sso.util.HttpClient.java
 * @Description:
 * @author: 20012055 yuleilei
 * @date:  2020/9/17 16:04
 * @version V1.0
 */
@Component
public class HttpClient {
    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestTemplateConfig restTemplateConfig;

    public HttpClient() {
        //this.restTemplate = new RestTemplate();
    }

    /**
     * 一般的GET请求，封装getForEntity接口
     * */
    public <T> ResponseEntity<T> getE(String url, Class<T> responseType, Object... uriVariables) {
        return restTemplate.getForEntity(url, responseType, uriVariables);
    }

    /**
     * 一般的GET请求
     * */
    public String getO(String url, Map<String, ?> paramMap) {
        logger.info("get-> url = " + url + " params: " + paramMap.toString());
        String s = restTemplate.getForObject(url, String.class, paramMap);
        logger.info("res->" + s);
        return s;
    }
    /**
     * 一般的GET请求，并返回header
     * */
    public String getWithHeader(String url, Map<String, ?> paramMap, HttpHeaders headers ) {
        logger.info("get-> url = " + url + " params: " + paramMap.toString());
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> results =  restTemplate.exchange(url, HttpMethod.GET, entity, String.class, paramMap);
        String s = results.getBody();
        logger.info("res->" + s);
        return s;
    }

    public ResponseEntity<String> getE2(String url, Map<String, ?> paramMap ) {
        logger.info("get-> url = " + url + " params: " + paramMap.toString());
        HttpEntity<String> entity = new HttpEntity<String>("parameters");
        ResponseEntity<String> results =  restTemplate.exchange(url, HttpMethod.GET, entity, String.class, paramMap);
        String s = results.getBody();
        logger.info("res->" + s);
        return results;
    }

    /**
     * 一般的GET请求，请求信息附带cookies
     * */
    public String getOCookie(String url, Map<String, ?> paramMap, List<String> cookies ) {
        logger.info("get-> url = " + url + " params: " + paramMap.toString() + " cookies: "+ cookies.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.COOKIE, cookies);
        //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<String> results =  restTemplate.exchange(url, HttpMethod.GET, entity, String.class, paramMap);

        String s = results.getBody();
        //String s = restTemplate.getForObject(url, String.class, paramMap);
        logger.info("res->" + s);
        return s;
    }

    /**
     * 一般的POST请求
     * */
    public String postO(String url, MultiValueMap paramMap) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(paramMap, headers);

        logger.info("post-> url = " + url + " params: " + paramMap.toString());
        String s = restTemplateConfig.restTemplate(restTemplateConfig.httpComponentsClientHttpRequestFactory()).postForObject(url, httpEntity, String.class);
        logger.info("res->" + s);
        //logger.info("res->" + JsonFormatUtil.formatJson(s));
        return s;
    }
    /**
     * 一般的POST请求，请求信息为JSONObject
     * */
    public String post_json(String url, JSONObject msg)
    {
        RestTemplate restTemplate = new RestTemplate();
        //请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        //请求体
        //封装成一个请求对象
        HttpEntity entity = new HttpEntity(msg.toJSONString(), headers);
        String result = restTemplateConfig.restTemplate(restTemplateConfig.httpComponentsClientHttpRequestFactory()).postForObject(url, entity, String.class);
        return result;
    }

    /**
     * 一般的POST请求，请求信息附带cookies
     * */
//    public String postOCookie(String url, MultiValueMap paramMap, List<String> cookies ) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.put(HttpHeaders.COOKIE,cookies);
//        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(paramMap, headers);
//
//        logger.info("post-> url = " + url + " params: " + paramMap.toString());
//        String s = restTemplateConfig.restTemplate(restTemplateConfig.httpComponentsClientHttpRequestFactory()).postForObject(url, httpEntity, String.class);
//        logger.info("res->" + JsonFormatUtil.formatJson(s));
//        return s;
//    }
}


