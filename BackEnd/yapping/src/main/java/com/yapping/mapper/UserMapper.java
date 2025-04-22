package com.yapping.mapper;

import com.yapping.dto.RoleDTO;
import com.yapping.dto.UserDTO;
import com.yapping.entity.User;
import com.yapping.entity.Userrole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "roles", source = "userroles", qualifiedByName = "toRoleDTOSet")
    @Mapping(target = "password", ignore = true) // Ignore password khi ánh xạ từ entity
    UserDTO toDTO(User user);

    @Mapping(target = "userroles", ignore = true)
    @Mapping(target = "passwordHash", ignore = true) // Ignore passwordHash khi ánh xạ sang entity
    User toEntity(UserDTO userDTO);

    @Named("toRoleDTOSet")
    default Set<RoleDTO> toRoleDTOSet(List<Userrole> userroles) {
        if (userroles == null) return null;
        return userroles.stream()
                .map(userrole -> RoleMapper.INSTANCE.toDTO(userrole.getRole()))
                .collect(Collectors.toSet());
    }
}