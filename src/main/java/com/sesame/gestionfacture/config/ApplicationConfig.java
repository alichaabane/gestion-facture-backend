package com.sesame.gestionfacture.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sesame.gestionfacture.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.TimeZone;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig  {

    private final UserRepository userRepository;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        StdDateFormat stdDateFormat = new StdDateFormat();
        stdDateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Set your desired time zone
        objectMapper.setDateFormat(stdDateFormat);

        return objectMapper;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            try {
                if (useCinForAuthentication(username)) {
                    return (UserDetails) userRepository.findByCin(username);
                } else {
                    return (UserDetails) userRepository.findByEmail(username);

                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }


    private boolean useCinForAuthentication(String username) {
        return !username.contains("@");
    }




    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
