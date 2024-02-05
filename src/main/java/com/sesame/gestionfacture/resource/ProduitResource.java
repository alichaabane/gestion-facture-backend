package com.sesame.gestionfacture.resource;

import com.sesame.gestionfacture.dto.ProduitDTO;
import com.sesame.gestionfacture.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitResource {

    @Autowired
    private ProduitService produitService;

    @GetMapping
    public ResponseEntity<List<ProduitDTO>> getAllProduits() {
        List<ProduitDTO> produits = produitService.getAllProduits();
        return new ResponseEntity<>(produits, HttpStatus.OK);
    }

    @GetMapping("/fournisseur/{fournisseurId}")
    public ResponseEntity<List<ProduitDTO>> getProduitsByFournisseur(@PathVariable Long fournisseurId) {
        List<ProduitDTO> produits = produitService.getProduitsByFournisseur(fournisseurId);
        return new ResponseEntity<>(produits, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> addProduit(@RequestBody ProduitDTO produitDTO) {
        produitService.addProduit(produitDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long productId) {
        if (produitService.deleteProduit(productId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProduitDTO> updateProduit(@PathVariable Long productId, @RequestBody ProduitDTO newProduitDTO) {
        newProduitDTO.setId(productId);
        ProduitDTO updatedProduit = produitService.updateProduit(newProduitDTO);

        if (updatedProduit != null) {
            return new ResponseEntity<>(updatedProduit, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProduitDTO> getProductById(@PathVariable Long productId) {
        ProduitDTO produit = produitService.getProductById(productId);

        if (produit != null) {
            return new ResponseEntity<>(produit, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
