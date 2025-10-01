package com.example.restaurant_management.mapper;

import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fullName", expression = "java(resolveFullName(user))")
    @Mapping(target = "activated", expression = "java(user.isActivated())")
    UserResponse toResponse(User user);

    default String resolveFullName(User user) {
        if (user == null) return null;
        if (user.getStaff() != null) return user.getStaff().getFullName();
        if (user.getCustomer() != null) return user.getCustomer().getFullName();
        return null;
    }
}
