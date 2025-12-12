package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.RecommendationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationLogRepository extends JpaRepository<RecommendationLog, String> {

    List<RecommendationLog> findByUser_Id(Long userId);
}
