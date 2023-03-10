package com.antra.config;

import com.antra.filter.JwtAuthenticationTokenFilter;
import com.antra.handler.security.AccessDeniedHandlerImpl;
import com.antra.handler.security.AuthenticationEntryPointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    private AccessDeniedHandler accessDeniedHandler;
    private AuthenticationEntryPoint authenticationEntryPoint;


    @Autowired
    public SecurityConfig(JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter, AccessDeniedHandler accessDeniedHandler
                            , AuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //??????csrf
                .csrf().disable()
                //?????????Session??????SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // ?????????????????? ??????????????????
                .antMatchers("/login").anonymous()
                .antMatchers("/logout").authenticated()
//                .antMatchers("/link/getAllLink").authenticated()
                .antMatchers("/user/userInfo").authenticated()
                // ????????????????????????????????????????????????????????????
                .anyRequest().permitAll();

        http.logout().disable();

        // ???????????????
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        //?????????????????????
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        //????????????
        http.cors();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
