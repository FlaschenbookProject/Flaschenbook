package com.book.flaschenbook.repository;

import com.book.flaschenbook.entity.SessionDataEntity;
import com.book.flaschenbook.entity.SessionDataId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionDataRepository extends JpaRepository<SessionDataEntity, SessionDataId> {
}
