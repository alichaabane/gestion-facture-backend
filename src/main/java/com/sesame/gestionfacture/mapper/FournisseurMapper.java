package com.sesame.gestionfacture.mapper;

import com.sesame.gestionfacture.dto.FournisseurDTO;
import com.sesame.gestionfacture.entity.Fournisseur;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FournisseurMapper extends GenericMapper<Fournisseur, FournisseurDTO> {
}
