package com.project.controllers;

import com.project.dto.UserDTO;
import com.project.entities.UserEntity;
import com.project.exceptions.DuplicateUsernameException;
import com.project.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin APIs")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        try {
            List<UserEntity> allUsers = userService.getAllUsers();
            if (allUsers.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while fetching users", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-admin-user")
    public ResponseEntity<HttpStatus> createAdmin(@Valid @RequestBody UserDTO user) {
        try {
            userService.saveAdmin(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (DuplicateUsernameException e) {
            log.warn("Username already exists: {}", user.getUsername());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Error occurred while creating an admin user", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
