package com.hivetech.kanban.util;

import com.hivetech.kanban.dto.UserResponseDTO;
import com.hivetech.kanban.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserDTOMapperUtil implements DTOMapperUtil<UserResponseDTO, User>{
    public UserResponseDTO toDTO(User user){
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setId(user.getId());
        userResponseDTO.setFirstName(user.getFirstName());
        userResponseDTO.setLastName(user.getLastName());
        userResponseDTO.setEmail(user.getEmail());

        return userResponseDTO;
    }
}
