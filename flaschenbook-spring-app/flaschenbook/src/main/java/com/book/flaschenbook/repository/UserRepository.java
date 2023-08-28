package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    // 이메일로 사용자 조회
    Optional<UserEntity> findByEmail(String email);
}
