package com.kirisamemarisa.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/avatar/**")
                .addResourceLocations("file:D:/Projects/selfprojects/blog/sources/avatar/");
        registry.addResourceHandler("/background/**")
                .addResourceLocations("file:D:/Projects/selfprojects/blog/sources/background/");
    }
}

