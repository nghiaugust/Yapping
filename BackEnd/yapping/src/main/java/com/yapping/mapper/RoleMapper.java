package com.yapping.mapper;

import com.yapping.dto.RoleDTO;
import com.yapping.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDTO toDTO(Role role);
    Role toEntity(RoleDTO roleDTO);
}