package com.book.flaschenbook.service;

import com.book.flaschenbook.dto.LoginRequestDTO;
import com.book.flaschenbook.dto.LogoutRequestDTO;
import com.book.flaschenbook.entity.SessionDataEntity;
import com.book.flaschenbook.model.UserModel;
import java.util.Optional;

public interface UserService {

    UserModel register(UserModel userModel);
    Optional<UserModel> login(LoginRequestDTO loginRequestDTO);
    void logout(LogoutRequestDTO logoutRequestDTO);
    UserModel updateProfile(UserModel userModel);
    void updateProfileImageUrl(int userId, String profileImageUrl);
    Optional<UserModel> getUserById(int userId);
    void saveSessionData(SessionDataEntity sessionData);
}
