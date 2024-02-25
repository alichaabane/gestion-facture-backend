package com.sesame.gestionfacture.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.sesame.gestionfacture.entity.Produit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FactureDTO {

    private Long id;
    private String createdAt;
    private String nomClient;
    private String prenomClient;
    private String numTelClient;
    private List<Produit> listeProduits;

}
