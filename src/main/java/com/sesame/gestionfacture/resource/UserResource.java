package com.sesame.gestionfacture.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sesame.gestionfacture.dto.PageRequestData;
import com.sesame.gestionfacture.dto.RegisterRequest;
import com.sesame.gestionfacture.entity.Role;
import com.sesame.gestionfacture.entity.User;
import com.sesame.gestionfacture.service.impl.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/user")
public class UserResource {

    private  final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
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
            User userToUpdate = userService.getUserByID(user.getId());
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
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping(path = "/image/{fileName}", produces = IMAGE_JPEG_VALUE)
//    public byte[] getProfileImage( @PathVariable("fileName") String fileName) throws IOException {
//        return Files.readAllBytes(Paths.get("uploads/users" +FORWARD_SLASH + fileName));
//    }
}
