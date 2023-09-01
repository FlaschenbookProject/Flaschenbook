package com.book.flaschenbook.controller;

import com.book.flaschenbook.dto.LoginRequestDTO;
import com.book.flaschenbook.dto.LogoutRequestDTO;
import com.book.flaschenbook.entity.SessionDataEntity;
import com.book.flaschenbook.entity.SessionDataId;
import com.book.flaschenbook.entity.UserEntity;
import com.book.flaschenbook.model.UserModel;
import com.book.flaschenbook.repository.UserRepository;
import com.book.flaschenbook.service.MyPageService;
import com.book.flaschenbook.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final MyPageService myPageService;

    public UserController(UserRepository userRepository, UserService userService, MyPageService myPageService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.myPageService = myPageService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserModel userModel) {
        Optional<UserEntity> existingUser = userRepository.findByEmail(userModel.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "이미 사용중인 이메일입니다."));
        }

        try {
            UserModel registeredUser = userService.register(userModel);
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An error occurred while registering the user"));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpSession session) {
        Optional<UserModel> userModelOpt = userService.login(loginRequestDTO);

        if (userModelOpt.orElse(null) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessage("등록된 사용자가 없습니다."));
        }

        UserModel userModel = userModelOpt.get();

        if (userModel.getUserId() == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessage("비밀번호가 틀렸습니다."));
        }

        SessionDataEntity sessionData = new SessionDataEntity();
        sessionData.setSessionDataId(new SessionDataId(userModel.getUserId(), session.getId()));
        sessionData.setSessionStart(LocalDateTime.now());

        userService.saveSessionData(sessionData);

        return ResponseEntity.ok(userModel);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDTO logoutRequestDTO) {
        userService.logout(logoutRequestDTO);
        return ResponseEntity.ok().body("Logout successful");
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
