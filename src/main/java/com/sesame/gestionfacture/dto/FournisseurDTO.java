package com.sesame.gestionfacture.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FournisseurDTO {
    private Long id;
    private String nom;
    private String adresse;
    private String contact;
}
