package com.sesame.gestionfacture.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private Long id_user;
    private String nom;
    private String prenom;
    private String cin;
    private String email;
    private String password;
    private String passwordText;
    private String role;
    private String telephone;
    private boolean confirmed;
    private int age;
}
