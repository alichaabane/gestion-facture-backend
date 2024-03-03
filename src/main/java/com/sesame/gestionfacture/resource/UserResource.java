package com.sesame.gestionfacture.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sesame.gestionfacture.dto.PageRequestData;
import com.sesame.gestionfacture.dto.RegisterRequest;
import com.sesame.gestionfacture.dto.ResetPasswordRequest;
import com.sesame.gestionfacture.entity.Role;
import com.sesame.gestionfacture.entity.User;
import com.sesame.gestionfacture.repository.UserRepository;
import com.sesame.gestionfacture.service.impl.UserService;
import freemarker.template.TemplateException;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/user")
public class UserResource {

    private  final UserService userService;
    private  final UserRepository userRepository;

    @Value("${origin.host}")
    private String originHost;

    public UserResource(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/count")
    public int getTotalUsers() {
        return userService.countUsers();
    }

    @GetMapping("/paginated")
    public ResponseEntity<PageRequestData<?>> getAllUsersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        PageRequestData<RegisterRequest> users = userService.getAllUsersPaginated(pageRequest);
        if(users != null){
            return new ResponseEntity<>(users, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserByID(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>("this user doesn't exist", HttpStatus.OK);
    }

    @PutMapping("/active/{id}")
    public ResponseEntity<?> toggleVisibleState(@PathVariable Long id) {
        ResponseEntity<?> responseEntity = userService.changeUserState(id);
        // Return a 404 response if changeImageVisibleState returns null
        return Objects.requireNonNullElseGet(responseEntity, () -> ResponseEntity.notFound().build()); // Return the response from changeImageVisibleState
    }

    @PostMapping("/add")
    public ResponseEntity<?> saveUser(@RequestBody RegisterRequest user) {
        try {
            User result = userService.saveUser(user);
            if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            return new ResponseEntity<>("Problem with adding user", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping(value = "/edit")
    public ResponseEntity<?> updateUser(@RequestBody RegisterRequest user) throws JsonProcessingException {
        try {
            User userToUpdate = userService.getUserByID(user.getId_user());
            userToUpdate.setCin(user.getCin());
            userToUpdate.setNom(user.getNom());
            userToUpdate.setPrenom(user.getPrenom());
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setAge(user.getAge());
            userToUpdate.setTelephone(user.getTelephone());
            userToUpdate.setRole(Role.valueOf(user.getRole()));

            if (user.getPassword() != null) {
                userToUpdate.setPassword(user.getPassword());
            }

            User result = userService.updateUser(userToUpdate);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/count-by-month")
    public ResponseEntity<List<Integer>> getUsersCountByMonth() {
        List<Integer> usersCountByMonth = userService.getUsersCountByMonth();
        return ResponseEntity.ok(usersCountByMonth);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    // forget password apis

    @PostMapping(value = "/reset", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> resetPassword(@RequestParam("email") String email, HttpServletRequest request) {


        User user = this.userRepository.findByEmail(email);

        if(user == null)
        {
            JSONObject entity = new JSONObject();
            entity.append("message", "User not found");
            return new ResponseEntity<>(entity.toString(), HttpStatus.NOT_FOUND);
        }

        // generate resetToken and update user info and prepare url for reset email

        RandomString randomString=new RandomString(45);
        String generatedToken=randomString.nextString();
        userService.updateResetPasswordToken(generatedToken,email);
        String frontendUrl = "/reset-password/" + generatedToken;
        String originHost = request.getHeader("Origin").contains(this.originHost)
                ? this.originHost : "http://localhost:4200" ;
        String resetPasswordLink = originHost + frontendUrl;

        //send reset email
        try {
            userService.sendRecoverPasswordEmail(email,resetPasswordLink);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject entity = new JSONObject();
        entity.append("message", "check your email to reset your password");
        return new ResponseEntity<>(entity.toString(), HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetNewPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        User user=userService.getUserByResetPasswordToken(resetPasswordRequest.getToken());
        if(user==null){
            return new ResponseEntity<>("Token invalid", HttpStatus.BAD_REQUEST);
        } else{
            userService.updatePassword(user,resetPasswordRequest.getNewPassword());
            JSONObject entity = new JSONObject();
            entity.append("message", "Password updated");
            return new ResponseEntity<>(entity.toString(), HttpStatus.OK);
        }
    }

//    @GetMapping(path = "/image/{fileName}", produces = IMAGE_JPEG_VALUE)
//    public byte[] getProfileImage( @PathVariable("fileName") String fileName) throws IOException {
//        return Files.readAllBytes(Paths.get("uploads/users" +FORWARD_SLASH + fileName));
//    }
}
