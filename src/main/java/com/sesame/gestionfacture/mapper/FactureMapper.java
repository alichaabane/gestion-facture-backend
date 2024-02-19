package com.sesame.gestionfacture.mapper;

import com.sesame.gestionfacture.dto.FactureDTO;
import com.sesame.gestionfacture.entity.Facture;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FactureMapper extends GenericMapper<Facture, FactureDTO> {
}
