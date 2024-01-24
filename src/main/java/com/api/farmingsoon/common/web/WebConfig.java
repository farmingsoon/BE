package com.api.farmingsoon.common.web;

import com.api.farmingsoon.common.interceptor.AuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private  final AuthenticationInterceptor authenticationInterceptor;
    private final List<String> excludePointList = Arrays.asList();

    private final List<String> addEndPointList = Arrays.asList("/api/**");

    /**
     * @Description
     * Interceptor 등록
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns(addEndPointList)
               .excludePathPatterns(excludePointList);
    }
}