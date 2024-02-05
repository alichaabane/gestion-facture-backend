package com.sesame.gestionfacture.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingTarget;

import java.util.List;

@MapperConfig
public interface GenericMapper<B, D> {

    List<D> toDtos(List<B> dtos);

    List<B> toEntitys(List<D> entitys);

    D toDto(B entity);

    B toEntity(D dto);

    B fillEntity(D dto, @MappingTarget B entity);
}