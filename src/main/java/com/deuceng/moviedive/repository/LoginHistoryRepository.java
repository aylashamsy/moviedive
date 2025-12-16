package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.LoginHistory;
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
    @Query(value = "update LoginHistory lhSET lh.logoutAt = CURRENT_TIMESTAMP where lh.sessionId = :sessionId and lh.logoutAt IS NULL", nativeQuery = true)
    int markLogout(@Param("sessionId") String sessionId);
}
