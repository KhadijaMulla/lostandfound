
package com.example.demo.config;

import com.example.demo.repository.AppUserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            @Value("${returnit.admin.username:returnitadmin}") String adminUsername,
            @Value("${returnit.admin.password:ChangeThisPassword123!}") String adminPassword) {

        UserDetails admin = User.withUsername(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        UserDetailsService databaseUsers = username ->
                appUserRepository.findByEmailIgnoreCase(username)
                        .map(appUser -> User.withUsername(appUser.getEmail())
                                .password(appUser.getPassword())
                                .roles("USER")
                                .build())
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found"));

        return username -> {
            if (username.equalsIgnoreCase(adminUsername)) {
                return admin;
            }

            return databaseUsers.loadUserByUsername(username);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()

                    .requestMatchers(HttpMethod.PATCH,
                            "/claims/*/approve",
                            "/claims/*/reject",
                            "/claims/*/complete")
                    .hasRole("ADMIN")

                    .requestMatchers(HttpMethod.POST, "/items").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/items/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/items/**").authenticated()
                    .requestMatchers(HttpMethod.POST, "/claims/**").authenticated()
                    .requestMatchers("/claims/**").authenticated()

                    .anyRequest().permitAll()
            );

        return http.build();
    }
}
