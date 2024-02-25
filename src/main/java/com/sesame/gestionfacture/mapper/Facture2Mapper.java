package com.sesame.gestionfacture.mapper;

import com.sesame.gestionfacture.dto.Facture2DTO;
import com.sesame.gestionfacture.dto.FactureDTO;
import com.sesame.gestionfacture.entity.Facture;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface Facture2Mapper extends GenericMapper<Facture, Facture2DTO> {
}
