package com.sesame.gestionfacture.repository;

import com.sesame.gestionfacture.entity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long>, JpaSpecificationExecutor<Facture> {
}
