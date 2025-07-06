package com.venvas.pocamarket.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class PageableConfig implements WebMvcConfigurer {

    private final int MAX_PAGE_SIZE = 30;
    private final int DEFAULT_PAGE_SIZE = 10;

    @Override
    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver pageResolver = new PageableHandlerMethodArgumentResolver();
        pageResolver.setMaxPageSize(MAX_PAGE_SIZE); // 최대 pageSize 제한
//        pageResolver.setOneIndexedParameters(true); // page=1부터, 권장 X
        pageResolver.setFallbackPageable(PageRequest.of(0, DEFAULT_PAGE_SIZE)); // 기본값

        resolvers.add(pageResolver);
    }
}
