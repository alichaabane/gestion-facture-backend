package com.sesame.gestionfacture.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    @Column(columnDefinition = "TEXT")
    private String description;
    private double prix;
    private int quantiteEnStock;

    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;

    @ManyToOne()
    @JoinColumn(name="facture_id")
    private Facture facture;

}
