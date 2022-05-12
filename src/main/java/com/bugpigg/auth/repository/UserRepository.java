package com.bugpigg.auth.repository;

import com.bugpigg.auth.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByOauthId(String id);
}
