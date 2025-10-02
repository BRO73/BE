package com.example.restaurant_management.mapper;

import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "activated", expression = "java(user.isActivated())")
    UserResponse toResponse(User user);

}
