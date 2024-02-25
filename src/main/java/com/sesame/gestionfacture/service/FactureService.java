package com.sesame.gestionfacture.service;

import com.sesame.gestionfacture.dto.Facture2DTO;
import com.sesame.gestionfacture.dto.FactureDTO;
import com.sesame.gestionfacture.dto.PageRequestData;
import com.sesame.gestionfacture.dto.ProduitDTO;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface FactureService {

    List<FactureDTO> getAllFactures();
    byte[] addFacture(FactureDTO factureDTO);
    boolean deleteFactureById(Long id);
    int countFactures();
    byte[] generatePdf(String fileName, List<ProduitDTO> listeProduits, FactureDTO factureDTO);
    void deletePdfFile(Long factureId);
    PageRequestData<Facture2DTO> getAllFacturesPaginated(PageRequest pageRequest);


}
