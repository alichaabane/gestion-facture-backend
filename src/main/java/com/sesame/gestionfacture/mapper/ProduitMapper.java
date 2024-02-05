package com.sesame.gestionfacture.mapper;

import com.sesame.gestionfacture.dto.ProduitDTO;
import com.sesame.gestionfacture.entity.Produit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProduitMapper extends GenericMapper<Produit, ProduitDTO> {

    @Mapping(source = "fournisseur.id", target = "fournisseurId")
    ProduitDTO toDto(Produit produit);

}
