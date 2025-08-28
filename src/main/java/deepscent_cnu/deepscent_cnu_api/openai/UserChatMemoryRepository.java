package deepscent_cnu.deepscent_cnu_api.openai;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChatMemoryRepository extends JpaRepository<UserChatMemory, Long> {

  List<UserChatMemory> findByMemoryRecallRoundOrderByCreatedAtAsc( MemoryRecallRound memoryRecallRound);
  void deleteByMemoryRecallRound(MemoryRecallRound memoryRecallRound);

  void deleteByMemoryId(MemoryRecallRound memoryRecallRound);
}
