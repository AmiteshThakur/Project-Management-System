package com.amiTech.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

import org.hibernate.mapping.Collection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class AppConfig {

  // with this line we authorized the Url if it starts with /api/** then is should be authenticated and any other request have the permit to access.
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .sessionManagement(Management ->
        Management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .authorizeHttpRequests(Authorize ->
        Authorize
          .requestMatchers("/api/**")
          .authenticated()
          .anyRequest()
          .permitAll()
      )
      .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
      .csrf(csrf -> csrf.disable())
      .cors(cors -> cors.configurationSource(corsConfigurationSource()));

    return http.build();
  }

//   Cors configuration is for accesssing the backend which is run on different server and frontend which is run on different server. So when we connect backend with frontend if gives Cors Error so, for that we need cors configuration.
// Cors => Cross Origin Resource Sharing.
  private CorsConfigurationSource corsConfigurationSource() {
    return new CorsConfigurationSource() {
      @Override
      public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        // TODO Auto-generated method stub
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(
          Arrays.asList(
            "http://localhost:3000", 
            "http://localhost:5173",
            "http://localhost:4200"
          )
        );
        // 3000 is for react app, 5173 is for vite app and 4200 is for Angular appplication (frontend).
        cfg.setAllowedMethods(Collections.singletonList("*"));
        //this is for allowing which methods like GET, POST, DELETE etc. methos we allow ,in this we allows all the mehtods.
        cfg.setAllowCredentials(true);
        // this above method allows the browser to include some cookies which is necessary for authentication.
        /*
         * Sure! Imagine you're building a website with Spring Boot that needs to communicate with another domain, like a frontend app hosted on a different server. If you want to include user authentication via cookies in these cross-domain requests, you'd use cfg.setAllowCredentials(true) to ensure that the browser includes the necessary cookies for authentication, allowing your server to recognize and authenticate the user.
         */
        cfg.setAllowedHeaders(Collections.singletonList("*"));
        cfg.setExposedHeaders(Arrays.asList("Authorization"));
        cfg.setMaxAge(3600L);
        return cfg;
      }
    };
  }

  @Bean
  PasswordEncoder passwordEncoder()
  {
        return new BCryptPasswordEncoder()
  }
}
