package deepscent_cnu.deepscent_cnu_api.auth.repository;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
}
