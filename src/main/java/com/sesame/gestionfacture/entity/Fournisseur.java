package com.sesame.gestionfacture.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table
public class Fournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String adresse;
    private String contact;

    @OneToMany(mappedBy = "fournisseur")
    private List<Produit> produits = new ArrayList<>();

}
