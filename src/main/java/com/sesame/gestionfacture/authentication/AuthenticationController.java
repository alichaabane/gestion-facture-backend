package com.sesame.gestionfacture.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sesame.gestionfacture.entity.User;
import com.sesame.gestionfacture.service.impl.FileStorageService;
import com.sesame.gestionfacture.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @PostMapping("/register")
    public ResponseEntity<?> register
            (@RequestParam(name = "user") String user,
             @RequestParam(value = "photoProfil", required = false) MultipartFile photoProfil
            ) throws JsonProcessingException, NoSuchFieldException {

        try {

            User userToRegister = new ObjectMapper().readValue(user, User.class);

            if (photoProfil != null) {
                userToRegister.setPhotoProfil(fileStorageService.save(photoProfil, "users"));
            }

            return ResponseEntity.ok(authenticationService.register(userToRegister));
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login
            (@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.login(request));
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);

        }
    }

    @GetMapping(value = "/show/{id}")
    public ResponseEntity showPhotoProfil(@PathVariable("id") long id) {

        User request = this.userService.getUserByID(id);

        Resource photo = fileStorageService.load(request.getPhotoProfil(), "users");

        if (photo != null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; photoName=\"" + photo.getFilename() + "\"")
                    .body(photo);
        } else {
            return new ResponseEntity<>("Could not read the file!", HttpStatus.NOT_FOUND);
        }
    }
}

