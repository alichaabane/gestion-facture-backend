package com.sesame.gestionfacture.service;

import com.sesame.gestionfacture.dto.PageRequestData;
import com.sesame.gestionfacture.dto.ProduitDTO;
import com.sesame.gestionfacture.dto.RegisterRequest;
import com.sesame.gestionfacture.entity.Produit;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProduitService {
    List<ProduitDTO> getAllProduits();
    List<ProduitDTO> getProduitsByFournisseur(Long fournisseurId);
    void addProduit(ProduitDTO produitDTO);
    boolean deleteProduit(Long productId);
    ProduitDTO updateProduit(ProduitDTO newProduitDTO);
    ProduitDTO getProductById(Long productId);
    PageRequestData<ProduitDTO> getAllProduitsPaginated(PageRequest pageRequest);
    int countProduits();

    List<ProduitDTO> getProduitsByFacture(Long factureId);
}
