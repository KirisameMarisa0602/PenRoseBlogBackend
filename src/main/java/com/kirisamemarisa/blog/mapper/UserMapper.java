package com.kirisamemarisa.blog.mapper;

import com.kirisamemarisa.blog.dto.UserRegisterDTO;
import com.kirisamemarisa.blog.dto.UserLoginDTO;
import com.kirisamemarisa.blog.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRegisterDTO dto);
    User toUser(UserLoginDTO dto);
    UserRegisterDTO toRegisterDTO(User user);
}
