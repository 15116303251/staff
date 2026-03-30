package com.king.staff.common.config;

import com.king.staff.common.auth.AuthorizeInterceptor;
import com.king.staff.common.auth.FeignRequestHeaderInterceptor;
import feign.RequestInterceptor;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
@EnableConfigurationProperties(StaffjoyProps.class)
public class StaffjoyConfig implements WebMvcConfigurer {

    @Value("${spring.profiles.active:NA}")
    private String activeProfile;
    @Value("$spring.application.name:NA")
    private String appName;

    @Autowired
    StaffjoyProps staffjoyProps;

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public SentryClient sentryClient(){
        SentryClient sentryClient= Sentry.init(staffjoyProps.getSentryDsn());
        sentryClient.setEnvironment(activeProfile);
        sentryClient.setRelease(staffjoyProps.getDeployEnv());
        sentryClient.addTag("service",appName);
        return sentryClient;
    }

    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new AuthorizeInterceptor());
    }

    @Bean
    public RequestInterceptor feignRequestInterceptor(){
        return new FeignRequestHeaderInterceptor();
    }

    @PostConstruct
    public void init(){
        // Logging initialization - using SLF4J instead of structlog4j
        // SLF4J is automatically configured by Spring Boot
    }

    @PreDestroy
    public void destroy(){
        sentryClient().closeConnection();
    }
}
