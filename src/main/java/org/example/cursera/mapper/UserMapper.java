package org.example.cursera.mapper;

import org.example.cursera.domain.dtos.UserDto;
import org.example.cursera.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User entity);
}
