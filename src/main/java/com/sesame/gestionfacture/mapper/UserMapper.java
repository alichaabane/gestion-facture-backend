package com.sesame.gestionfacture.mapper;

import com.sesame.gestionfacture.dto.RegisterRequest;
import com.sesame.gestionfacture.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends GenericMapper<User, RegisterRequest> {
}
