package com.book.flaschenbook.service;

import com.book.flaschenbook.dto.LoginRequestDTO;
import com.book.flaschenbook.dto.LogoutRequestDTO;
import com.book.flaschenbook.entity.SessionDataEntity;
import com.book.flaschenbook.entity.SessionDataId;
import com.book.flaschenbook.entity.UserEntity;
import com.book.flaschenbook.model.UserModel;
import com.book.flaschenbook.repository.SessionDataRepository;
import com.book.flaschenbook.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SessionDataRepository sessionDataRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, SessionDataRepository sessionDataRepository,ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sessionDataRepository = sessionDataRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserModel register(UserModel userModel) {
        String passwordHash = passwordEncoder.encode(userModel.getPassword());
        UserEntity userEntity = modelMapper.map(userModel, UserEntity.class);
        userEntity.setPasswordHash(passwordHash);
        userRepository.save(userEntity);
        return modelMapper.map(userEntity, UserModel.class);
    }

    @Override
    public Optional<UserModel> login(LoginRequestDTO loginRequestDTO) {
        Optional<UserEntity> userEntityOpt = userRepository.findByEmail(loginRequestDTO.getEmail());

        // 사용자가 없는 경우
        if (userEntityOpt.isEmpty()) {
            return Optional.empty();
        }

        UserEntity userEntity = userEntityOpt.get();

        // 비밀번호가 틀린 경우
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), userEntity.getPasswordHash())) {
            return Optional.of(new UserModel());
        }

        userEntity.setLastLogin(LocalDateTime.now());
        userRepository.save(userEntity);

        UserModel userModel = modelMapper.map(userEntity, UserModel.class);

        // 세션 정보 생성
        String sessionId = UUID.randomUUID().toString();
        SessionDataId sessionDataId = new SessionDataId(userModel.getUserId(), sessionId);
        SessionDataEntity sessionData = new SessionDataEntity();
        sessionData.setSessionDataId(sessionDataId);
        sessionData.setSessionStart(LocalDateTime.now());

        // 세션 정보 저장
        sessionDataRepository.save(sessionData);
        userModel.setSessionId(sessionId);

        // 로그인 성공
        return Optional.of(userModel);
    }


    @Override
    public void logout(LogoutRequestDTO logoutRequestDTO) {
        int userId = logoutRequestDTO.getUserId();
        String sessionId = logoutRequestDTO.getSessionId();

        // sessionId로 SessionDataEntity 찾기
        SessionDataId sessionDataId = new SessionDataId(userId, sessionId);
        Optional<SessionDataEntity> sessionDataOpt = sessionDataRepository.findById(sessionDataId);

        if (sessionDataOpt.isPresent()) {
            SessionDataEntity sessionData = sessionDataOpt.get();
            sessionData.setSessionEnd(LocalDateTime.now());
            sessionDataRepository.save(sessionData);
        } else {
            // 세션 정보가 없는 경우 에러 처리
            throw new RuntimeException("No session found for sessionId: " + sessionId);
        }
    }


    @Override
    public UserModel updateProfile(UserModel userModel) {
        Optional<UserEntity> userEntity = userRepository.findById(userModel.getUserId());
        if (userEntity.isPresent()) {
            UserEntity existingUser = userEntity.get();
            existingUser.setUsername(userModel.getUsername());
            userRepository.save(existingUser);
            return modelMapper.map(existingUser, UserModel.class);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public void updateProfileImageUrl(int userId, String profileImageUrl) {
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        if (userEntity.isPresent()) {
            UserEntity existingUser = userEntity.get();
            existingUser.setProfileImageUrl(profileImageUrl);
            userRepository.save(existingUser);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public Optional<UserModel> getUserById(int userId) {
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        return userEntity.map(entity -> modelMapper.map(entity, UserModel.class));
    }

    @Override
    public void saveSessionData(SessionDataEntity sessionData) {
        sessionDataRepository.save(sessionData);
    }
}
