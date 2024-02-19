package com.sesame.gestionfacture.service;

import com.sesame.gestionfacture.dto.FactureDTO;
import com.sesame.gestionfacture.dto.ProduitDTO;

import java.util.List;

public interface FactureService {

    List<FactureDTO> getAllFactures();
    void addFacture(FactureDTO factureDTO);
    boolean deleteFactureById(Long id);
    void createPdf(String fileName,List<ProduitDTO> listeProduits,FactureDTO factureDTO);


}
