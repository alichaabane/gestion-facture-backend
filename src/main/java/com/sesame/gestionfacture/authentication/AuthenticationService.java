package com.sesame.gestionfacture.authentication;


import com.sesame.gestionfacture.config.JwtService;
import com.sesame.gestionfacture.entity.Role;
import com.sesame.gestionfacture.entity.User;
import com.sesame.gestionfacture.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(User request) {
        User newUser = new User();
        newUser.setNom(request.getNom());
        newUser.setPrenom(request.getPrenom());
        newUser.setCin(request.getCin());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setTelephone(request.getTelephone());
        newUser.setAge(request.getAge());
        String roleUser = null;

        Role role ;
        if (request.getRole()==null) {
            role = Role.ADMIN;

        } else {
            roleUser = String.valueOf((request.getRole()));

            role = switch (roleUser) {
                case "ADMIN" -> Role.ADMIN;
                case "SUPERADMIN" -> Role.SUPERADMIN;
                default -> Role.ADMIN;
            };
        }

        newUser.setRole(role);
        userRepository.save(newUser);

        User savedUser= userRepository.save(newUser);
        var jwtToken=jwtService.generateToken(newUser);
        return AuthenticationResponse.builder()
                .user(savedUser)
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getCin(),
                        request.getPassword()
                )
        );
        User UserLogged  ;
        try {
            UserLogged = userRepository.findByCin(request.getCin());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        var jwtToken = jwtService.generateToken(UserLogged);
        return AuthenticationResponse.builder()
                .user(UserLogged)
                .token(jwtToken)
                .build();
    }

}

