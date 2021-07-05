package com.mango.log.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 注意：要去yml里面改变hystrix Feign的隔离策为strategy: SEMAPHORE FIXME:待修改为THREAD,自定义CALL
 **/
@Configuration
public class FeignConfiguration implements RequestInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(FeignConfiguration.class);

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String values = request.getHeader(name);
                requestTemplate.header(name, values);
            }
        }
//        Enumeration<String> bodyNames = request.getParameterNames();
//        StringBuilder body = new StringBuilder();
//        if (bodyNames != null) {
//            while (bodyNames.hasMoreElements()) {
//                String name = bodyNames.nextElement();
//                String values = request.getParameter(name);
//                body.append(name).append("=").append(values).append("&");
//            }
//        }
//        if (body.length() != 0) {
//            body.deleteCharAt(body.length() - 1);
//            requestTemplate.body(body.toString());
//            logger.info("feign interceptor body:{}", body.toString());
//        }
    }
}
