package com.sesame.gestionfacture.service;

import com.sesame.gestionfacture.dto.FactureDTO;
import com.sesame.gestionfacture.dto.PageRequestData;
import com.sesame.gestionfacture.dto.ProduitDTO;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface FactureService {

    List<FactureDTO> getAllFactures();
    void addFacture(FactureDTO factureDTO);
    boolean deleteFactureById(Long id);
    int countFactures();
    void createPdf(String fileName,List<ProduitDTO> listeProduits,FactureDTO factureDTO);

    PageRequestData<FactureDTO> getAllFacturesPaginated(PageRequest pageRequest);


}
