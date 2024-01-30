package com.arkopharma.app.initialize;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AppInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("Init Gestion Facture project ...");
    }
}
