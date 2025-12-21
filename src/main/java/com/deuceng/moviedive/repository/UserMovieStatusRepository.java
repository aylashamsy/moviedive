package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.dto.DtoUserMovieStatus;
import com.deuceng.moviedive.entity.RelationTypeEnum;
import com.deuceng.moviedive.entity.UserMovieStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.management.relation.RelationType;
import java.util.List;
import java.util.Optional;

public interface UserMovieStatusRepository extends JpaRepository<UserMovieStatus, Long> {

    // user.id ve movie.id ile arama (ID ile çalışmak için en rahat)
    Optional<UserMovieStatus> findByUser_IdAndMovie_Id(Long userId, Long movieId);

    // Kullanıcının tüm status kayıtları
    List<UserMovieStatus> findAllByUser_Id(Long userId);

    // Bu user+movie ilişkisi var mı?
    boolean existsByUser_IdAndMovie_Id(Long userId, Long movieId);

    // Bu user'ın herhangi kaydı var mı?
    boolean existsByUser_Id(Long userId);

    // Bu movie için herhangi kayıt var mı? (genelde çok lazım olmaz)
    boolean existsByMovie_Id(Long movieId);

    @Query("""
        SELECT new com.deuceng.moviedive.dto.DtoUserMovieStatus(
            temp.user,
            temp.movie,
            temp.liked,
            temp.relation_type
        )
        FROM UserMovieStatus temp
        WHERE temp.user.id = :userId
          AND temp.relation_type = :relationType
    """)
    List<DtoUserMovieStatus> findByUserIdAndRelationTypeDto(
            @Param("userId") Long userId,
            @Param("relationType") RelationTypeEnum relationType
    );

        @Modifying
        @Transactional
        @Query("""
        DELETE FROM UserMovieStatus temp
        WHERE temp.user.id = :userId
          AND temp.movie.id = :movieId
          AND temp.relation_type = :relationType
    """)
        int deleteByUserAndMovieAndRelationType(
                @Param("userId") Long userId,
                @Param("movieId") Long movieId,
                @Param("relationType") RelationTypeEnum relationType
        );

    @Query("""
    SELECT CASE WHEN COUNT(temp) > 0 THEN true ELSE false END
    FROM UserMovieStatus temp
    WHERE temp.user.id = :userId
      AND temp.movie.id = :movieId
      AND temp.relation_type = :type
""")
    boolean existsByUser_IdAndMovie_IdAndRelation_type(@Param("userId") Long userId,
                         @Param("movieId") Long movieId,
                         @Param("type") RelationTypeEnum type);


}
