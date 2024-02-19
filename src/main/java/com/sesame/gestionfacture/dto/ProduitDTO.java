package com.sesame.gestionfacture.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProduitDTO {
    private Long id;
    private String nom;
    private double prix;
    private String description;
    private int quantiteEnStock;
    private Long fournisseurId;
    private String fournisseurNom;
}
