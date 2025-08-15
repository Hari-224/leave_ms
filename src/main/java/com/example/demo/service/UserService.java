package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(UserDTO userDTO) {
        User user = new User();
        mapDtoToUser(userDTO, user, true);
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserDTO userDTO) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    boolean updatePassword = userDTO.getPassword() != null && !userDTO.getPassword().isEmpty();
                    mapDtoToUser(userDTO, existingUser, updatePassword);
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }

    public List<User> getUsersByManagerId(Long managerId) {
        return userRepository.findByManager_Id(managerId);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    private void mapDtoToUser(UserDTO userDTO, User user, boolean setPassword) {
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());

        if (setPassword) {
            String password = (userDTO.getPassword() == null || userDTO.getPassword().isEmpty())
                    ? "default123"
                    : userDTO.getPassword();
            user.setPasswordHash(passwordEncoder.encode(password));
        }

        if (userDTO.getManagerId() != null) {
            userRepository.findById(userDTO.getManagerId())
                    .ifPresent(user::setManager);
        } else {
            user.setManager(null);
        }
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }
}
