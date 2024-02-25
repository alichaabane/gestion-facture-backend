package com.sesame.gestionfacture.authentication;

import com.sesame.gestionfacture.dto.ForgotPasswordRequest;
import com.sesame.gestionfacture.dto.LoginRequest;
import com.sesame.gestionfacture.dto.RegisterRequest;
import com.sesame.gestionfacture.dto.Response;
import com.sesame.gestionfacture.entity.User;
import com.sesame.gestionfacture.exception.UserAlreadyExistsException;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

import static java.time.LocalDateTime.now;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            return ResponseEntity.ok(authenticationService.register(user));
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            return new ResponseEntity<>("An error occurred during registration", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> signin(@RequestBody LoginRequest request) {
        try {
            AuthenticationResponse response = authenticationService.login(request);
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not confirmed");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @GetMapping("/current")
    public ResponseEntity<?> currentUserName(Authentication authentication) {
        RegisterRequest user = this.authenticationService.getCurrentUser(authentication);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
    }

    @PostMapping(value = "/forget-password")
    public ResponseEntity<?> sendEmail(@RequestBody ForgotPasswordRequest forgotPasswordRequest) throws TemplateException, IOException {
        Map<String, HttpStatus> res;
        res = authenticationService.sendRecoverPasswordEmail(forgotPasswordRequest);
        int statusCode= 0;
        HttpStatus status = null;
        String key = "";
        boolean isMailExists = false;
        for(Map.Entry<String, HttpStatus> entry : res.entrySet()) {
            statusCode =  entry.getValue().value();
            status = HttpStatus.valueOf(statusCode);
            key = entry.getKey();
            isMailExists = statusCode == 200;
        }
        return ResponseEntity.ok(
                Response.builder().timeStamp(now())
                        .data(Map.of("isEmailSended", isMailExists))
                        .message(key)
                        .status(status)
                        .statusCode(statusCode)
                        .build());
    }


}

