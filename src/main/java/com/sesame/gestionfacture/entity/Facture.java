package com.sesame.gestionfacture.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String createdAt;

    @Column(nullable = false)
    private String nomClient;

    @Column(nullable = false)
    private String prenomClient;

    @Column(nullable = false)
    private String numTelClient;

    @JsonIgnore
    @OneToMany()
    @JoinColumn(name = "facture_id")
    private List<Produit> listeProduits;

}