package com.book.flaschenbook.service;

import com.book.flaschenbook.model.UserModel;
import java.util.Optional;

public interface UserService {

    // 사용자 등록
    UserModel register(UserModel userModel);

    // 사용자 로그인
    Optional<UserModel> login(String email, String password);

    // 사용자 정보 업데이트
    UserModel updateProfile(UserModel userModel);

    // 사용자의 프로필 사진 URL 업데이트
    void updateProfileImageUrl(int userId, String profileImageUrl);

    // 사용자 정보 조회
    Optional<UserModel> getUserById(int userId);
}
