package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.ActiveSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActiveSessionsRepository extends JpaRepository<ActiveSession, String>
{
    @Query(value = "SELECT s FROM ActiveSession s WHERE s.lastSeenAt > :cutoff ORDER BY s.lastSeenAt DESC", nativeQuery = true)
    List<ActiveSession> findOnlineSince(@Param("cutoff") LocalDateTime cutoff);

    @Modifying
    @Query("DELETE FROM ActiveSession s WHERE s.sessionId = :sessionId")
    int deleteBySessionId(@Param("sessionId") String sessionId);
}
