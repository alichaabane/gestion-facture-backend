package com.sesame.gestionfacture.service.impl;

import com.sesame.gestionfacture.config.ContactMail;
import com.sesame.gestionfacture.dto.PageRequestData;
import com.sesame.gestionfacture.dto.RegisterRequest;
import com.sesame.gestionfacture.entity.Role;
import com.sesame.gestionfacture.entity.User;
import com.sesame.gestionfacture.mapper.UserMapper;
import com.sesame.gestionfacture.repository.UserRepository;
import com.sesame.gestionfacture.service.MailingService;
import freemarker.template.TemplateException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    @Value("${spring.mail.username}")
    public String sender;

    @Autowired
    private freemarker.template.Configuration freemarkerConfig;

    @Autowired
    private MailingService mailingService;


    public User saveUser(RegisterRequest user) {

        String roleUser = null;
        Role role = null;


        if (user.getRole() == null) {
            role = Role.ADMIN;

        } else {
            roleUser = user.getRole();

            role = switch (roleUser) {
                case "ADMIN" -> Role.ADMIN;
                case "UTILISATEUR" -> Role.UTILISATEUR;
                default -> Role.UTILISATEUR;
            };
        }
        user.setRole(role.toString());

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setPasswordText(user.getPasswordText());
        return userRepository.save(userMapper.toEntity(user));
    }

    public int countUsers() {
        return (int) userRepository.count();
    }

    public List<Integer> getUsersCountByMonth() {
        List<Object[]> result = userRepository.countUsersByMonth();
        Integer[] usersCount = new Integer[12];

        // Initialize the array with zeros
        Arrays.fill(usersCount, 0);

        // Populate the array with the retrieved data
        for (Object[] row : result) {
            int month = (int) row[0];
            int count = ((Number) row[1]).intValue();
            usersCount[month - 1] = count;
        }

        return Arrays.asList(usersCount);
    }
    public ResponseEntity<?> changeUserState(Long id) {
        User user = userRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        user.setConfirmed(!user.isConfirmed());
        // Save the updated image back to the repository
        user = userRepository.save(user);
        logger.info("User with CIN = " + user.getCin() + " change his status");
        // Return a ResponseEntity with the updated image and an HTTP status code
        return ResponseEntity.ok(userMapper.toDto(user));
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


    public PageRequestData<RegisterRequest> getAllUsersPaginated(PageRequest pageRequest) {
        Page<User> userPage = userRepository.findAll(pageRequest);
        PageRequestData<RegisterRequest> customPageResponse = new PageRequestData<>();
        customPageResponse.setContent(userPage.map(userMapper::toDto).getContent());
        customPageResponse.setTotalPages(userPage.getTotalPages());
        customPageResponse.setTotalElements(userPage.getTotalElements());
        customPageResponse.setNumber(userPage.getNumber());
        customPageResponse.setSize(userPage.getSize());
        logger.info("Fetching All users of Page N° " + pageRequest.getPageNumber());
        return customPageResponse;
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
                case "UTILISATEUR":
                    role = Role.UTILISATEUR;
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
    public void sentResetPasswordEmail(User user, String resetPasswordLink) throws IOException, TemplateException {
        ContactMail contactMail = new ContactMail();
        Map<String, Object> data = new HashMap<>();
        data.put("Username", user.getNom() + " " +  user.getPrenom());
        data.put("resetPasswordLink", resetPasswordLink);
        contactMail.setSubject("Mot de passe oublié");
        contactMail.setTo(user.getEmail().trim());
        contactMail.setFrom(sender);

        contactMail.setContent(FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfig.getTemplate("reset-password.ftl"), data));
        mailingService.sendMail(contactMail);

    }

    public void sendRecoverPasswordEmail(User user, String resetPasswordLink) throws IOException, TemplateException {

        this.sentResetPasswordEmail(user, resetPasswordLink);
    }




}
