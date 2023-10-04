package com.example.takehome.config.security;

import com.example.takehome.authentication.service.impl.UserDetailServiceImpl;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    private final JwtAuthenticationFilter authenticationFilter;
    private final UserDetailServiceImpl userDetailsService;

    SecurityConfig(JwtAuthenticationFilter authenticationFilter,
                   UserDetailServiceImpl userDetailsService) {
        this.authenticationFilter = authenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterAfter(authenticationFilter, AnonymousAuthenticationFilter.class);
        http.userDetailsService(userDetailsService);

        http.authorizeRequests()
            .requestMatchers("/auth/**").permitAll()
            .anyRequest().authenticated();

        // Disable CSRF (cross site request forgery)
        http.csrf().disable();

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/actuator/**",
                                                     "/swagger-ui.html/**",
                                                     "/swagger-ui/**",
                                                     "/api-docs/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean("yamlObjectMapper")
    public ObjectMapper yamlObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public HttpMessageConverter<?> createStringHttpMessageConverter() {
        // this request message converter is used for the plain text requests
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        // we explicitly set the supported media type, as by default this converter will support both - text/plain and
        // */*. Which will match to any content type and depending of the order in which converters are defined, some
        // more specific converter may be missed if this one is placed before it and supports */* as well
        stringHttpMessageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_PLAIN));
        return stringHttpMessageConverter;
    }

    @Bean
    public Module hibernate5Module() {
        Hibernate6Module hibernate5Module = new Hibernate6Module();
        hibernate5Module.enable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);
        return hibernate5Module;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}