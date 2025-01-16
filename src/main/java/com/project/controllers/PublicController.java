package com.project.controllers;

import com.project.dto.ErrorResponseDTO;
import com.project.dto.LoginRequestDTO;
import com.project.dto.UserDTO;
import com.project.entities.UserEntity;
import com.project.exceptions.DuplicateUsernameException;
import com.project.repositories.UserRepo;
import com.project.services.UserDetailsServiceImpl;
import com.project.services.UserService;
import com.project.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
@Slf4j
@Tag(name = "Public APIs", description = "Accessed by all")
public class PublicController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;

    @Autowired
    public PublicController(UserService userService, AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsServiceImpl, JwtUtil jwtUtil, UserRepo userRepo) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
    }

    @Operation(summary = "Application Health Check")
    @GetMapping("/health-check")
    public String healthCheck() {
        return "Ok";
    }

    @Operation(summary = "Signup")
    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody UserDTO user) {
        try {
            UserEntity newUser = userService.saveNewUser(user);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (DuplicateUsernameException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Login")
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequestDTO user) {
        try {
            UserEntity userEntity = userRepo.findByUsername(user.getUsername());
            if (userEntity == null) {
                log.warn("Login failed: Username {} not found", user.getUsername());
                return new ResponseEntity<>(new ErrorResponseDTO("Username not found", "The provided username does not exist"), HttpStatus.NOT_FOUND);
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(user.getUsername());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        }catch (BadCredentialsException e) {
            log.warn("Login failed: Invalid credentials for username {}", user.getUsername());
            return new ResponseEntity<>(new ErrorResponseDTO("Invalid credentials", "Incorrect password or username"), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return new ResponseEntity<>(new ErrorResponseDTO("Login failed", "Unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
