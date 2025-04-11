package deepscent_cnu.deepscent_cnu_api.auth.repository;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByUsername(String username);
}
