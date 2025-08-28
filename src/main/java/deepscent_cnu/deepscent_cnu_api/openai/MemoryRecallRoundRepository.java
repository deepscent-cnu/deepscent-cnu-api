package deepscent_cnu.deepscent_cnu_api.openai;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoryRecallRoundRepository extends JpaRepository<MemoryRecallRound, Long> {
    MemoryRecallRound findByMemberAndAndRound(Member member, Long roundId);
}
