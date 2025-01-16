package com.project.services;

import com.project.dto.UserDTO;
import com.project.entities.JournalEntity;
import com.project.entities.UserEntity;
import com.project.exceptions.DuplicateUsernameException;
import com.project.repositories.JournalRepo;
import com.project.repositories.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserRepo userRepo;
    private final JournalRepo journalRepo;

    @Autowired
    public UserService(UserRepo userRepo, JournalRepo journalRepo) {
        this.userRepo = userRepo;
        this.journalRepo = journalRepo;
    }

    public List<UserEntity> getAllUsers() {
        return userRepo.findAll();
    }

    public UserEntity saveNewUser(UserDTO user) {
        if (userRepo.existsByUsername(user.getUsername())) {
            throw new DuplicateUsernameException("Username " + user.getUsername() + " is already taken.");
        }
        UserEntity newUser = new UserEntity();
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setCity(user.getCity());
        newUser.setUsername(user.getUsername());
        newUser.setSentimentAnalysis(user.isSentimentAnalysis());
        newUser.setEmail(user.getEmail());
        newUser.setDate(LocalDateTime.now());
        newUser.setRoles(Arrays.asList("USER"));

        return userRepo.save(newUser);
    }

    public void saveUser(UserEntity user) {
        userRepo.save(user);
    }

    public UserEntity findUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Transactional
    public void deleteUser(String username) {
        UserEntity user = userRepo.findByUsername(username);
        List<JournalEntity> all = user.getJournalEntries();
        for (JournalEntity journalEntity : all) {
            journalRepo.deleteById(journalEntity.getId());
        }
        log.info("Deleting user and their journal entries for username: {}", username);

        userRepo.deleteUserByUsername(username);
    }

    public void saveAdmin(UserDTO user) {
        if (userRepo.findByUsername(user.getUsername()) != null) {
            throw new DuplicateUsernameException("Username already exists");
        }
        UserEntity newUser = new UserEntity();
        newUser.setDate(LocalDateTime.now());
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setEmail(user.getEmail());
        newUser.setCity(user.getCity());
        newUser.setSentimentAnalysis(user.isSentimentAnalysis());
        newUser.setRoles(Arrays.asList("USER", "ADMIN"));

        userRepo.save(newUser);
    }

    public UserEntity updateUser(UserDTO userDTO, UserEntity userEntity) {
        userEntity.setUsername(ObjectUtils.defaultIfNull(userDTO.getUsername(), userEntity.getUsername()));
        userEntity.setPassword(ObjectUtils.defaultIfNull(passwordEncoder.encode(userDTO.getPassword()), userEntity.getPassword()));
        userEntity.setSentimentAnalysis(userDTO.isSentimentAnalysis());
        userEntity.setCity(ObjectUtils.defaultIfNull(userDTO.getCity(), userEntity.getCity()));
        userEntity.setEmail(ObjectUtils.defaultIfNull(userDTO.getEmail(), userEntity.getEmail()));

        return userRepo.save(userEntity);
    }
}

