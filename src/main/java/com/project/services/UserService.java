package com.project.services;

import com.project.entities.UserEntity;
import com.project.repositories.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class UserService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepo userRepo;

    public List<UserEntity> getAllUsers() {
        return userRepo.findAll();
    }

    public boolean saveNewUser(UserEntity user) {
        try {
            user.setDate(LocalDateTime.now());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER"));
            userRepo.save(user);
            return true;
        } catch (Exception e) {
            log.error("Error occurred for {}: ", user.getUsername(), e);
            return false;
        }
    }

    public void saveUser(UserEntity user) {
        userRepo.save(user);
    }

    public UserEntity findUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public void deleteUser(ObjectId id) {
        try {
            userRepo.deleteById(id);
        } catch (Exception e) {
            log.error("Error occurred", e);
        }
    }

    public void saveAdmin(UserEntity user) {
        try {
            user.setDate(LocalDateTime.now());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER", "ADMIN"));
            userRepo.save(user);
        } catch (Exception e) {
            log.error("Error occurred for {}: ", user.getUsername(), e);
        }
    }
}
