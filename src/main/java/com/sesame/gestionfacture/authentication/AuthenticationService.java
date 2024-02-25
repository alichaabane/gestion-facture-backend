package com.sesame.gestionfacture.authentication;


import com.sesame.gestionfacture.config.ContactMail;
import com.sesame.gestionfacture.config.JwtService;
import com.sesame.gestionfacture.dto.ForgotPasswordRequest;
import com.sesame.gestionfacture.dto.LoginRequest;
import com.sesame.gestionfacture.dto.RegisterRequest;
import com.sesame.gestionfacture.entity.Role;
import com.sesame.gestionfacture.entity.User;
import com.sesame.gestionfacture.exception.UserAlreadyExistsException;
import com.sesame.gestionfacture.mapper.UserMapper;
import com.sesame.gestionfacture.repository.UserRepository;
import com.sesame.gestionfacture.service.MailingService;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    @Value("${spring.mail.username}")
    public String sender;

    @Autowired
    private freemarker.template.Configuration freemarkerConfig;

    @Autowired
    private MailingService mailingService;


    public AuthenticationResponse register(User request) {
        // Check if the user already exists
        if (userRepository.findByCin(request.getCin()) != null) {
            throw new UserAlreadyExistsException("User with this CIN number already exists");
        }

        User newUser = new User();
        newUser.setNom(request.getNom());
        newUser.setPrenom(request.getPrenom());
        newUser.setCin(request.getCin());
        newUser.setEmail(request.getEmail());
        newUser.setTextPassword(request.getPassword());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setTelephone(request.getTelephone());
        newUser.setConfirmed(false);
        newUser.setAge(request.getAge());

        // Set role based on the provided role or default to UTILISATEUR
        Role role = (request.getRole() == null) ? Role.UTILISATEUR : request.getRole();
        newUser.setRole(role);

        User savedUser = userRepository.save(newUser);
        String jwtToken = jwtService.generateToken(newUser);

        return AuthenticationResponse.builder()
                .user(savedUser)
                .token(jwtToken)
                .build();
    }


    public AuthenticationResponse login(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getCin(),
                            request.getPassword()
                    )
            );

            User userLogged = userRepository.findByCin(request.getCin());

            if (userLogged == null) {
                throw new UsernameNotFoundException("User not found");
            }

            if (!userLogged.isConfirmed() && !userLogged.getRole().equals(Role.ADMIN)) {
                throw new AccessDeniedException("User not confirmed");
            }

            String jwtToken = jwtService.generateToken(userLogged);
            return AuthenticationResponse.builder()
                    .user(userLogged)
                    .token(jwtToken)
                    .build();
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password", e);
        }
    }

    public RegisterRequest getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return userMapper.toDto(user);
        }
        return  null;
    }


    public void sentResetPasswordEmail(User user) throws IOException, TemplateException, TemplateException {
        ContactMail contactMail = new ContactMail();
        Map<String, Object> data = new HashMap<>();
        data.put("Username", user.getNom() + " " +  user.getPrenom());
        data.put("Password", user.getTextPassword());
        String token = UUID.randomUUID().toString();
        contactMail.setSubject("Mot de passe oublié");
        contactMail.setTo(user.getEmail().trim());
        contactMail.setFrom(sender);
        contactMail.setContent(FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfig.getTemplate("reset-password.ftl"), data));
        mailingService.sendMail(contactMail);

    }

    public Map<String, HttpStatus> sendRecoverPasswordEmail(ForgotPasswordRequest forgotPasswordRequest) throws IOException, TemplateException {
        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail());
        System.out.println(" USER = " + user);
        Map<String, HttpStatus> result = new HashMap<>();
        if (user == null) {
            result.put("Email not found", HttpStatus.NOT_FOUND);
            return result;
        }
        result = new HashMap<>();
        this.sentResetPasswordEmail(user);
        result.put("Email sended successfully", HttpStatus.OK);
        return result;
    }

}

