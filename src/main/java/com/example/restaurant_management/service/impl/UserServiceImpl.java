package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        user.setId(id);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        // Note: This method needs to be added to UserRepository
        return Optional.empty();
    }

    @Override
    public List<User> getUsersByEmail(String email) {
        // Note: This method needs to be added to UserRepository
        return List.of();
    }

    @Override
    public Optional<User> findByUsernameAndStore_StoreName(String username, String storeName) {
        return userRepository.findByUsernameAndStore_StoreName(username, storeName);
    }
}
