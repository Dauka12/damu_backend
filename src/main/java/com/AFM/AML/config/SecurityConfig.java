package com.AFM.AML.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.cors().and().
                csrf().
                disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        getPublicEndpoints()
                ).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }    private static String[] getPublicEndpoints() {
        return new String[]{
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/api/aml/course/**",
                "/api/aml/course-category/all",
                "/api/aml/course/getUserCoursesNoPr",
                "/api/references/**",
                "/api/public/**", // Добавляем публичный endpoint для получения курсов по ИИН
                "/api/aml/course-category/{id}",
                "/api/aml/course-category/{id}",
                "/api/aml/course/getCourseById/**",
                "/api/aml/auth/**",
                "/api/aml/chapter/**",
                "/api/trash/**",
                "/api/checkQR/**",
                "/api/aml/course-category/delete/**",
                "/api/aml/course-category/**"
        };
    }


}
