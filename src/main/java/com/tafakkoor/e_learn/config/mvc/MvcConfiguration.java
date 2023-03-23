package com.tafakkoor.e_learn.config.mvc;

import com.tafakkoor.e_learn.config.security.interceptors.RateLimitingInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.impl.LinkedInTemplate;
import org.springframework.social.linkedin.connect.LinkedInConnectionFactory;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan("com.tafakkoor.e_learn")
public class MvcConfiguration implements WebMvcConfigurer {

    private final ApplicationContext applicationContext;
    private final Environment env;


    public MvcConfiguration(ApplicationContext applicationContext, Environment env) {
        this.applicationContext = applicationContext;
        this.env = env;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitingInterceptor());
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(this.applicationContext);
        templateResolver.setPrefix("classpath:templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.addDialect(new SpringSecurityDialect());
        return templateEngine;
    }


    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setOrder(1);
        return viewResolver;
    }

    @Bean
    public LinkedIn linkedin() {
        return new LinkedInTemplate(env.getProperty("spring.security.oauth2.client.registration.linkedin.accessToken"));
    }

    @Bean
    public OAuth2Operations oauthOperations() {
        OAuth2Operations oauthOperations = new OAuth2Template(env.getProperty("spring.security.oauth2.client.registration.linkedin.client-id"), env.getProperty("spring.security.oauth2.client.registration.linkedin.client-secret"),
                "https://www.linkedin.com/oauth/v2/authorization", "https://www.linkedin.com/oauth/v2/accessToken");
        return oauthOperations;
    }

    @Bean
    public OAuth2ConnectionFactory<LinkedIn> connectionFactory() {
        return new LinkedInConnectionFactory(env.getProperty("spring.security.oauth2.client.registration.linkedin.client-id"), env.getProperty("spring.security.oauth2.client.registration.linkedin.client-secret"));
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/*")
                .addResourceLocations("classpath:static/css/");
        registry.addResourceHandler("/js/*")
                .addResourceLocations("classpath:static/js/");

        registry.addResourceHandler("/img/*")
                .addResourceLocations("classpath:static/img/");
        registry.addResourceHandler("/scss/*")
                .addResourceLocations("classpath:static/scss/");
        registry.addResourceHandler("/jquery/*")
                .addResourceLocations("classpath:static/jquery/");
        registry.addResourceHandler("/webfonts/*")
                .addResourceLocations("classpath:static/webfonts/");
    }
}
