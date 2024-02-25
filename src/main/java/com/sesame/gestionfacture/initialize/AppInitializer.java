package com.sesame.gestionfacture.initialize;

import com.sesame.gestionfacture.dto.RegisterRequest;
import com.sesame.gestionfacture.entity.Role;
import com.sesame.gestionfacture.repository.UserRepository;
import com.sesame.gestionfacture.service.impl.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AppInitializer implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(AppInitializer.class);
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Init Gestion Facture project ...");
        if (userRepository.findAll().isEmpty()) {
            logger.info("No User found. creating some users");
            RegisterRequest user = new RegisterRequest(
                                    null,
                                    "Chaabane",
                                    "Ali",
                                    "20208080",
                                    "alichaabane98@gmail.com",
                                    "123456",
                                    "123456",
                                     String.valueOf(Role.ADMIN),
                                    "29100200",
                                    true,
                                    25
                                    );
            userService.saveUser(user);
            logger.info("User initialized and created successfully");
        }
    }
}
