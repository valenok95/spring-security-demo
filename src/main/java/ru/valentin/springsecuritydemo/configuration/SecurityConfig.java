package ru.valentin.springsecuritydemo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import ru.valentin.springsecuritydemo.model.Role;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  protected UserDetailsService userDetailsService() {
    InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
    manager.createUser(
        User.withUsername("user")
            .password(bCryptPasswordEncoder().encode("user"))
            .roles("USER")
            .build());
    manager.createUser(
        User.withUsername("admin")
            .password(bCryptPasswordEncoder().encode("admin"))
            .roles("USER", "ADMIN")
            .build());
    return manager;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .authorizeHttpRequests()
        .requestMatchers("/")
        .permitAll()
        .requestMatchers(HttpMethod.GET, "/api/**")
        .hasAnyRole(Role.USER.name(), Role.ADMIN.name())
        .requestMatchers(HttpMethod.POST, "/api/**")
        .hasRole(Role.ADMIN.name())
        .requestMatchers(HttpMethod.DELETE, "/api/**")
        .hasRole(Role.ADMIN.name())
        .and()
        .httpBasic();
    return http.build();
  }

  @Bean
  protected BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  public AuthenticationManager authenticationManager(
      HttpSecurity http,
      BCryptPasswordEncoder bCryptPasswordEncoder,
      UserDetailsService userDetailsService)
      throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
        .userDetailsService(userDetailsService)
        .passwordEncoder(bCryptPasswordEncoder)
        .and()
        .build();
  }
}
