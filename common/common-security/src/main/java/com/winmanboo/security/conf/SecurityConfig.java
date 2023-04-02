package com.winmanboo.security.conf;

import com.winmanboo.security.exception.AccessDeniedHandlerImpl;
import com.winmanboo.security.exception.AuthenticationEntryPointImpl;
import com.winmanboo.security.filter.TokenAuthenticationFilter;
import com.winmanboo.security.filter.TokenLoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
  private final UserDetailsService userDetailsService;

  private final RedisTemplate<String, Object> redisTemplate;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder())
        .and()
        .build();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(AuthenticationManager authenticationManager, HttpSecurity httpSecurity)
      throws Exception {
    return httpSecurity.csrf().disable() // 关闭 csrf 跨站请求伪造
        // 开启跨域以便前端调用接口
        .cors().and()
        .authorizeRequests()
        // 指定某些接口不需要通过验证即可访问
        .antMatchers("/admin/system/index/login").permitAll()
        // 其他所有接口都需要认证才能访问
        .anyRequest().authenticated()
        .and()
        // TokenAuthenticationFilter 方道 UsernamePasswordAuthenticationFilter 之前，
        // 这样做是为了登陆的时候去查询数据库外，其他时候都用 token 进行认证
        .addFilterBefore(new TokenAuthenticationFilter(redisTemplate), UsernamePasswordAuthenticationFilter.class)
        .addFilter(new TokenLoginFilter(authenticationManager, redisTemplate))
        .exceptionHandling()
        .authenticationEntryPoint(new AuthenticationEntryPointImpl()) // 认证异常处理类
        .accessDeniedHandler(new AccessDeniedHandlerImpl()) // 授权异常处理类
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .build();
  }

  /**
   * 配置哪些请求不拦截，排除 swagger 相关请求
   */
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web.ignoring().antMatchers("/favicon.ico",
        "/swagger-resources/**",
        "/webjars/**",
        "/v2/**",
        "/swagger-ui.html/**",
        "/doc.html");
  }
}
