package com.xuecheng.framework.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/** Feign拦截器，实现JWT令牌在微服务之间的透传，其实透传的是header里面的用户身份标识token
 *  此时没有注入到spring容器中，由于此类在common中，所以其他类使用的时候注入进去就行了
 * @author Administrator
 * @version 1.0
 **/
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(requestAttributes!=null){
            HttpServletRequest request = requestAttributes.getRequest();
            //取出当前请求的header，找到jwt令牌
            Enumeration<String> headerNames = request.getHeaderNames();
            if(headerNames!=null){
                while (headerNames.hasMoreElements()){
                    String headerName = headerNames.nextElement();
                    String headerValue = request.getHeader(headerName);
                    // 将header向下传递
                    requestTemplate.header(headerName,headerValue);
                }
            }
        }
    }
}
