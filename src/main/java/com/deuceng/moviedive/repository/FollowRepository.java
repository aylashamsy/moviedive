package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.Follow;
import com.deuceng.moviedive.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollower_IdAndFollowing_Id(Long followerId, Long followingId);

    @Modifying
    @Transactional
    @Query("""
    DELETE FROM Follow f
    WHERE f.follower.id = :followerId
    AND f.following.id = :followingId
    """)
    int deleteByFollowerAndFollowing(@Param("followerId") Long followerId,
                                     @Param("followingId") Long followingId);


    long countByFollower_Id(Long followerId);   // following count (kaç kişiyi takip ediyor)
    long countByFollowing_Id(Long followingId); // followers count (kaç takipçisi var)

    @Query("""
        SELECT DISTINCT f.following
        FROM Follow f
        WHERE f.follower.id = :followerId
    """)
    List<User> findFollowingsAsUsers(@Param("followerId") Long followerId);


        @Query("""
        SELECT DISTINCT f.follower
        FROM Follow f
        WHERE f.following.id = :userId
    """)
        List<User> findFollowersAsUsers(@Param("userId") Long userId);
}


