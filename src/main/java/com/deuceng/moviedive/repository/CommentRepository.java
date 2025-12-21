package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>
{

}
