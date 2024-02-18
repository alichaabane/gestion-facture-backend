package com.sesame.gestionfacture.repository;

import com.sesame.gestionfacture.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
   User findByEmail(String username);

    User findByCin(String username);

    User findByResetPasswordToken(String resetPasswordToken);

    @Query("SELECT MONTH(u.createdAt) as month, COUNT(u.id) as userCount " +
            "FROM User u " +
            "WHERE YEAR(u.createdAt) = YEAR(CURRENT_DATE) " +
            "GROUP BY MONTH(u.createdAt)")
    List<Object[]> countUsersByMonth();

}
