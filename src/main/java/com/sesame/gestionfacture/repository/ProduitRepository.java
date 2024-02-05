package com.sesame.gestionfacture.repository;

import com.sesame.gestionfacture.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long>, JpaSpecificationExecutor<Produit> {

     List<Produit> findByFournisseurId(Long fournisseurId);
}
