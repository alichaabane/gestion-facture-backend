package com.sesame.gestionfacture.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sesame.gestionfacture.entity.Produit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FactureDTO {

    private Long id;
    private LocalDateTime createdAt;
    private String nomClient;
    private String prenomClient;
    private String numTelClient;
    private String adresseClient;
    private List<Produit> listeProduits;

}
