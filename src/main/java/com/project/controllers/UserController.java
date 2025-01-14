package com.project.controllers;

import com.project.api.response.WeatherResponse;
import com.project.entities.UserEntity;
import com.project.repositories.UserRepo;
import com.project.services.UserService;
import com.project.services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserRepo userRepo;
    private final WeatherService weatherService;

    @Autowired
    public UserController(UserService userService, UserRepo userRepo, WeatherService weatherService){
        this.userService = userService;
        this.userRepo = userRepo;
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<String> getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        WeatherResponse weatherResponse = weatherService.getWeather("Mumbai");
        String feelsLike = "";
        if (weatherResponse != null){
            feelsLike = " Weather feels like " + weatherResponse.getCurrent().getFeelslike();
        }
        return new ResponseEntity<>("Hi " + authentication.getName() + feelsLike, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<UserEntity> updateUser(@RequestBody UserEntity user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity userInDb = userService.findUserByUsername(username);

        userInDb.setUsername(user.getUsername());
        userInDb.setPassword(user.getPassword());
        userInDb.setSentimentAnalysis(user.isSentimentAnalysis());

        if (userService.saveNewUser(userInDb)) {
            return new ResponseEntity<>(userInDb, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        userService.deleteUser(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
