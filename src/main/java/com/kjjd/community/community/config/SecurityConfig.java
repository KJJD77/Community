package com.kjjd.community.community.config;

import com.kjjd.community.community.util.CommunityConstant;
import com.kjjd.community.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import java.io.PrintWriter;

@Configuration
public class SecurityConfig implements CommunityConstant  {
    private static final Logger logger=LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/resources/**");
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(
                                "/user/setting"
//                                ,"/user/upload",
//                                "/discuss/add",
//                                "/comment/add/**",
//                                "/letter/**",
//                                "/notice/**",
//                                "/like",
//                                "/follow",
//                                "/unfollow"
                        ).hasAnyAuthority(
                                AUTHORITY_USER,
                                AUTHORITY_ADMIN,
                                AUTHORITY_MODERATOR
                        )
                        .anyRequest().permitAll()
                )
                .csrf((csrf) -> csrf.disable());
                http.exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint((request, response, e) -> {
                            String xRequestedWith = request.getHeader("x-requested-with");
                            if ("XMLHttpRequest".equals(xRequestedWith)) {
                                response.setContentType("application/plain;charset=utf-8");
                                PrintWriter writer = response.getWriter();
                                writer.write(CommunityUtil.getJSONString(403, "你还没有登录哦!"));
                            } else {
                                response.sendRedirect(request.getContextPath() + "/login");
                            }
                        })
                        .accessDeniedHandler((request, response, e) -> {
                            String xRequestedWith = request.getHeader("x-requested-with");
                            if ("XMLHttpRequest".equals(xRequestedWith)) {
                                response.setContentType("application/plain;charset=utf-8");
                                PrintWriter writer = response.getWriter();
                                writer.write(CommunityUtil.getJSONString(403, "你没有访问此功能的权限!"));
                            } else {
                                response.sendRedirect(request.getContextPath() + "/denied");
                            }
                        })
                );


                http.logout((logout) -> logout.logoutUrl("/securitylogout"));

        return http.build();
    }
}
