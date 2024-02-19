package com.sesame.gestionfacture.service.impl;

import com.sesame.gestionfacture.dto.ProduitDTO;
import com.sesame.gestionfacture.entity.Produit;
import com.sesame.gestionfacture.mapper.ProduitMapper;
import com.sesame.gestionfacture.repository.ProduitRepository;
import com.sesame.gestionfacture.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProduitServiceImpl implements ProduitService {

    @Autowired
    private ProduitRepository produitRepository;

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
        Produit produit = produitMapper.toEntity(produitDTO);
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
            produitMapper.fillEntity(newProduitDTO, existingProduit);
            produitRepository.save(existingProduit);
            return produitMapper.toDto(existingProduit);
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
}
