package com.sesame.gestionfacture.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sesame.gestionfacture.entity.User;
import com.sesame.gestionfacture.service.impl.FileStorageService;
import com.sesame.gestionfacture.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserResource {

    @Autowired
    private  UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("")
    public List<User> getAllUsers() {

        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserByID(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>("this user doesn't exist", HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> saveUser(@RequestParam(name = "user") String user,
                                      @RequestParam(value = "photoProfil", required = false) MultipartFile photoProfil
    ) throws JsonProcessingException, NoSuchFieldException {

        User userToSave = new ObjectMapper().readValue(user, User.class);

        if (photoProfil != null) {
            userToSave.setPhotoProfil(fileStorageService.save(photoProfil, "users"));
        }

        User result = userService.saveUser(userToSave);
        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>("problem with adding user", HttpStatus.OK);
    }


    @PutMapping(value = "/edit")
    public ResponseEntity<?> updateUser(@RequestParam(name = "user") String user,
                                     @RequestParam(value = "photoProfil", required = false) MultipartFile photoProfil
    ) throws JsonProcessingException {

        User newUser = new ObjectMapper().readValue(user, User.class);

        User userToUpdate = userService.getUserByID(newUser.getId_user());
        userToUpdate.setCin(newUser.getCin());
        userToUpdate.setNom(newUser.getNom());
        userToUpdate.setPrenom(newUser.getPrenom());
        userToUpdate.setEmail(newUser.getEmail());
        userToUpdate.setAge(newUser.getAge());
        userToUpdate.setTelephone(newUser.getTelephone());
        userToUpdate.setRole(newUser.getRole());

        if (newUser.getPassword()!=null) {
            userToUpdate.setPassword(newUser.getPassword());
        }

        if (photoProfil != null) {
            userToUpdate.setPhotoProfil(fileStorageService.save(photoProfil, "users"));
        }

        try {
            User result = userService.updateUser(userToUpdate);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping(path = "/image/{fileName}", produces = IMAGE_JPEG_VALUE)
//    public byte[] getProfileImage( @PathVariable("fileName") String fileName) throws IOException {
//        return Files.readAllBytes(Paths.get("uploads/users" +FORWARD_SLASH + fileName));
//    }
}
