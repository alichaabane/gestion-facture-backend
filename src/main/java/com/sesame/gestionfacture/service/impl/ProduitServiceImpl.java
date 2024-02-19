package com.sesame.gestionfacture.service.impl;

import com.sesame.gestionfacture.dto.PageRequestData;
import com.sesame.gestionfacture.dto.ProduitDTO;
import com.sesame.gestionfacture.dto.RegisterRequest;
import com.sesame.gestionfacture.entity.Fournisseur;
import com.sesame.gestionfacture.entity.Produit;
import com.sesame.gestionfacture.mapper.ProduitMapper;
import com.sesame.gestionfacture.repository.FournisseurRepository;
import com.sesame.gestionfacture.repository.ProduitRepository;
import com.sesame.gestionfacture.service.ProduitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProduitServiceImpl implements ProduitService {

    private final Logger logger = LoggerFactory.getLogger(ProduitServiceImpl.class);

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Autowired
    private ProduitMapper produitMapper;

    @Override
    public List<ProduitDTO> getAllProduits() {
        List<Produit> produits = produitRepository.findAll();
        return produits.stream()
                .map(this::convertToDTOWithFournisseurId)
                .collect(Collectors.toList());
    }

    private ProduitDTO convertToDTOWithFournisseurId(Produit produit) {
        ProduitDTO produitDTO = produitMapper.toDto(produit);
        // Accéder à l'entité Fournisseur et récupérer son ID
        if (produit.getFournisseur() != null) {
            produitDTO.setFournisseurId(produit.getFournisseur().getId());
            produitDTO.setFournisseurNom(produit.getFournisseur().getNom());
        }
        return produitDTO;
    }

    @Override
    public List<ProduitDTO> getProduitsByFournisseur(Long fournisseurId) {
        List<Produit> produits = produitRepository.findByFournisseurId(fournisseurId);
        return produits.stream()
                .map(produitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void addProduit(ProduitDTO produitDTO) {
        Produit produit = new Produit();
        produit.setNom(produitDTO.getNom());
        produit.setDescription(produitDTO.getDescription());
        produit.setPrix(produitDTO.getPrix());
        produit.setQuantiteEnStock(produitDTO.getQuantiteEnStock());

        // Assuming that fournisseurId in ProduitDTO corresponds to Fournisseur entity
        Fournisseur fournisseur = fournisseurRepository.findById(produitDTO.getFournisseurId()).orElse(null);
        produit.setFournisseur(fournisseur);

        produitRepository.save(produit);
    }


    @Override
    public boolean deleteProduit(Long productId) {
        if (produitRepository.existsById(productId)) {
            produitRepository.deleteById(productId);
            return true;
        }
        return false;
    }

    @Override
    public ProduitDTO updateProduit(ProduitDTO newProduitDTO) {
        Produit existingProduit = produitRepository.findById(newProduitDTO.getId()).orElse(null);

        if (existingProduit != null) {
            // Update the existingProduit fields directly
            existingProduit.setNom(newProduitDTO.getNom());
            existingProduit.setDescription(newProduitDTO.getDescription());
            existingProduit.setPrix(newProduitDTO.getPrix());
            existingProduit.setQuantiteEnStock(newProduitDTO.getQuantiteEnStock());

            // Assuming that fournisseurId in ProduitDTO corresponds to Fournisseur entity
            Fournisseur fournisseur = fournisseurRepository.findById(newProduitDTO.getFournisseurId()).orElse(null);
            existingProduit.setFournisseur(fournisseur);

            produitRepository.save(existingProduit);
            return new ProduitDTO(existingProduit.getId(), existingProduit.getNom(),
                    existingProduit.getPrix(), existingProduit.getDescription(),
                    existingProduit.getQuantiteEnStock(),
                    existingProduit.getFournisseur().getId(),
                    existingProduit.getFournisseur().getNom()
                    ); // Return DTO with updated values
        }

        return null;
    }


    @Override
    public ProduitDTO getProductById(Long productId) {
        Produit produit = produitRepository.findById(productId).orElse(null);
        return (produit != null) ? produitMapper.toDto(produit) : null;
    }

    @Override
    public List<ProduitDTO> getProduitsByFacture(Long factureId) {
        return produitRepository.findByFactureId(factureId).stream().map(produitMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public PageRequestData<ProduitDTO> getAllProduitsPaginated(PageRequest pageRequest) {
        Page<Produit> produitPage = produitRepository.findAll(pageRequest);
        PageRequestData<ProduitDTO> customPageResponse = new PageRequestData<>();

        customPageResponse.setContent(produitPage.map(this::convertToDTOWithFournisseurId).getContent());
        customPageResponse.setTotalPages(produitPage.getTotalPages());
        customPageResponse.setTotalElements(produitPage.getTotalElements());
        customPageResponse.setNumber(produitPage.getNumber());
        customPageResponse.setSize(produitPage.getSize());
        logger.info("Fetching All produits of Page N° " + pageRequest.getPageNumber());
        return customPageResponse;
    }
}
