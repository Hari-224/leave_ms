package com.example.demo.dto;

import com.example.demo.model.User;
import com.example.demo.model.User.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Long managerId;
    private String password;

    public static UserDTO fromEntity(User user) {
        Long mgrId = null;
        if (user.getManager() != null) {
            mgrId = user.getManager().getId();
        }
        return new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            mgrId,
            null
        );
    }
}
