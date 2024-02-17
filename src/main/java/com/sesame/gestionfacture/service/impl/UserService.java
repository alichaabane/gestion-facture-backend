package com.sesame.gestionfacture.service.impl;

import com.sesame.gestionfacture.authentication.RegisterRequest;
import com.sesame.gestionfacture.entity.Role;
import com.sesame.gestionfacture.entity.User;
import com.sesame.gestionfacture.mapper.UserMapper;
import com.sesame.gestionfacture.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public User saveUser(RegisterRequest user) {

        String roleUser = null;
        Role role = null;


        if (user.getRole() == null) {
            role = Role.ADMIN;

        } else {
            roleUser = user.getRole();

            switch (roleUser) {
                case "ADMIN":
                    role = Role.ADMIN;
                    break;

                case "SUPERADMIN":
                    role = Role.SUPERADMIN;
                    break;

                default:
                    role = Role.ADMIN;
                    break;
            }
        }
        user.setRole(role.toString());

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        return userRepository.save(userMapper.toEntity(user));
    }

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public User getUserByID(Long id) {

        return this.userRepository.findById(id).orElse(null);
    }

    public void deleteUserById(Long id) {

        this.userRepository.deleteById(id);
    }

    public User updateUser(User user) throws Exception {

        String roleUser ;
        Role role ;

        if (user.getRole()==null) {
            role = Role.ADMIN;

        } else {
            roleUser = String.valueOf((user.getRole()));

            switch (roleUser) {
                case "ADMIN":
                    role = Role.ADMIN;
                    break;
                case "SUPERADMIN":
                    role = Role.SUPERADMIN;
                    break;

                default:
                    role = Role.ADMIN;
                    break;
            }
        }
        user.setRole(role);

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        return userRepository.save(user);

    }

    public void updateResetPasswordToken(String token,String email) throws UsernameNotFoundException {
        var user=userRepository.findByEmail(email);
        if(user!=null) {
            user.setResetPasswordToken(token);
            try{
                userRepository.save(user);
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }else {
            throw new UsernameNotFoundException("Utilisateur non trouvé");
        }
    }
    public User getUserByResetPasswordToken(String resetPasswordToken){
        return userRepository.findByResetPasswordToken(resetPasswordToken);
    }

    public void updatePassword(User user, String newPassword){
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        String encodedPassword=passwordEncoder.encode(newPassword);

        user.setPassword(encodedPassword);
        user.setResetPasswordToken(null);
        try{
            userRepository.save(user);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
}
