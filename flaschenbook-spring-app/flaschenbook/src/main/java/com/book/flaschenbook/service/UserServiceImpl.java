package com.book.flaschenbook.service;

import com.book.flaschenbook.dto.LoginRequestDTO;
import com.book.flaschenbook.entity.UserEntity;
import com.book.flaschenbook.model.UserModel;
import com.book.flaschenbook.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
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
        if (userEntityOpt.isPresent()) {
            UserEntity userEntity = userEntityOpt.get();
            if (passwordEncoder.matches(loginRequestDTO.getPassword(), userEntity.getPasswordHash())) {
                userEntity.setLastLogin(LocalDateTime.now());
                userRepository.save(userEntity);
                return Optional.of(modelMapper.map(userEntity, UserModel.class));
            }
        }
        return Optional.empty();
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

}
