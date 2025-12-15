package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.CastMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CastMemberRepository extends JpaRepository<CastMember,Long> {
}
