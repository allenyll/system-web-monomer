package com.allenyll.sw.sso.config;

import com.allenyll.sw.sso.util.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


/**
 * RestTemplate 同时开启http和https
 * @ClassName: com.allenyll.sw.sso.config.RestTemplateConfig.java
 * @Description:
 * @author: 20012055 yuleilei
 * @date:  2020/9/17 16:00
 * @cope  from: https://blog.csdn.net/bdqx_007/article/details/100893853
 * @version V1.0
 */
@Configuration
public class RestTemplateConfig {

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                return false;
            }
            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
            }
        });
        return restTemplate;
    }

    //为了支持https 改为下面的factory
    @Bean(name = "httpsFactory")
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory()
    {
        try {
            CloseableHttpClient httpClient = HttpClientUtils.acceptsUntrustedCertsHttpClient();
            HttpComponentsClientHttpRequestFactory httpsFactory =
                    new HttpComponentsClientHttpRequestFactory(httpClient);
            httpsFactory.setReadTimeout(40000);
            httpsFactory.setConnectTimeout(40000);
            return httpsFactory;
        }
        catch (Exception e ){
            logger.info(e.getMessage());
            return  null;
        }
    }
}
