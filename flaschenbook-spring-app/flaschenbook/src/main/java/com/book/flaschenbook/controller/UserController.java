package com.book.flaschenbook.controller;

import com.book.flaschenbook.dto.LoginRequestDTO;
import com.book.flaschenbook.model.UserModel;
import com.book.flaschenbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserModel> register(@RequestBody UserModel userModel) {
        try {
            UserModel registeredUser = userService.register(userModel);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserModel> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Optional<UserModel> userModel = userService.login(loginRequestDTO);
        return userModel.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(401).body(null));
    }

    @PutMapping("/update")
    public ResponseEntity<UserModel> updateProfile(@RequestBody UserModel userModel) {
        try {
            UserModel updatedUser = userService.updateProfile(userModel);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/update-profile-image")
    public ResponseEntity<Void> updateProfileImageUrl(@RequestParam int userId, @RequestParam String profileImageUrl) {
        try {
            userService.updateProfileImageUrl(userId, profileImageUrl);
            return ResponseEntity.ok(null);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserModel> getUserById(@PathVariable int userId) {
        Optional<UserModel> userModel = userService.getUserById(userId);
        return userModel.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(404).body(null));
    }
}
