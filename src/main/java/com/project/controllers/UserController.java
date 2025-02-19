package com.project.controllers;

import com.project.api.response.WeatherResponse;
import com.project.dto.ErrorResponseDTO;
import com.project.dto.UserDTO;
import com.project.entities.UserEntity;
import com.project.services.UserService;
import com.project.services.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/user")
@Tag(name = "User APIs",  description = "Create, Read, Update, Delete users")
public class UserController {

    private final UserService userService;
    private final WeatherService weatherService;

    @Autowired
    public UserController(UserService userService, WeatherService weatherService) {
        this.userService = userService;
        this.weatherService = weatherService;
    }

    @Operation(summary = "Get user")
    @GetMapping
    public ResponseEntity<String> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = userService.findUserByUsername(authentication.getName());
        WeatherResponse weatherResponse = weatherService.getWeather(user.getCity() != null ? user.getCity() : "Mumbai");

        String feelsLike = "";
        if (weatherResponse != null && weatherResponse.getCurrent() != null) {
            feelsLike = " Weather feels like " + weatherResponse.getCurrent().getFeelslike();
        } else {
            feelsLike = " Weather information is unavailable.";
        }
        return new ResponseEntity<>("Hi " + authentication.getName() + feelsLike, HttpStatus.OK);
    }

    @Operation(summary = "Update user")
    @PutMapping
    public ResponseEntity<Object> updateUser(@RequestBody UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            UserEntity userEntity = userService.findUserByUsername(username);
            UserEntity updatedUser = userService.updateUser(userDTO ,userEntity);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }  catch (Exception e) {
            log.error("Error updating user: ", e);
            return new ResponseEntity<>(new ErrorResponseDTO("Update failed", "An unexpected error occurred while updating the user."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete user")
    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            userService.deleteUser(username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Unexpected error occurred during user deletion", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
