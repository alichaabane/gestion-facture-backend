package com.sesame.gestionfacture.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.sesame.gestionfacture.entity.Produit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FactureDTO {

    private Long id;
    private String createdAt;
    private String nomClient;
    private String prenomClient;
    private String numTelClient;
    private List<Produit> listeProduits;

    @JsonCreator
    public FactureDTO(Long id, String createdAt, String nomClient, String prenomClient, String numTelClient, List<Produit> listeProduits) {
        this.id = id;
        this.createdAt = createdAt;
        this.nomClient = nomClient;
        this.prenomClient = prenomClient;
        this.numTelClient = numTelClient;
        this.listeProduits = listeProduits;
    }

}