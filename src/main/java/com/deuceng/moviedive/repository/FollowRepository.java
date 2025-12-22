package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.Follow;
import com.deuceng.moviedive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

    @Modifying
    @Query("""
      DELETE FROM Follow f
      WHERE f.follower.id = :followerId AND f.following.id = :followingId
    """)
    int deleteByFollowerAndFollowing(Long followerId, Long followingId);

    long countByFollower_Id(Long followerId);   // following count (kaç kişiyi takip ediyor)
    long countByFollowing_Id(Long followingId); // followers count (kaç takipçisi var)

    @Query("""
      SELECT f.following FROM Follow f
      WHERE f.follower.id = :userId
    """)
    List<User> findFollowingUsers(Long userId);

    @Query("""
      SELECT f.follower FROM Follow f
      WHERE f.following.id = :userId
    """)
    List<User> findFollowerUsers(Long userId);
}
