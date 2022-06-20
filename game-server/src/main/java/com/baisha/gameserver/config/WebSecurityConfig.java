package com.baisha.gameserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private MyAuthenticationFilter myAuthenticationFilter;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().permitAll()
                .and().logout().permitAll()
//                .and()
//                .addFilter(new TokenLoginFilter(authenticationManager()))
//                .addFilter(new TokenAuthenticationFilter(authenticationManager())).httpBasic()
        ;

    }

    //配置哪些请示不拦截

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("**");
    }
}
