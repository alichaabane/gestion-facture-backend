package com.sesame.gestionfacture.repository;

import com.sesame.gestionfacture.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
   User findByEmail(String username);

    User findByCin(String username);

    User findByResetPasswordToken(String resetPasswordToken);

}
