package com.sesame.gestionfacture.service;

import com.sesame.gestionfacture.dto.FournisseurDTO;
import com.sesame.gestionfacture.dto.PageRequestData;
import com.sesame.gestionfacture.dto.ProduitDTO;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface FournisseurService {
    List<FournisseurDTO> getAllFournisseurs();
    void addFournisseur(FournisseurDTO fournisseurDTO);
    boolean deleteFournisseur(Long fournisseurId);
    FournisseurDTO updateFournisseur(FournisseurDTO newFournisseurDTO);
    FournisseurDTO getFournisseurById(Long fournisseurId);

    PageRequestData<FournisseurDTO> getAllFournisseursPaginated(PageRequest pageRequest);
}
