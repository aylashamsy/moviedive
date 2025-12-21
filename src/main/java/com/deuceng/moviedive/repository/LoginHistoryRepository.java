package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.LoginHistory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long>
{
    Optional<LoginHistory> findTopBySessionIdAndLogoutAtIsNullOrderByLoginAtDesc(String sessionId);

    @Modifying
    @Transactional
    @Query(value = """
    UPDATE moviedive.login_history
    SET logout_at = CURRENT_TIMESTAMP
    WHERE session_id = :sessionId
      AND logout_at IS NULL
""", nativeQuery = true)
    int markLogout(@Param("sessionId") String sessionId);
}
