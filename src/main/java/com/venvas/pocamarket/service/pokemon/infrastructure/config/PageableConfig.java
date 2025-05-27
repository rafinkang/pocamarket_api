package com.venvas.pocamarket.service.pokemon.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class PageableConfig implements WebMvcConfigurer {

    private final int MAX_PAGE_SIZE = 30;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver pageResolver = new PageableHandlerMethodArgumentResolver();
        pageResolver.setMaxPageSize(MAX_PAGE_SIZE); // 최대 pageSize 제한
//        pageResolver.setOneIndexedParameters(true); // page=1부터 시작하게 할 수도 있음
        pageResolver.setFallbackPageable(PageRequest.of(0, MAX_PAGE_SIZE)); // 기본값

        resolvers.add(pageResolver);
    }
}
